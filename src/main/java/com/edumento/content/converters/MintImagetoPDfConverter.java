package com.edumento.content.converters;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.edumento.content.domain.Task;
import com.edumento.content.services.ContentService;
import com.edumento.content.util.EncryptPDFUtil;
import com.edumento.content.util.FileUtil;
import com.edumento.core.constants.ContentStatus;
import com.edumento.core.constants.ContentType;

@Service
public class MintImagetoPDfConverter {
	private static final Logger log = LoggerFactory.getLogger(VideoConverterService.class);
	@Autowired
	FileUtil fileUtil;

	@Autowired
	EncryptPDFUtil encryptPDFUtil;

	@Autowired
	private ContentService contentService;

	public Path convertImageToPdf(Task task) throws IOException, InterruptedException {
		Path imgPath = fileUtil.createFilePathFromTask(task);

		// Convert
		convertImgToPDF(imgPath.toFile());

		// move to original dir
		Path originalDirectory = Files.createDirectories(Paths.get(imgPath.getParent().toString(), "original"));
		Path outPath = Paths.get(originalDirectory.toString(), imgPath.getFileName().toFile().getName());
		Files.move(imgPath, outPath, StandardCopyOption.REPLACE_EXISTING);

		contentService.updateContentStatus(task.getContentId(), ContentStatus.READY, null, null, "pdf",
				task.getContentType() == ContentType.IMAGE ? ContentType.TEXT : task.getContentType(),
				outPath.toString());

		// encrypt image after concert to pdf
		log.info("encrypt image after concert to pdf");
		task.setExt("pdf");
		encryptPDFUtil.encryptPdf(task);

		return outPath;
	}

	private void convertImgToPDF(File file) throws IOException, InterruptedException {
		log.info("Convert " + file.getAbsolutePath() + " to pdf");

		String output = file.getName().substring(0, file.getName().lastIndexOf(".")) + ".pdf";

        Process process = Runtime.getRuntime().exec("convert " + file.getAbsolutePath() + " " + file.getParent() + "/" + output);
		process.waitFor();
		log.info("Convertion done");
	}

	@Async
	public CompletableFuture<Path> convertImageToPdfAsync(Task task)
			throws IOException, InterruptedException, URISyntaxException {
		return CompletableFuture.completedFuture(convertImageToPdf(task));
	}
}
