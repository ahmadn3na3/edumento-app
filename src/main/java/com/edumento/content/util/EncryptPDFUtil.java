package com.edumento.content.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.edumento.content.domain.Task;
import com.edumento.content.services.ContentService;
import com.edumento.core.constants.ContentStatus;

@Service
public class EncryptPDFUtil {
	private static final Logger log = LoggerFactory.getLogger(FileUtil.class);

	@Value("${mint.pdf.encryption.enabled}")
	private boolean pdfEncryptionEnabled;

	@Autowired
	FileUtil fileUtil;

	@Autowired
	ContentService contentService;

	public void encryptPdf(Task task) throws IOException {
		log.info("pdfEncryptionEnabled" + pdfEncryptionEnabled);
		if (pdfEncryptionEnabled) {
			log.info("start encrypt pdf");
			Path path = fileUtil.createUploadParentPathFromTask(task);

			Path orgFolder = Paths.get(fileUtil.createUploadParentPathFromTask(task).toString(), "original");
			if (!Files.exists(orgFolder)) {
				orgFolder = Files.createDirectory(orgFolder);
			}

			Path filePath = Paths.get(path.toString(), String.format("%s.%s", task.getFileName(), task.getExt()));
			Path fileOriginalPath = Paths.get(orgFolder.toString(),
					String.format("%s.%s", task.getFileName(), task.getExt()));

			log.info("moving original pdf");
			Files.move(filePath, fileOriginalPath, StandardCopyOption.REPLACE_EXISTING);

			String readpass = RandomStringUtils.randomNumeric(8);
			String ownerpass = RandomStringUtils.randomNumeric(8);
			Process process;
			String cmd = String.join(" ", "qpdf --encrypt ", readpass, ownerpass, " 256 -- ",
					fileOriginalPath.toString(), filePath.toString());
			log.info("command -> {} " + cmd, cmd);
			process = Runtime.getRuntime().exec(cmd);
			while (process.isAlive()) {
				continue;
			}
			log.info("process exit value ==> {}", process.exitValue());
			if (process.exitValue() != 0) {
				BufferedReader bufferedInputStream = new BufferedReader(
						new InputStreamReader(process.getErrorStream()));
				bufferedInputStream.lines().forEach(arg0 -> log.error("process error stream ==> {}", arg0));
			}
			contentService.updateContentStatus(task.getContentId(), ContentStatus.READY, readpass, ownerpass, null,
					null, fileOriginalPath.toString());
		}
	}

}
