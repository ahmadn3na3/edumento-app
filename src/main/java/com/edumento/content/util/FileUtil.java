package com.edumento.content.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.edumento.content.domain.Chunk;
import com.edumento.content.domain.Task;
import com.edumento.core.constants.ContentType;
import com.edumento.user.domain.UserResources;

/** Created by ahmad on 7/19/16. */
@Service
public class FileUtil {
	private final Logger log = LoggerFactory.getLogger(FileUtil.class);

	@Value("${mint.upload.content.path}")
	private String uploadPath;

	@Value("${mint.view.content.path}")
	private String viewPath;

	@Value("${mint.upload.audio.path}")
	private String audioPath;

	@Value("${mint.upload.img.path}")
	private String imgPath;

	public Path createUploadParentPathFromTask(Task contentModel) {
		return Paths.get(uploadPath, contentModel.getFolderName());
	}

	public Path createFilePathFromTask(Task contentModel) {
		return Paths.get(uploadPath, contentModel.getFolderName(),
				String.format("%s.%s", contentModel.getFileName().toLowerCase(), contentModel.getExt()));
	}

	public Path createViewParentPathFromTask(Task task) {
		return Paths.get(viewPath, task.getViewFolderName());
	}

	public Path createViewFilePathFromTask(Task task) {
		return Paths.get(viewPath, task.getViewFolderName(),
				String.format("%s.%s", task.getViewFileName(), task.getExt()));
	}

	public void deleteFile(Path path) {
		try {
			if (Files.isDirectory(path)) {
				if (Files.isSymbolicLink(path)) {
					Files.delete(path);
				}
				if (Files.exists(path)) {
					if (path.toFile().listFiles() != null) {
						for (File file : path.toFile().listFiles()) {
							deleteFile(file.toPath());
						}
					}

					Files.delete(path);
				}
			} else if (Files.isRegularFile(path)) {
				Files.delete(path);
			}
		} catch (IOException e) {
			log.error("Exception in delete file", e);
		}
	}

	public UserResources uploadResourceFile(MultipartFile file, ContentType type) throws IOException {
		log.debug("Uploading image {}", file.getName());
		String fileName = null, ext = null;
		if (!file.isEmpty()) {
			fileName = file.getOriginalFilename();
			ext = fileName.lastIndexOf(".") > -1 ? fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length())
					: "";
			File fileOnDisk = null;
			switch (type) {
				case IMAGE:
					fileOnDisk = Paths
							.get(imgPath, String.format("%s.%s", UUID.randomUUID().toString().replace("-", ""), ext))
							.toFile();
					break;
				case AUDIO:
					fileOnDisk = Paths
							.get(audioPath, String.format("%s.%s", UUID.randomUUID().toString().replace("-", ""), ext))
							.toFile();
					break;
				default:
					// TODO: Add mint exception to handle this case
					throw new IOException("Not supported resource type");
			}

			BufferedInputStream inputStream = new BufferedInputStream(file.getInputStream());
			BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(fileOnDisk));
			int read = 0;
			byte[] buffer = new byte[20480];
			while ((read = inputStream.read(buffer)) > 0) {
				outputStream.write(buffer, 0, read);
				outputStream.flush();
			}
			outputStream.close();
			inputStream.close();
			UserResources userResources = new UserResources();
			String diskFileName = fileOnDisk.getAbsolutePath();
			if (diskFileName.indexOf(":") == 1) {
				diskFileName = diskFileName.substring(2, diskFileName.length()).replace("\\", "/");
			}
			log.debug("disk file name {}", diskFileName);
			userResources.setDiskFileName(diskFileName);
			userResources.setFileSize(file.getSize());
			userResources.setFileName(fileName);
			userResources.setResourceType(type);
			userResources.setFormat(ext);
			return userResources;

		} else {
			throw new IOException("file is Empty");
		}
	}

	public List<Chunk> createChunks(Integer chunkSize, Long contentSize) {

		Integer maxChunkCount = Long.valueOf(contentSize / chunkSize).intValue();
		List<Chunk> chunks = new ArrayList<>();
		int insertedRows = 0;
		for (int counter = 0; counter < maxChunkCount; counter++) {
			Chunk curChunk = new Chunk(counter);
			curChunk.setChunkStart(Integer.valueOf(counter * chunkSize).longValue());
			curChunk.setChunkEnd(Integer.valueOf((chunkSize * (counter + 1)) - 1).longValue()); // the
			// first
			// chunk
			// end
			// will be
			// 999_999
			// insertChunkRecords inserts chunk data into DB and returns the number of rows
			// affected (1 if
			// succeeded, 0 otherwise)
			chunks.add(curChunk);
		}

		if ((maxChunkCount * chunkSize) < contentSize) {
			Chunk lastChunk = new Chunk(maxChunkCount);
			lastChunk.setChunkStart(Integer.valueOf(maxChunkCount * chunkSize).longValue());
			lastChunk.setChunkEnd(contentSize);
			maxChunkCount++;
			chunks.add(lastChunk);
		}
		return chunks;
	}

	public void extract(Path uploadpath, Path extractedFolder) throws IOException {
		// Open the file
		log.info("Start Extract");
		try (ZipFile file = new ZipFile(uploadpath.toFile())) {
			// Get file entries
			Enumeration<? extends ZipEntry> entries = file.entries();
			// We will unzip files in this folder
			String uncompressedDirectory = extractedFolder.toString();
			// Iterate over entries

			String subfolderName = "";
			boolean firstItraion = true;
			while (entries.hasMoreElements()) {
				// If directory then create a new directory in uncompressed folder
				try {
					ZipEntry entry = entries.nextElement();
					if (entry.isDirectory()) {
						if (!entry.getName().toLowerCase().startsWith("assets") && firstItraion) {
							subfolderName = entry.getName();
							firstItraion = false;
						} else {
							log.debug("Creating Directory 1:" + uncompressedDirectory + "/"
									+ entry.getName().replace(subfolderName, ""));
							Files.createDirectories(
									Paths.get(uncompressedDirectory, entry.getName().replace(subfolderName, "")));
						}

					}
					// Else create the file
					else {
						Path uncompressedFilePath = null;

						uncompressedFilePath = Paths.get(uncompressedDirectory,
								entry.getName().replace(subfolderName, ""));

						if (!Files.exists(uncompressedFilePath.getParent())) {
							log.debug("Creating Directory 2:" + uncompressedFilePath.getParent().toString());
							Files.createDirectory(uncompressedFilePath.getParent());
						}
						Files.copy(file.getInputStream(entry), uncompressedFilePath,
								StandardCopyOption.REPLACE_EXISTING);
						log.debug("Written :" + entry.getName().replace(subfolderName, ""));
					}
				} catch (IllegalArgumentException e) {
					log.error(e.getMessage(), e);
					continue;
				}
			}
		}
		log.info("End Extract");
	}

	public void extractZIP(Path fileZip, Path extractedFolder) throws IOException {
		// Open the file
		log.info("Start Extract");
		File dir = new File(extractedFolder.toString());
		// create output directory if it doesn't exist
		if (!dir.exists())
			dir.mkdirs();
		FileInputStream fis;
		// buffer for read and write data to file
		byte[] buffer = new byte[1024];
		try {
			fis = new FileInputStream(fileZip.toFile());
			ZipInputStream zis = new ZipInputStream(fis);
			ZipEntry ze = zis.getNextEntry();
			while (ze != null) {
				String fileName = ze.getName();
				File newFile = new File(extractedFolder.toString() + File.separator + fileName);
				System.out.println("Unzipping to " + newFile.getAbsolutePath());
				// create directories for sub directories in zip
				new File(newFile.getParent()).mkdirs();
				FileOutputStream fos = new FileOutputStream(newFile);
				int len;
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}
				fos.close();
				// close this ZipEntry
				zis.closeEntry();
				ze = zis.getNextEntry();
			}
			// close last ZipEntry
			zis.closeEntry();
			zis.close();
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		log.info("End Extract");
	}

	public void zipDirectory(File inputDir, File outputZipFile) throws IOException {
		log.info("start compress");
		// Create parent directory for the output file.
		outputZipFile.getParentFile().mkdirs();
		String inputDirPath = inputDir.getAbsolutePath();
		byte[] buffer = new byte[1024];
		FileOutputStream fileOs = null;
		ZipOutputStream zipOs = null;
		try {
			List<File> allFiles = this.listChildFiles(inputDir);
			// Create ZipOutputStream object to write to the zip file
			fileOs = new FileOutputStream(outputZipFile);
			//
			zipOs = new ZipOutputStream(fileOs);
			for (File file : allFiles) {
				String filePath = file.getAbsolutePath();
				String entryName = filePath.substring(inputDirPath.length() + 1);
				ZipEntry ze = new ZipEntry(entryName);
				// Put new entry into zip file.
				zipOs.putNextEntry(ze);
				// Read the file and write to ZipOutputStream.
				FileInputStream fileIs = new FileInputStream(filePath);
				int len;
				while ((len = fileIs.read(buffer)) > 0) {
					zipOs.write(buffer, 0, len);
				}
				fileIs.close();
			}
			log.info("end compress");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			zipOs.close();
			fileOs.close();
		}
	}

	public void createShortcutFile(String filePath, String shortCutPath, boolean soft) throws IOException {
		Process process;
		String cmd = "ln ";
		if (soft)
			cmd = cmd + "-s ";
		cmd = cmd + filePath + " " + shortCutPath;
		log.info("command -> {} " + cmd, cmd);
		process = Runtime.getRuntime().exec(cmd);
		while (process.isAlive()) {
			continue;
		}
		log.info("process exit value ==> {}", process.exitValue());
		if (process.exitValue() != 0) {
			BufferedReader bufferedInputStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			bufferedInputStream.lines().forEach(arg0 -> log.error("process error stream ==> {}", arg0));
		}
	}

	public void copyWithCommand(String source, String target, String execuldePath) throws IOException {
		String cmd = "rsync -az --exclude=" + execuldePath + " " + source + " " + target + " &";
		log.info("command -> {} " + cmd, cmd);
		Runtime.getRuntime().exec(cmd);
	}

	private List<File> listChildFiles(File dir) throws IOException {
		List<File> allFiles = new ArrayList<File>();

		File[] childFiles = dir.listFiles();
		for (File file : childFiles) {
			if (file.isFile()) {
				allFiles.add(file);
			} else {
				List<File> files = this.listChildFiles(file);
				allFiles.addAll(files);
			}
		}
		return allFiles;
	}

	public String getUploadPath() {
		return uploadPath;
	}

	public String getViewPath() {
		return viewPath;
	}

	public String getAudioPath() {
		return audioPath;
	}

	public String getImgPath() {
		return imgPath;
	}
}
