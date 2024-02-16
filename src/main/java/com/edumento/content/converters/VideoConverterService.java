package com.edumento.content.converters;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.GeneralSecurityException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.edumento.content.domain.Task;
import com.edumento.content.services.ContentService;
import com.edumento.content.util.EncryptionKeysGenerator;
import com.edumento.content.util.FileUtil;
import com.edumento.content.util.PackagerUtil;
import com.edumento.core.constants.ContentStatus;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.job.FFmpegJob;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;

/** Created by ahmad on 6/15/17. */
@Service
public class VideoConverterService {
  private static final Logger log = LoggerFactory.getLogger(VideoConverterService.class);

  @Value("${mint.ffmpeg}")
  private String pathToffmpeg;

  @Value("${mint.ffprobe}")
  private String pathToffprobe;

  @Value("${mint.encryption.enabled:false}")
  private boolean encryptionEnabled;

  @Autowired private FileUtil fileUtil;

  @Autowired private ContentService contentService;

  public Path convertToMp4(Path fileToConvertPath, String fileName) throws IOException {
    Path outPutPath = Paths.get(fileToConvertPath.getParent().toString(), fileName + ".mp4");
    FFmpeg ffmpeg = new FFmpeg(pathToffmpeg);
    FFprobe ffprobe = new FFprobe(pathToffprobe);
    FFmpegBuilder builder = new FFmpegBuilder();
    builder
        .addInput(fileToConvertPath.toString())
        .addInput(Paths.get(fileUtil.getImgPath(), "watermark.png").toString())
        .overrideOutputFiles(true)
        .addOutput(outPutPath.toString())
        .setVideoCodec("libx264") // Video
        // using
        // x264
        .setAudioCodec("aac") // using the aac codec
        .setComplexVideoFilter("overlay=W-w-5:5")
        .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL)
        .done();
    FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
    FFmpegJob fFmpegJob = executor.createJob(builder);
    fFmpegJob.run();
    while (fFmpegJob.getState() == FFmpegJob.State.RUNNING) {
      continue;
    }

    return outPutPath;
  }

  public Path createFileWithQuality(Path orginalFile, int width) throws IOException {
    Path outPutPath = Paths.get(orginalFile.getParent().toString(), width + ".mp4");
    FFmpeg ffmpeg = new FFmpeg(pathToffmpeg);
    FFprobe ffprobe = new FFprobe(pathToffprobe);
    FFmpegBuilder builder = new FFmpegBuilder();
    builder
        .addInput(orginalFile.toString())
        .overrideOutputFiles(true)
        .addOutput(outPutPath.toString())
        .setVideoCodec("libx264") // Video using x264
        .setVideoFilter("scale='" + width + ":trunc(ow/a/2)*2'") // using the aac codec
        .setAudioCodec("copy")
        .done();
    FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
    FFmpegJob fFmpegJob = executor.createJob(builder);
    fFmpegJob.run();
    while (fFmpegJob.getState() == FFmpegJob.State.RUNNING) {
      continue;
    }
    return outPutPath;
  }

  public Path extractAndConvertSync(Task task)
      throws IOException, ExecutionException, InterruptedException {
    Path mp4Path = fileUtil.createFilePathFromTask(task);
    Path mp4PathOriginal = mp4Path;
    mp4Path = convertToMp4(mp4PathOriginal, task.getFileName() + "_h264_watermark");
    if (encryptionEnabled) {
      Path orgFolder =
          Files.createDirectory(
              Paths.get(fileUtil.createUploadParentPathFromTask(task).toString(), "original"));
      mp4PathOriginal =
          Files.move(
              mp4PathOriginal,
              Paths.get(
                  orgFolder.toString(), String.format("%s.%s", task.getFileName(), task.getExt())),
              StandardCopyOption.REPLACE_EXISTING);
    }
    String keyId =
        encryptionEnabled ? EncryptionKeysGenerator.getVideoKeyId(mp4PathOriginal.toFile()) : null;
    String key =
        encryptionEnabled ? EncryptionKeysGenerator.getVideoKey(mp4PathOriginal.toFile()) : null;
    log.info("key is {}, key Id is {}", key, keyId);
    if (!encryptionEnabled) {
      log.warn("encryption disabled");
    }

    extractAndPackage(mp4Path, keyId, key);
    File out = mp4Path.toFile();
    if (key != null && keyId != null) {
      out =
          Paths.get(
                  mp4Path.getParent().toString(), String.format("%s.%s", task.getFileName(), "mp4"))
              .toFile();
      if (out.exists()) {
        fileUtil.deleteFile(out.toPath());
      }
      Files.createFile(out.toPath());
      encryptVideo(mp4Path.toFile(), out, keyId.substring(0, 16), key);
      Files.delete(mp4Path);
    }
    contentService.updateContentStatus(
        task.getContentId(),
        ContentStatus.READY,
        key,
        keyId,
        encryptionEnabled ? "mp4" : task.getExt(),
        null,
        mp4PathOriginal.toString());
    return out.toPath();
  }

  @Async
  public CompletableFuture<Path> extractAndConvert(Task task)
      throws IOException, ExecutionException, InterruptedException {
    return CompletableFuture.completedFuture(extractAndConvertSync(task));
  }

  public void extractAndPackage(Path mp4Path, String keyId, String key)
      throws IOException, InterruptedException {
    Set<File> files = new HashSet<>();
    FFprobe ffprobe = new FFprobe(pathToffprobe);
    FFmpegProbeResult ffmpegProbeResult = ffprobe.probe(mp4Path.toString());
    FFmpegStream videoStream = ffmpegProbeResult.getStreams().get(0);
    int maxWidth = videoStream.width;

    if (maxWidth > 1920) {
      files.add(createFileWithQuality(mp4Path, 1920).toFile());
    }
    // -------
    if (maxWidth > 1280) {
      files.add(createFileWithQuality(mp4Path, 1280).toFile());
    }
    // -------
    if (maxWidth > 1024) {
      files.add(createFileWithQuality(mp4Path, 1024).toFile());
    }
    // -------
    if (maxWidth > 768) {
      files.add(createFileWithQuality(mp4Path, 768).toFile());
    }
    // -------
    if (maxWidth > 512) {
      files.add(createFileWithQuality(mp4Path, 512).toFile());
    }
    // -------
    if (maxWidth > 256) {
      files.add(createFileWithQuality(mp4Path, 256).toFile());
    }
    files.add(createFileWithQuality(mp4Path, maxWidth).toFile());

    PackagerUtil.packageFiles(mp4Path.toFile(), files, keyId, key);
  }

  private void encryptVideo(
      final File sourceFile, final File ourFile, final String keyId, final String key) {

    final String AES_ALGORITHM = "AES";
    final String AES_TRANSFORMATION = "AES/CTR/NoPadding";
    final byte[] buffer = new byte[1024 * 1024];
    Cipher mCipher;
    SecretKeySpec mSecretKeySpec;
    IvParameterSpec mIvParameterSpec;

    final byte[] byteKey = key.getBytes();
    final byte[] byteIv = keyId.getBytes();

    mSecretKeySpec = new SecretKeySpec(byteKey, AES_ALGORITHM);
    mIvParameterSpec = new IvParameterSpec(byteIv);

    try {
      mCipher = Cipher.getInstance(AES_TRANSFORMATION);
      mCipher.init(Cipher.ENCRYPT_MODE, mSecretKeySpec, mIvParameterSpec);
    } catch (GeneralSecurityException e) {
      log.error("error in chipher init", e);
      throw new RuntimeException(e.getMessage(), e);
    }

    try (InputStream inputStream = new FileInputStream(sourceFile);
        CipherOutputStream cipherOutputStream =
            new CipherOutputStream(new FileOutputStream(ourFile), mCipher)) {
      int bytesRead;
      while ((bytesRead = inputStream.read(buffer)) != -1) {
        cipherOutputStream.write(buffer, 0, bytesRead);
      }

    } catch (IOException e) {
      log.error("error in fileWrite", e);
      throw new RuntimeException(e.getMessage(), e);
    }
  }
}
