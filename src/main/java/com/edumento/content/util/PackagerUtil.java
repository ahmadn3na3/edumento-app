package com.edumento.content.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edumento.core.exception.InvalidException;

public final class PackagerUtil {
	private static final Logger log = LoggerFactory.getLogger(PackagerUtil.class);

	private PackagerUtil() {
	}

	public static void packageFiles(final File source, final Collection<File> files, String keyId, String key)
			throws IOException, InterruptedException {
		log.debug("PackagerUtil.packageFiles()");
		final String dir = source.getParentFile().toString();

		Path videoPath = Files.createDirectory(Paths.get(dir, "video"));
		Path audioPath = Files.createDirectory(Paths.get(dir, "audio"));
		final StringBuilder packagerBuilder = new StringBuilder();
		packagerBuilder.append("packager \\").append('\n').append("'input=").append(source.getName());
		if (key != null && keyId != null) {
			packagerBuilder.append(
					"',stream=audio,init_segment='audio/audio.mp4',segment_template='audio/audio-$Number$.mint' \\")
					.append('\n');
			for (final File file : files) {
				if (file != null) {
					packagerBuilder.append("input='").append(file.getName())
							.append("',stream=video,init_segment='video/").append(file.getName())
							.append("',segment_template='video/").append(file.getName().replace(".mp4", ""))
							.append("-$Number$.mint' \\").append('\n');
				}
			}
			packagerBuilder.append("--mpd_output 'play.mint.mpd' \\").append('\n');
			packagerBuilder.append(" --enable_fixed_key_encryption \\").append('\n');
			packagerBuilder.append(" --key_id '").append(keyId).append("' \\").append('\n');
			packagerBuilder.append(" --key '").append(key).append("' \\").append('\n');
			packagerBuilder.append(" --generate_static_mpd \\").append('\n');
		} else {
			packagerBuilder.append(",stream=audio,segment_template=")
					.append("audio$Number$.ts,playlist_name=audio-playlist.m3u8' \\").append('\n');
			for (final File file : files) {
				if (file != null) {
					packagerBuilder.append("'input=").append(file.getName()).append(",stream=video,segment_template=")
							.append(file.getName()).append("-$Number$.ts,playlist_name=").append(file.getName())
							.append("-playlist.m3u8' \\").append('\n');
				}
			}
			packagerBuilder.append("--hls_master_playlist_output=").append(dir).append("/").append("master.m3u8");
		}

		// --------------------------------------------
		log.debug(packagerBuilder.toString());
		File fileSh = new File(dir, "packager.sh");
		fileSh.setExecutable(true, true);
		fileSh.setReadable(true, true);
		fileSh.setWritable(true, true);
		fileSh.createNewFile();
		FileWriter fileWriter = new FileWriter(fileSh);
		fileWriter.append(packagerBuilder.toString());
		fileWriter.flush();
		fileWriter.close();
		Process process = Runtime.getRuntime().exec("sh " + fileSh.getAbsolutePath(), new String[] {}, new File(dir));
		process.waitFor();
		log.debug("process exit value ==> {}", process.exitValue());
		log.debug("process output stream ==> {}", process.getOutputStream());
		files.add(fileSh);
		// --------------------------------------------
		for (final File file1 : files) {
			Files.delete(file1.toPath());
		}

		if (process.exitValue() != 0) {
			BufferedReader inputStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			inputStream.lines().forEach(s -> log.error("errror in packager == {}", s));
			throw new InvalidException("Packager did not finish");
		}
		// --------------------------------------------
	}
}
