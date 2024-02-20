package com.edumento.content.converters;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.edumento.content.domain.Task;
import com.edumento.content.util.FileUtil;

import jakarta.transaction.Transactional;

@Service
public class MintH5PInteractiveContentConverter {
	private final Logger log = LoggerFactory.getLogger(MintH5PInteractiveContentConverter.class);

	@Autowired
	FileUtil fileUtil;

	@Transactional
	public Path convertH5PInteractiveContentDirectorySync(Task task)
			throws IOException, InterruptedException, URISyntaxException {
		try {
			log.info("start convert H5P Interactive Content");
			var uploadParentPathFromTask = fileUtil.createUploadParentPathFromTask(task);
			// rename .h5p to .zip
			var path = fileUtil.createFilePathFromTask(task);
			var pathZIP = Paths.get(path.getParent().toString(), task.getFileName() + ".zip");
			var ExtractPath = Paths.get(uploadParentPathFromTask + File.separator + "content");
			path.toFile().renameTo(pathZIP.toFile());
			log.info("extract H5P Interactive Content");
			fileUtil.extractZIP(pathZIP, ExtractPath);

			// copy config files to make h5p like normal zip file and open with index.html
			log.info("copy config files to make h5p like normal zip file");
			FileUtils.copyDirectory(new File("/data/resources/h5p-config"),
					new File(uploadParentPathFromTask.toString()));
			// delete old zip file and then compress again after add config files
			Files.delete(pathZIP);
			// compress again
			log.info("compress again" + pathZIP);
			fileUtil.zipDirectory(uploadParentPathFromTask.toFile(), pathZIP.toFile());

			log.info("end commit");
			return uploadParentPathFromTask;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Async
	public CompletableFuture<Path> convertH5PInteractiveContentDirectory(Task task)
			throws IOException, InterruptedException, URISyntaxException {
		return CompletableFuture.completedFuture(convertH5PInteractiveContentDirectorySync(task));
	}
}
