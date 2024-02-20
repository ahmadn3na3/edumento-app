package com.edumento.content.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.edumento.content.converters.MintInteractiveContentConverter;
import com.edumento.content.converters.VideoConverterService;
import com.edumento.content.domain.Content;
import com.edumento.content.domain.Task;
import com.edumento.content.util.FileUtil;
import com.edumento.core.constants.ContentStatus;
import com.edumento.core.constants.ContentType;

@Service
public class ContentBackGroundService {
	private final Logger log = LoggerFactory.getLogger(ContentBackGroundService.class);

	@Autowired
	ContentService contentService;

	@Autowired
	FileUtil fileUtil;

	@Autowired
	VideoConverterService videoConverterService;

	@Autowired
	MintInteractiveContentConverter mintInteractiveContentConverter;

	@Async
	public CompletableFuture<List<Path>> convertType(ContentType contentType) {
		List<Path> paths = new ArrayList<>();
		CompletableFuture<List<Path>> listCompletableFuture = CompletableFuture.completedFuture(paths);
		var contentList = contentService.getContentListByType(contentType);
		log.info("Found {}", contentList.size());
		for (Content content : contentList) {

			var task = new Task(content);
			try {
				var uploadFolder = fileUtil.createUploadParentPathFromTask(task);
				var uploadFile = fileUtil.createFilePathFromTask(task);
				Path fileToCheck = null;
				if (!Files.exists(uploadFile)) {
					log.warn("Skipping not found files of content {} converted to error", content.getId());
					contentService.updateContentStatus(content.getId(), ContentStatus.ERROR);
					continue;
				}
				switch (contentType) {
				case VIDEO:
					fileToCheck = Paths.get(uploadFolder.toString(), "play.mint.mpd");
					if (Files.exists(fileToCheck)) {
						continue;
					}
					contentService.updateContentStatus(content.getId(), ContentStatus.UPLOADED);
					log.info("deleting old files ");
					Files.walk(uploadFolder, 1).filter(new Predicate<Path>() {
						@Override
						public boolean test(Path path) {
							return path.endsWith(".ts") || path.endsWith(".m3u8");
						}
					})
							.forEach(new Consumer<Path>() {
								@Override
								public void accept(Path path) {
									try {
										log.info("path --> {}", path.toFile());
										Files.delete(path);
									} catch (IOException e) {
										log.error(e.getMessage(), e);
									}
								}
							});

					paths.add(videoConverterService.extractAndConvertSync(task));
					break;
				case INTERACTIVE:
					fileToCheck = Paths.get(uploadFolder.toString(), "index.html");
					if (Files.exists(fileToCheck)) {
						continue;
					}
					contentService.updateContentStatus(content.getId(), ContentStatus.UPLOADED);
					paths.add(mintInteractiveContentConverter.convertInteractiveContentDirectorySync(task));
					break;
				default:
					break;
				}
			} catch (Exception e) {
				log.warn("content {} has errors convert status to error", content.getId());
				log.error(e.getMessage(), e);
				contentService.updateContentStatus(content.getId(), ContentStatus.ERROR);
			}
		}
		return listCompletableFuture;
	}
}
