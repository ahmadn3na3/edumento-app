package com.edumento.content.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.edumento.content.converters.VideoConverterService;
import com.edumento.content.domain.Content;
import com.edumento.content.domain.ContentUser;
import com.edumento.content.domain.Task;
import com.edumento.content.models.ContentViewModel;
import com.edumento.content.repos.ContentRepository;
import com.edumento.content.repos.ContentUserRepository;
import com.edumento.content.repos.TaskRepository;
import com.edumento.content.util.Base64HexUtil;
import com.edumento.content.util.Base64Util;
import com.edumento.content.util.FileUtil;
import com.edumento.core.constants.Code;
import com.edumento.core.constants.ContentStatus;
import com.edumento.core.constants.TaskStatus;
import com.edumento.core.constants.TaskType;
import com.edumento.core.exception.MintException;
import com.edumento.core.exception.NotFoundException;
import com.edumento.core.exception.NotReadyException;
import com.edumento.core.model.ResponseModel;
import com.edumento.core.model.TimeSpentModel;
import com.edumento.core.security.SecurityUtils;

import jakarta.transaction.Transactional;

@Service
public class ContentViewService {

	private final Logger log = LoggerFactory.getLogger(ContentViewService.class);
	private final ContentService contentService;
	private final TaskRepository taskRepository;
	private final FileUtil fileUtil;
	private final ContentUserRepository contentUserRepository;

	private final VideoConverterService videoConverterService;

	private final ContentRepository contentRepository;

	@Value("${mint.viewurl}")
	private String viewUrl;

	@Value("${mint.ffmpeg}")
	private String pathToffmpeg;

	@Value("${mint.ffprobe}")
	private String pathToffprobe;

	@Value("${mint.pdf.encryption.enabled}")
	private boolean pdfEncryptionEnabled;

	@Autowired
	public ContentViewService(ContentService contentService, TaskRepository taskRepository, FileUtil fileUtil,
			VideoConverterService videoConverterService, ContentUserRepository contentUserRepository,
			ContentRepository contentRepository) {
		super();
		this.contentService = contentService;
		this.taskRepository = taskRepository;
		this.fileUtil = fileUtil;
		this.videoConverterService = videoConverterService;
		this.contentUserRepository = contentUserRepository;
		this.contentRepository = contentRepository;
	}

	@Transactional
	public ResponseModel getContentServUrl(Long contentId, boolean isAndroid, boolean getOrginal) throws IOException {
		// TODO: Second level validation
		Optional.ofNullable(taskRepository.findOneByUserNameAndContentIdAndType(SecurityUtils.getCurrentUserLogin(),
				contentId, TaskType.VEIW)).ifPresent(this::finishTask);
		Content content = contentService.getContentInformation(contentId);
		if (content == null) {
			throw new NotFoundException();
		}
		if (content.getStatus() != ContentStatus.READY) {
			throw new MintException(Code.INVALID, "error.content.notready");
		}
		Task task = new Task(content, UUID.randomUUID().toString().replace("-", ""),
				UUID.randomUUID().toString().replace("-", ""));

		Path uploadPath = fileUtil.createFilePathFromTask(task);
		log.debug("upload {} is exist {}", uploadPath.toString(), Files.exists(uploadPath));
		log.debug("upload {} is regular {}", uploadPath.toString(), Files.isRegularFile(uploadPath));
		if (!Files.exists(uploadPath) || !Files.isRegularFile(uploadPath)) {
			task.setExt(task.getExt().toUpperCase());
			uploadPath = fileUtil.createFilePathFromTask(task);
			log.debug("upload {} is exist {}", uploadPath.toString(), !Files.exists(uploadPath));
			log.debug("upload {} is regular {}", uploadPath.toString(), !Files.isRegularFile(uploadPath));
			if (!Files.exists(uploadPath) || !Files.isRegularFile(uploadPath)) {
				throw new FileNotFoundException();
			}
		}
		Path viewFolder = fileUtil.createViewParentPathFromTask(task);
		if (!Files.exists(viewFolder) || !Files.isDirectory(viewFolder)) {
			Files.createDirectory(viewFolder);
		}
		ContentViewModel contentViewModel = new ContentViewModel();
		Path viewPath = null;
		switch (content.getType()) {
			case VIDEO:
				if (content.getStatus() != ContentStatus.READY) {
					throw new NotReadyException();
				}
				viewPath = Paths.get(viewFolder.toString(), "view");

				if (getOrginal && content.getAllowUseOrginal().booleanValue()) {
					Path orginalPath = Paths.get(uploadPath.getParent().toString(), "original");
					if (orginalPath.toFile().exists()) {
						// DirUtils.copy(orginalPath, viewFolder);
						fileUtil.createShortcutFile(
								Paths.get(orginalPath.toString(), String.format("%s.%s", task.getFileName(), "mp4"))
										.toString(),
								Paths.get(fileUtil.createViewParentPathFromTask(task).toString(),
										String.format("%s.%s", task.getFileName(), "mp4")).toString(),
								true);
						contentViewModel.getUrls().add(
								String.format("%s%s/%s.%s", viewUrl, task.getViewFolderName(), task.getFileName(),
										"mp4"));
						contentViewModel.setSize(task.getContentLength());
					}
					break;
				}
				if (content.getKey() != null && content.getKeyId() != null) {
					contentViewModel.setKeyId(content.getKeyId().substring(0, 16));
					contentViewModel.setKey(content.getKey());
					contentViewModel.setEncodedKeyId(Base64HexUtil.hex2Base64(content.getKeyId()));
					contentViewModel.setEncodedKey(Base64HexUtil.hex2Base64(content.getKey()));
					if (!isAndroid) {
						Path targetPath = Paths
								.get(String.format("%s/%s", uploadPath.getParent().getParent().toString(), "view"));
						fileUtil.createShortcutFile(uploadPath.getParent().toString(), targetPath.toString(), true);
						// DirUtils.copyWithPredicate(uploadPath.getParent(), viewFolder, path ->
						// !path.endsWith("original"));
						contentViewModel.getUrls()
								.add(String.format("%s%s/%s", viewUrl.replace("view", "content/view"),
										task.getFolderName(), "play.mint.mpd"));
					} else {
						viewPath = fileUtil.createViewFilePathFromTask(task);
						fileUtil.createShortcutFile(uploadPath.toString(),
								Paths.get(fileUtil.createViewParentPathFromTask(task).toString(),
										String.format("%s.%s", task.getFileName(), "mp4")).toString(),
								true);
						// Files.copy(uploadPath, viewPath);
						contentViewModel.getUrls().add(String.format("%s%s/%s.%s", viewUrl, task.getViewFolderName(),
								task.getViewFileName(), "mp4"));
						contentViewModel.setSize(Files.size(uploadPath));
					}
				} else if (Files.exists(Paths.get(viewPath.toString(), "master.m3u8")) && !isAndroid) {
					final Path uPath = uploadPath;
					// DirUtils.copyWithPredicate(uploadPath, viewFolder, path ->
					// !path.equals(uPath));
					Path contentViewPath = Paths.get(uploadPath.getParent().getParent().toString(), "view");
					fileUtil.createShortcutFile(uploadPath.getParent().toString(), contentViewPath.toString(), true);
					contentViewModel.getUrls()
							.add(viewUrl.replace("view", "content/view") + task.getViewFolderName() + File.separator
									+ "master.m3u8");
				} else {
					if (!Files.exists(Paths.get(viewPath.toString(), task.getFileName() + ".mp4"))) {
						videoConverterService.convertToMp4(fileUtil.createFilePathFromTask(task), task.getFileName());
					}
					contentViewModel.getUrls()
							.add(String.format("%s%s/%s/%s.%s", viewUrl, task.getViewFolderName(), "view",
									task.getFileName(), "mp4"));
				}

				break;
			case INTERACTIVE:
				if (content.getStatus() != ContentStatus.READY && !isAndroid) {
					throw new NotReadyException();
				}

				if (isAndroid) {
					viewPath = fileUtil.createViewFilePathFromTask(task);
					if (Files.exists(viewPath)) {
						Files.delete(viewPath);
					}
					// Files.createFile(viewPath);
					// Files.copy(uploadPath, viewPath);
					fileUtil.createShortcutFile(uploadPath.toString(),
							Paths.get(fileUtil.createViewParentPathFromTask(task).toString(),
									String.format("%s.%s", task.getViewFileName(), task.getExt())).toString(),
							true);
					contentViewModel.getUrls().add(String.format("%s%s/%s.%s", viewUrl, task.getViewFolderName(),
							task.getViewFileName(), task.getExt()));
					contentViewModel.setSize(Files.size(uploadPath));
				} else {
					// DirUtils.copyWithPredicate(uploadPath.getParent(), viewFolder, path -> {
					// return !path.toString().endsWith(".zip");
					// });
					Path targetPath = Paths
							.get(String.format("%s/%s", uploadPath.getParent().getParent().toString(), "view"));
					fileUtil.createShortcutFile(uploadPath.getParent().toString(), targetPath.toString(), true);
					contentViewModel.getUrls()
							.add(viewUrl.replace("view", "content/view") + task.getFolderName() + File.separator
									+ "index.html");
				}

				break;
			case TEXT:
				if (content.getKey() != null) {
					contentViewModel.setEncodedKey(Base64Util.base64Encode(content.getKey()));
				}
				if (!isAndroid) {
					task.setExt("mwpdf");
				}
				viewPath = fileUtil.createViewFilePathFromTask(task);
				if (Files.exists(viewPath)) {
					Files.delete(viewPath);
				}
				if (!pdfEncryptionEnabled ||
						(getOrginal && content.getAllowUseOrginal().booleanValue())) {
					Path orginalFolder = Paths.get(uploadPath.getParent().toString(), "original");
					Path orginalPath = Paths.get(orginalFolder.toString(),
							String.format("%s.%s", task.getFileName(), task.getExt()));
					log.info(orginalPath.toString() + viewPath.toString());
					if (Files.exists(orginalPath)) {
						fileUtil.createShortcutFile(orginalPath.toString(),
								Paths.get(fileUtil.createViewParentPathFromTask(task).toString(),
										String.format("%s.%s", task.getViewFileName(), task.getExt())).toString(),
								true);
						// Files.copy(orginalPath, viewPath);
						contentViewModel.getUrls().add(String.format("%s%s/%s.%s", viewUrl, task.getViewFolderName(),
								task.getViewFileName(), task.getExt()));
						contentViewModel.setSize(Files.size(viewPath));
					} else {
						fileUtil.createShortcutFile(uploadPath.toString(),
								Paths.get(fileUtil.createViewParentPathFromTask(task).toString(),
										String.format("%s.%s", task.getViewFileName(), task.getExt())).toString(),
								true);
						// Files.copy(uploadPath, viewPath);
						contentViewModel.getUrls().add(String.format("%s%s/%s.%s", viewUrl, task.getViewFolderName(),
								task.getViewFileName(), task.getExt()));
						contentViewModel.setSize(Files.size(viewPath));
					}
					break;
				} else {
					fileUtil.createShortcutFile(uploadPath.toString(),
							Paths.get(fileUtil.createViewParentPathFromTask(task).toString(),
									String.format("%s.%s", task.getViewFileName(), task.getExt())).toString(),
							true);
					// Files.copy(uploadPath, viewPath);
					contentViewModel.getUrls().add(String.format("%s%s/%s.%s", viewUrl, task.getViewFolderName(),
							task.getViewFileName(), task.getExt()));
					contentViewModel.setSize(Files.size(viewPath));
				}
				break;

			default:
				viewPath = fileUtil.createViewFilePathFromTask(task);
				if (Files.exists(viewPath)) {
					Files.delete(viewPath);
				}
				fileUtil.createShortcutFile(uploadPath.toString(),
						Paths.get(fileUtil.createViewParentPathFromTask(task).toString(),
								String.format("%s.%s", task.getViewFileName(), task.getExt())).toString(),
						true);
				// Files.copy(uploadPath, viewPath);
				contentViewModel.getUrls().add(String.format("%s%s/%s.%s", viewUrl, task.getViewFolderName(),
						task.getViewFileName(), task.getExt()));
				contentViewModel.setSize(Files.size(viewPath));
				break;
		}
		log.debug("files url =>", contentViewModel.getUrls());
		taskRepository.save(task);
		contentViewModel.setViewTaskId(task.getId());
		return ResponseModel.done(contentViewModel);
	}

	@Transactional
	public ResponseModel finishView(String id) {
		return taskRepository.findById(id).map(task -> {
			finishTask(task);
			return ResponseModel.done();
		}).orElseThrow(NotFoundException::new);
	}

	@Scheduled(cron = "0 0 0 * * ?")
	public void deleteExpiredTasks() {
		taskRepository.findByTypeAndStatusAndExpiryDateAfter(TaskType.VEIW, TaskStatus.OPEN, new Date())
				.forEach(this::finishTask);
	}

	private void finishTask(Task task) {
		if (task != null) {
			Path path = fileUtil.createViewParentPathFromTask(task);
			fileUtil.deleteFile(path);
			taskRepository.delete(task);
			Long userId = SecurityUtils.getCurrentUser() == null ? null : SecurityUtils.getCurrentUser().getId();
			if (userId != null) {
				ContentUser contentUser = contentUserRepository.findByUserIdAndContentId(userId, task.getContentId())
						.map(cu -> {
							TimeSpentModel model = new TimeSpentModel();
							model.setStartDateTime(task.getStartDate());
							model.setEndTime(new Date());
							model.setTimeSpent(model.getEndTime().getTime() - model.getStartDateTime().getTime());
							cu.getTimeSpent().add(model);
							cu.setLastAccessDate(task.getStartDate());
							cu.setViews(cu.getViews() + 1);
							return cu;
						}).orElseGet(() -> {
							log.debug("new contnet user {}", SecurityUtils.getCurrentUserLogin());
							ContentUser cuContentUser = new ContentUser();
							cuContentUser.setContentId(task.getContentId());
							cuContentUser.setUserId(userId);
							cuContentUser.setUserName(SecurityUtils.getCurrentUser().getUsername());
							TimeSpentModel model = new TimeSpentModel();
							model.setStartDateTime(task.getStartDate());
							model.setEndTime(new Date());
							model.setTimeSpent(model.getEndTime().getTime() - model.getStartDateTime().getTime());
							cuContentUser.getTimeSpent().add(model);
							cuContentUser.setViews(1);
							cuContentUser.setLastAccessDate(task.getStartDate());
							return cuContentUser;
						});
				contentUserRepository.save(contentUser);
			}
		}
	}
}
