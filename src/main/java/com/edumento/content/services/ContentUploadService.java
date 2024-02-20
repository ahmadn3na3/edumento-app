package com.edumento.content.services;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.edumento.content.converters.MintH5PInteractiveContentConverter;
import com.edumento.content.converters.MintImagetoPDfConverter;
import com.edumento.content.converters.MintInteractiveContentConverter;
import com.edumento.content.converters.VideoConverterService;
import com.edumento.content.domain.Chunk;
import com.edumento.content.domain.Task;
import com.edumento.content.models.InitUploadModel;
import com.edumento.content.models.InitUploadResponseModel;
import com.edumento.content.repos.TaskRepository;
import com.edumento.content.util.EncryptPDFUtil;
import com.edumento.content.util.FileUtil;
import com.edumento.core.configuration.notifications.Message;
import com.edumento.core.constants.Code;
import com.edumento.core.constants.ContentStatus;
import com.edumento.core.constants.ContentType;
import com.edumento.core.constants.Services;
import com.edumento.core.constants.TaskStatus;
import com.edumento.core.constants.TaskType;
import com.edumento.core.constants.notification.EntityAction;
import com.edumento.core.exception.InvalidException;
import com.edumento.core.exception.MintException;
import com.edumento.core.exception.NotFoundException;
import com.edumento.core.model.ResponseModel;
import com.edumento.core.model.messages.From;
import com.edumento.core.model.messages.content.ContentInfoMessage;
import com.edumento.core.security.SecurityUtils;

import jakarta.servlet.http.HttpServletRequest;

/** Created by ahmad on 6/22/16. */
@Service
public class ContentUploadService {
	private static final int DEFAULT_BUFFER_SIZE = 20480;
	private final Logger log = LoggerFactory.getLogger(ContentUploadService.class);

	@Autowired
	TaskRepository taskRepository;

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	ContentService contentService;

	@Autowired
	FileUtil fileUtil;

	@Autowired
	VideoConverterService videoConverterService;

	@Autowired
	MintInteractiveContentConverter interactiveContentConverter;

	@Autowired
	MintH5PInteractiveContentConverter mintH5PInteractiveContentConverter;

	@Autowired
	MintImagetoPDfConverter imagetoPDfConverter;

	@Autowired
	EncryptPDFUtil encryptPDFUtil;

	public ResponseModel startUpload(Long id) {
		return startUpload(new InitUploadModel(id));
	}

	public ResponseModel startUpload(InitUploadModel initUploadModel) {
		var contentModel = contentService.getContentInformation(initUploadModel.getContentId());
		if (contentModel == null) {
			log.warn("content Not found");
			throw new NotFoundException("content not found");
		}
		var task = new Task(contentModel);
		task.setType(TaskType.UPLOAD);
		if (initUploadModel.getChunkSize() <= 0) {
			task.getChunkList().add(new Chunk(0, 0L, task.getContentLength()));
		} else {
			task.getChunkList().addAll(fileUtil.createChunks(initUploadModel.getChunkSize(), task.getContentLength()));
		}
		taskRepository.save(task);

		var path = fileUtil.createUploadParentPathFromTask(task);
		fileUtil.deleteFile(path);

		log.info("start upload end");

		//////////////////////////////////////////////

		contentService.updateContentStatus(contentModel.getId(), ContentStatus.UPLOADING);
		var initUploadResponseModel = new InitUploadResponseModel();
		initUploadResponseModel.setTaskId(task.getId());
		initUploadResponseModel.setChunkList(task.getChunkList());

		return ResponseModel.done(initUploadResponseModel);
	}

	public ResponseModel resume(MultipartFile filePart, String uploadId, Integer chunkId) {
		// get content Information by id throw services
		log.info("upload chunk file {}", uploadId);
		if (filePart == null) {
			log.error("Upload part is null");
			throw new InvalidException("Upload part is null");
		}

		if (uploadId == null || uploadId.isEmpty()) {
			log.error("INVALID_KEY");
			throw new MintException(Code.INVALID_KEY);
		}

		var task = taskRepository.findOneByIdAndType(uploadId, TaskType.UPLOAD);
		//////////////////////////////////////////////
		if (task == null || task.getStatus() == TaskStatus.FINISHED || task.getStatus() == TaskStatus.CANCELED) {
			log.error("Not vaild task {}", uploadId);
			throw new NotFoundException("task");
		}
		if (task.getExpiryDate().before(new Date())) {
			contentService.updateContentStatus(task.getContentId(), ContentStatus.NOT_UPLOAD);
			log.error("Not vaild task date expired {}", uploadId);
			throw new InvalidException("expired task");
		}

		var path = fileUtil.createUploadParentPathFromTask(task);

		if (task.getChunkList().stream().noneMatch(new Predicate<Chunk>() {
			@Override
			public boolean test(Chunk chunk) {
				return chunk.getChunkIndex().equals(chunkId);
			}
		})) {
			throw new NotFoundException("not found chunk");
		}
		try {
			if (!Files.exists(path) || !Files.isDirectory(path)) {
				Files.createDirectory(path);
			}

			var file = new File(path.toFile(), chunkId.toString());
			if (file.exists() && !file.isDirectory()) {
				fileUtil.deleteFile(file.toPath());
			}
			Files.createFile(file.toPath());

			var inputStream = new BufferedInputStream(filePart.getInputStream());
			var outputStream = new BufferedOutputStream(new FileOutputStream(file));
			var read = 0;
			var buffer = new byte[DEFAULT_BUFFER_SIZE];
			while ((read = inputStream.read(buffer)) > 0) {
				outputStream.write(buffer, 0, read);
				outputStream.flush();
			}
			outputStream.close();
			inputStream.close();
		} catch (IOException e) {
			log.error("something wrong", e);
			throw new MintException(e.getMessage(), e, Code.UNKNOWN, e.getMessage());
		}
		if (task.getStatus() == TaskStatus.INITIATED) {
			task.setStartDate(new Date());
			task.setStatus(TaskStatus.STARTED);
			taskRepository.save(task);
		}
		log.info("chunk ended");
		return ResponseModel.done();
	}

	@Message(entityAction = EntityAction.CONTENT_CREATE, services = Services.NOTIFICATIONS)
	public ResponseModel commit(String uploadId, HttpServletRequest request) {
		// get content Information by id throw services
		log.info("start commit");
		request.getHeader("Authorization");
		var task = taskRepository.findOneByIdAndType(uploadId, TaskType.UPLOAD);
		if (task == null) {
			log.error("task not found");
			throw new NotFoundException("task");
		}

		//////////////////////////////////////////////
		var path = fileUtil.createFilePathFromTask(task);

		try {

			Files.deleteIfExists(path);

			try (var finalOutputStream = Files.newOutputStream(path)) {

				var fileNames = path.toFile().getParentFile()
						.list(new FilenameFilter() {
							@Override
							public boolean accept(File dir, String name) {
								return !name.toLowerCase().contains(task.getFileName().toLowerCase());
							}
						});
				if (fileNames.length > 1) {
					Arrays.sort(fileNames, Comparator.comparing(Long::valueOf));
				}
				for (String fileName : fileNames) {
					var chunk = new File(path.getParent().toFile(), fileName);
					try (var inputStream = Files.newInputStream(chunk.toPath())) {
						var read = 0;
						var bytes = new byte[DEFAULT_BUFFER_SIZE];
						if (inputStream.available() <= DEFAULT_BUFFER_SIZE) {
							bytes = new byte[inputStream.available()];
						}
						while ((read = inputStream.read(bytes)) > 0) {
							finalOutputStream.write(bytes, 0, read);
							finalOutputStream.flush();
						}
					}
					Files.deleteIfExists(chunk.toPath());
				}

			} catch (IOException e) {
				log.error("error in commiting file", e);
			}

			var size = Files.size(path);
			if (size != task.getContentLength()) {
				contentService.deleteContent(task.getContentId());
				throw new InvalidException("size do not match");
			}

			contentService.updateContentStatus(task.getContentId(), ContentStatus.UPLOADED);
			task.setFinishedDate(new Date());
			task.setStatus(TaskStatus.FINISHED);
			taskRepository.save(task);
			switch (task.getContentType()) {
			case VIDEO:
				videoConverterService.extractAndConvert(task);
				break;
			case INTERACTIVE:
				interactiveContentConverter.convertInteractiveContentDirectory(task);
				break;
			case IMAGE:
				imagetoPDfConverter.convertImageToPdfAsync(task);
				break;
			case TEXT:
				encryptPDFUtil.encryptPdf(task);
				break;
			case H5P:
				mintH5PInteractiveContentConverter.convertH5PInteractiveContentDirectory(task);
				log.info("update Content to be .zip");
				contentService.updateContentStatus(task.getContentId(), ContentStatus.READY, null, null, "zip",
						ContentType.INTERACTIVE, null);
				break;
			case OTHER:
				if (task.getExt() != null && "h5p".equals(task.getExt())) {
					mintH5PInteractiveContentConverter.convertH5PInteractiveContentDirectory(task);
					log.info("update Content to be .zip");
					contentService.updateContentStatus(task.getContentId(), ContentStatus.READY, null, null, "zip",
							ContentType.INTERACTIVE, null);
				}
				break;
			default:
				contentService.updateContentStatus(task.getContentId(), ContentStatus.READY);
				break;
			}

		} catch (Exception e) {
			log.error("error in commiting file", e);
			throw new MintException(e.getMessage(), e, Code.UNKNOWN, e.getMessage() + ":" + e.getClass().getName());
		}
		var content = contentService.getContentInformation(task.getContentId());
		return ResponseModel.done(null,
				new ContentInfoMessage(content.getId(), content.getName(), content.getType(),
						content.getSpace().getId(), content.getSpace().getName(),
						content.getSpace().getCategory().getName(), new From(SecurityUtils.getCurrentUser())));
	}

	public ResponseModel cancel(String id, HttpServletRequest request) {
		var task = taskRepository.findOneByIdAndType(id, TaskType.UPLOAD);
		var path = fileUtil.createUploadParentPathFromTask(task);
		if (Files.isDirectory(path)) {
			try {
				Files.deleteIfExists(path);
			} catch (IOException e) {
				log.error("Error in cancel", e);
			}
		}
		contentService.deleteContent(task.getContentId());
		task.setStatus(TaskStatus.CANCELED);
		taskRepository.save(task);
		return ResponseModel.done();
	}
}
