package com.edumento.user.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.edumento.core.exception.NotFoundException;
import com.edumento.core.exception.NotPermittedException;
import com.edumento.core.model.ResponseModel;
import com.edumento.core.security.SecurityUtils;
import com.edumento.user.constant.UserType;
import com.edumento.user.domain.Module;
import com.edumento.user.domain.Permission;
import com.edumento.user.domain.User;
import com.edumento.user.model.modules.ModuleListModel;
import com.edumento.user.model.modules.ModuleModel;
import com.edumento.user.model.modules.PermissionModel;
import com.edumento.user.repo.ModuleRepository;
import com.edumento.user.repo.PermissionRepository;
import com.edumento.user.repo.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;

@Service()
public class ModuleService {
	Logger log = LoggerFactory.getLogger(ModuleService.class);
	private final ModuleRepository moduleRepository;

	private final PermissionRepository permissionRepository;

	private final UserRepository userRepository;

	public ModuleService(ModuleRepository moduleRepository, PermissionRepository permissionRepository,
			UserRepository userRepository) {
		this.moduleRepository = moduleRepository;
		this.permissionRepository = permissionRepository;
		this.userRepository = userRepository;
		initializeModulesAndPermission();
	}

	@Transactional()
	public ResponseModel getModules() {
		return ResponseModel.done(moduleRepository.findAll().stream().map(ModuleListModel::new).toList());

	}

	@Transactional()
	public ResponseModel getModule(Long Id) {
		var module = moduleRepository.findById(Id).orElseThrow(NotFoundException::new);
		var model = new ModuleModel(module);
		module.getPermissions().forEach(new Consumer<Permission>() {
			@Override
			public void accept(Permission permission) {
				model.getPermissions().add(new PermissionModel(permission));
			}
		});
		return ResponseModel.done(model);
	}

	@Transactional()
	public ResponseModel getPermissions() {

		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin()).map(new Function<User, ResponseModel>() {
			@Override
			public ResponseModel apply(User user) {
				final Map<String, Set<Byte>> permissionGroup = switch (user.getType()) {
				case SUPER_ADMIN, SYSTEM_ADMIN -> permissionRepository
										.findByTypeInAndDeletedFalse(
												Arrays.asList(UserType.FOUNDATION_ADMIN, UserType.ADMIN, UserType.USER))
										.stream()
										.collect(Collectors.groupingBy(Permission::getKeyCode, HashMap::new,
												Collectors.collectingAndThen(Collectors.toSet(),
														new Function<Set<Permission>, Set<Byte>>() {
															@Override
															public Set<Byte> apply(Set<Permission> permissions) {
																return permissions.stream()
																		.map(new Function<Permission, Byte>() {
																			@Override
																			public Byte apply(Permission permission) {
																				return permission.getCode().byteValue();
																			}
																		})
																		.collect(Collectors.toSet());
															}
														})));
				default -> throw new NotPermittedException("user type not allowed");
				};
				return ResponseModel.done(permissionGroup);
			}
		}).orElseThrow(NotPermittedException::new);
	}

	@Transactional
	@PostConstruct
	public void initializeModulesAndPermission() {

		var classPathResource = new ClassPathResource("data/module/module.json");
		var objectMapper = new ObjectMapper();
		try (var file = classPathResource.getInputStream()) {
			List<ModuleModel> moduleModels = objectMapper.readValue(file,
					objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, ModuleModel.class));
			log.debug("modules ===> {}", moduleModels);

			moduleModels.forEach(new Consumer<ModuleModel>() {
				@Override
				public void accept(ModuleModel moduleModel) {
					var module = moduleRepository.findOneByKeyCode(moduleModel.getKey());
					if (module == null) {
						addNewModule(moduleModel);

					} else {
						log.debug("updating Module=>{} ", moduleModel.getName());
						moduleModel.getPermissions().forEach(new Consumer<PermissionModel>() {
							@Override
							public void accept(PermissionModel permissionModel) {
								var permission = new Permission(permissionModel.getName(), permissionModel.getKeyCode(),
										permissionModel.getCode(), permissionModel.getType(), module);
								var permissions = module.getPermissions();
								log.trace("updating permission=>{} and key =>{}", permissionModel.getName(),
										permissionModel.getKeyCode());
								if (!permissions.contains(permission)) {
									if (permissionRepository.findByName(permission.getName()) == null) {
										permissionRepository.save(permission);
									}
								}
							}
						});
					}
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void addNewModule(ModuleModel moduleModel) {
		log.debug("adding new Module=>{} ", moduleModel.getName());
		var module = new Module(moduleModel.getName(), moduleModel.getDescription(), moduleModel.getKey());
		moduleRepository.save(module);
		moduleModel.getPermissions().forEach(new Consumer<PermissionModel>() {
			@Override
			public void accept(PermissionModel permissionModel) {
				log.trace("updating permission=>{} and key =>{}", permissionModel.getName(), permissionModel.getKeyCode());
				if (permissionModel.getName() != null) {
					module.getPermissions().add(new Permission(permissionModel.getName(), permissionModel.getKeyCode(),
							permissionModel.getCode(), permissionModel.getType(), module));
				}
			}
		});
		moduleRepository.save(module);
	}
}
