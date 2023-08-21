package com.edumento.content.converters;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.edumento.content.domain.Task;
import com.edumento.content.services.ContentService;
import com.edumento.content.util.FileUtil;
import com.edumento.core.constants.ContentStatus;

import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;

@Service
public class MintInteractiveContentConverter {
  private final Logger log = LoggerFactory.getLogger(MintInteractiveContentConverter.class);
  private final List<String> REMOVE_RESOURCES = initRemovedResourcesList();
  @Autowired
  FileUtil fileUtil;

  @Autowired
  VideoConverterService videoConverterService;

  @Autowired
  ContentService contentService;

  private String NEW_VIDEO_TAG;

  private void deleteConflictContents(final File directory) throws IOException {
    final List<File> allFiles = (List<File>) FileUtils.listFiles(directory, null, true);
    // video-js.css
    for (final File file : allFiles) {
      if (REMOVE_RESOURCES.contains(file.getName().toLowerCase())) {
        FileUtils.forceDelete(file);
      }
    }
  }

  private List<String> initRemovedResourcesList() {
    final List<String> list = new ArrayList<>();
    list.add("video-js.css");
    list.add("video.js");
    return list;
  }

  private String getNewVideoHTML(
      final String videoElementId, final String newVideoPath, final String poster)
      throws IOException, URISyntaxException {
    if (NEW_VIDEO_TAG == null) {
      NEW_VIDEO_TAG = FileUtils.readFileToString(new File("/data/resources/app/NewVideoElement.html"), "utf-8");
    }
    String ret = NEW_VIDEO_TAG.replace("VIDEO_ID", videoElementId);
    ret = ret.replace("VIDEO_SRC", newVideoPath);
    ret = ret.replace(
        "VIDEO_POSTER",
        (poster == null) || poster.trim().isEmpty()
            ? "app/assets/imgs/VIDEO_POSTER.jpg"
            : poster);

    return ret;
  }

  public Path convertInteractiveContentDirectorySync(Task task)
      throws IOException, InterruptedException, URISyntaxException {
    Path uploadParentPathFromTask = fileUtil.createUploadParentPathFromTask(task);
    fileUtil.extract(fileUtil.createFilePathFromTask(task), uploadParentPathFromTask);
    final File interactiveDirectory = uploadParentPathFromTask.toFile();
    final List<File> htmlFiles = (List<File>) FileUtils.listFiles(interactiveDirectory, new String[] { "html", "htm" },
        true);
    // ----------------------
    for (final File file : htmlFiles) {
      log.debug("html file: {}", file);

      final Document doc = Jsoup.parse(file, "utf-8");
      final Elements links = doc.select("source[type='video/mp4']");
      for (final Element element : links) {
        log.debug("element --> ", element);
        final File videoFile = new File(file.getParent() + "/" + element.attr("src"));

        if (!videoFile.exists()) {
          continue;
        }
        FFprobe ffprobe = new FFprobe();
        FFmpegProbeResult ffmpegProbeResult = ffprobe.probe(videoFile.toPath().toString());
        if (ffmpegProbeResult.getStreams() == null || ffmpegProbeResult.getStreams().size() != 2) {
          continue;
        }

        final int index = videoFile.toString().lastIndexOf(".");

        final File videoDirectory = new File(videoFile.toString().substring(0, index));
        File newVideoFile = new File(videoDirectory + "/" + videoFile.getName());

        if (newVideoFile.exists()) {
          FileUtils.forceDelete(newVideoFile);
        }

        FileUtils.moveFile(videoFile, newVideoFile);
        // -------------------------------------
        log.debug(">>>>>>>>> path: {}", newVideoFile);
        newVideoFile = videoConverterService.convertToMp4(newVideoFile.toPath(), "video").toFile();
        videoConverterService.extractAndPackage(newVideoFile.toPath(), null, null);
        // -------------------------------------
        final Element videoElement = element.parent();
        final String videoElementId = videoElement.id();
        final String poster = videoElement.attr("poster");

        final int ind = element.attr("src").lastIndexOf(".");
        final String newVideoPath = element.attr("src").substring(0, ind) + "/" + "master.m3u8";
        // -------------------------------------
        videoElement.parent().append(getNewVideoHTML(videoElementId, newVideoPath, poster));
        videoElement.remove();
        // -------------------------------------
      }
      FileUtils.write(file, doc.toString(), "utf-8", false);
      // -------------------------------------

      FileUtils.copyDirectory(
          new File("/data/resources/app/assets"), new File(file.getParent() + "/assets"));
      // -------------------------------------
    }
    // ----------------------
    deleteConflictContents(interactiveDirectory);
    // ----------------------
    // ----------------------
    // ----------------------

    contentService.updateContentStatus(task.getContentId(), ContentStatus.READY);
    log.info("end commit");
    return uploadParentPathFromTask;
  }

  @Async
  public CompletableFuture<Path> convertInteractiveContentDirectory(Task task)
      throws IOException, InterruptedException, URISyntaxException {
    return CompletableFuture.completedFuture(convertInteractiveContentDirectorySync(task));
  }
}
