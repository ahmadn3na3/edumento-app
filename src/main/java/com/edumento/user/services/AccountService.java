package com.edumento.user.services;

import static org.springframework.data.jpa.domain.Specification.where;

import com.edumento.b2c.domain.CloudPackage;
import com.edumento.b2c.repos.CloudPackageRepository;
import com.edumento.core.configuration.auditing.Auditable;
import com.edumento.core.configuration.notifications.Message;
import com.edumento.core.constants.Code;
import com.edumento.core.constants.PackageType;
import com.edumento.core.constants.Services;
import com.edumento.core.constants.SpaceRole;
import com.edumento.core.constants.notification.EntityAction;
import com.edumento.core.exception.ExistException;
import com.edumento.core.exception.MintException;
import com.edumento.core.exception.NotFoundException;
import com.edumento.core.exception.NotPermittedException;
import com.edumento.core.model.ResponseModel;
import com.edumento.core.model.messages.user.UserInfoMessage;
import com.edumento.core.security.SecurityUtils;
import com.edumento.core.util.DateConverter;
import com.edumento.core.util.RandomUtils;
import com.edumento.user.constant.UserType;
import com.edumento.user.domain.Permission;
import com.edumento.user.domain.User;
import com.edumento.user.model.account.ChangePasswordModel;
import com.edumento.user.model.account.RegesiterAccountModel;
import com.edumento.user.model.user.UserCreateModel;
import com.edumento.user.model.user.UserInfoModel;
import com.edumento.user.model.user.UserModel;
import com.edumento.user.model.user.UserSearchModel;
import com.edumento.user.repo.PermissionRepository;
import com.edumento.user.repo.UserRepository;
import com.edumento.user.repo.specifications.UserSpecifications;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * import ch.qos.logback.classic.pattern.DateConverter; Created by ahmad on 2/17/16.
 *
 * @author ahmad neanaa
 */
@Service
public class AccountService {

  private final Logger log = LoggerFactory.getLogger(AccountService.class);
  private final UserRepository userRepository;

  private final PasswordEncoder passwordEncoder;

  private final CloudPackageRepository cloudPackageRepository;

  private final PermissionRepository permissionRepository;

  public AccountService(
      PasswordEncoder passwordEncoder,
      UserRepository userRepository,
      CloudPackageRepository cloudPackageRepository,
      PermissionRepository permissionRepository) {
    this.passwordEncoder = passwordEncoder;
    this.userRepository = userRepository;
    this.cloudPackageRepository = cloudPackageRepository;
    this.permissionRepository = permissionRepository;
  }

  @Transactional
  // @Auditable(EntityAction.USER_UPDATE)
  // @Message(entityAction = EntityAction.USER_REACTIVATE, services =
  // Services.NOTIFICATIONS)
  public ResponseModel resendActivationCode(String mail, String lang) {
    return userRepository
        .findOneByEmailAndDeletedFalse(mail)
        .map(
            new Function<User, ResponseModel>() {
              @Override
              public ResponseModel apply(User user) {
                if (user.getStatus()) {
                  log.warn("invalid , user {} not activated", user.getId());
                  throw new MintException(Code.INVALID, "error.email.activated");
                }
                if (!user.getFirstLogin()) {
                  throw new NotPermittedException("error.user.disabled");
                }

                user.setActivationKey(RandomUtils.generateActivationKey());
                user.setActivationDate(new Date());
                userRepository.save(user);
                return ResponseModel.done(null, new UserInfoMessage(user, null, lang));
              }
            })
        .orElseThrow(
            new Supplier<NotFoundException>() {
              @Override
              public NotFoundException get() {
                return new NotFoundException("error.email.account");
              }
            });
  }

  @Transactional
  // @Auditable(EntityAction.USER_UPDATE)
  // @Message(entityAction = EntityAction.USER_ACTIVATE, services = Services.CHAT)
  public ResponseModel activateRegistration(String key) {
    log.debug("Activating user for activation key {}", key);
    return userRepository
        .findOneByActivationKeyAndDeletedFalse(key)
        .map(
            new Function<User, ResponseModel>() {
              @Override
              public ResponseModel apply(User user) {
                if (!user.getFirstLogin()) {
                  throw new NotPermittedException("error.user.disabled");
                }
                var calendar = Calendar.getInstance();
                calendar.setTime(user.getActivationDate());
                calendar.add(Calendar.HOUR, 24);
                if (new Date().after(calendar.getTime())) {
                  throw new MintException(Code.INVALID, "error.activation.code");
                }

                user.setActivationKey(null);
                user.setActivationDate(null);
                user.setStatus(Boolean.TRUE);
                userRepository.save(user);
                log.debug("Activated user: {}", user);
                return ResponseModel.done(null, new UserInfoMessage(user, null, user.getLangKey()));
              }
            })
        .orElseThrow(
            new Supplier<MintException>() {
              @Override
              public MintException get() {
                return new MintException(Code.INVALID, "error.activation.code");
              }
            });
  }

  @Transactional
  @Auditable(EntityAction.USER_UPDATE)
  public ResponseModel completePasswordReset(String newPassword, String key) {
    log.debug("Reset user password for reset key {}", key);

    return userRepository
        .findOneByResetKeyAndDeletedFalse(key)
        .map(
            new Function<User, ResponseModel>() {
              @Override
              public ResponseModel apply(User user) {
                var calendar = Calendar.getInstance();
                calendar.setTime(user.getResetDate());
                calendar.add(Calendar.HOUR, 24);
                if (new Date().after(calendar.getTime())) {
                  log.warn("reset key {} expired", key);
                  throw new MintException(Code.INVALID, "error.reset.code");
                }
                user.setResetDate(null);
                user.setResetKey(null);
                user.setPassword(passwordEncoder.encode(newPassword));
                if (user.getForceChangePassword()) {
                  user.setForceChangePassword(Boolean.FALSE);
                }
                userRepository.save(user);
                return ResponseModel.done();
              }
            })
        .orElseThrow(
            new Supplier<MintException>() {
              @Override
              public MintException get() {
                return new MintException(Code.INVALID, "error.reset.code");
              }
            });
  }

  @Transactional
  @Auditable(EntityAction.USER_UPDATE)
  @Message(entityAction = EntityAction.USER_FORGETPASSOWORD, services = Services.NOTIFICATIONS)
  public ResponseModel requestPasswordReset(String mail, String baseUrl, String lang) {
    log.debug("{} request password reset", mail);
    return userRepository
        .findOneByEmailAndDeletedFalse(mail)
        .map(
            new Function<User, ResponseModel>() {
              @Override
              public ResponseModel apply(User user) {
                if (!user.getStatus()) {
                  log.warn("user {} not activated", user.getUserName());
                  throw new MintException(Code.NOT_ACTIVATED);
                }
                user.setResetKey(RandomUtils.generateResetKey());
                user.setResetDate(Date.from(ZonedDateTime.now(ZoneOffset.UTC).toInstant()));
                userRepository.save(user);
                log.debug("password reset , and sent to {}", mail);
                return ResponseModel.done(null, new UserInfoMessage(user, null, lang));
              }
            })
        .orElseThrow(
            new Supplier<NotFoundException>() {
              @Override
              public NotFoundException get() {
                return new NotFoundException("email");
              }
            });
  }

  @Transactional
  @Auditable(EntityAction.USER_UPDATE)
  public ResponseModel checkResetCode(String code) {
    log.debug("check reset code {} ", code);
    return userRepository
        .findOneByResetKeyAndDeletedFalse(code)
        .map(
            new Function<User, ResponseModel>() {
              @Override
              public ResponseModel apply(User user) {
                if (ZonedDateTime.now(ZoneOffset.UTC)
                    .isAfter(
                        ZonedDateTime.ofInstant(user.getResetDate().toInstant(), ZoneOffset.UTC)
                            .plusHours(24))) {
                  log.warn("code {} is invalid ", code);
                  throw new MintException(Code.INVALID, "error.reset.code");
                }
                return ResponseModel.done();
              }
            })
        .orElseThrow(
            new Supplier<NotFoundException>() {
              @Override
              public NotFoundException get() {
                return new NotFoundException("error.reset.code");
              }
            });
  }

  @Transactional
  @Auditable(EntityAction.USER_CREATE)
  @Message(entityAction = EntityAction.USER_REGISTER, services = Services.NOTIFICATIONS)
  public synchronized ResponseModel createUserInformation(
      RegesiterAccountModel userCreateModel, String baseUrl, String lang) {
    log.debug("create user information:{}", userCreateModel);
    if (userRepository
        .findOneByUserNameAndDeletedFalse(userCreateModel.getUsername())
        .isPresent()) {
      throw new ExistException(userCreateModel.getUsername());
    }
    if (userRepository.findOneByEmailAndDeletedFalse(userCreateModel.getEmail()).isPresent()) {
      throw new ExistException(userCreateModel.getEmail());
    }

    var newUser = new User();

    var encryptedPassword = passwordEncoder.encode(userCreateModel.getPassword());
    // new user gets initially a generated password
    newUser.setPassword(encryptedPassword);
    newUser.setFullName(userCreateModel.getFullName());
    newUser.setUserName(userCreateModel.getUsername());
    newUser.setEmail(userCreateModel.getEmail());
    newUser.setLangKey(userCreateModel.getLang());
    // new user is not active
    newUser.setStatus(false);
    newUser.setFirstLogin(Boolean.TRUE);
    // new user gets registration key
    newUser.setActivationKey(RandomUtils.generateActivationKey());
    newUser.setActivationDate(new Date());
    newUser.setType(UserType.USER);
    newUser.setBirthDate(DateConverter.convertLocalDateToDate(userCreateModel.getBirthDate()));
    newUser.setCountry(userCreateModel.getCountry());
    newUser.setMobile(userCreateModel.getMobile());
    newUser.setGender(userCreateModel.getGender().getValue());

    CloudPackage cloudPackage =
        cloudPackageRepository.findByPackageTypeAndNameAndDeletedFalse(
            PackageType.STANDARD, PackageType.STANDARD.name());
    if (userCreateModel.getPackageId() != null && !userCreateModel.getPackageId().equals(0L)) {
      cloudPackage =
          cloudPackageRepository
              .findById(userCreateModel.getPackageId())
              .orElseThrow(NotFoundException::new);
    }

    newUser.setCloudPackage(cloudPackage);
    userRepository.save(newUser);
    log.info("Activation key: {}", newUser.getActivationKey());
    log.debug("Created Information for User: {}", newUser);

    return ResponseModel.done(
        "Please check your mail to complete activation process",
        new UserInfoMessage(newUser, null, lang));
  }

  @Transactional
  @Auditable(EntityAction.USER_UPDATE)
  @Message(entityAction = EntityAction.USER_UPDATE, services = Services.NOTIFICATIONS)
  public ResponseModel updateUserInformation(UserCreateModel userUpdateModel) {
    log.debug("update user information: {}", userUpdateModel);
    return userRepository
        .findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
        .map(
            new Function<User, ResponseModel>() {
              @Override
              public ResponseModel apply(User u) {
                return getUserUpdateResponseModel(userUpdateModel, u);
              }
            })
        .orElseThrow(
            new Supplier<NotFoundException>() {
              @Override
              public NotFoundException get() {
                return new NotFoundException("user");
              }
            });
  }

  @Transactional
  @Auditable(EntityAction.USER_UPDATE)
  public ResponseModel changePassword(ChangePasswordModel password) {
    return userRepository
        .findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
        .map(
            new Function<User, ResponseModel>() {
              @Override
              public ResponseModel apply(User u) {

                if (!passwordEncoder.matches(password.getOldPassword(), u.getPassword())) {
                  throw new MintException(Code.INVALID, "error.password.old.invalid");
                }

                var encryptedPassword = passwordEncoder.encode(password.getPassword());
                u.setPassword(encryptedPassword);
                u.setFirstLogin(Boolean.FALSE);
                u.setForceChangePassword(Boolean.FALSE);
                userRepository.save(u);
                log.debug("Changed password for User: {}", u);
                return ResponseModel.done();
              }
            })
        .orElseThrow(
            new Supplier<NotFoundException>() {
              @Override
              public NotFoundException get() {
                return new NotFoundException("user");
              }
            });
  }

  @Transactional(readOnly = true)
  // TODO: Recreate Permission For
  @PreAuthorize("hasAuthority('USER_READ')")
  public ResponseModel getUserWithAuthorities(Long id) {
    log.debug("get user {} with authorities", id);
    var user = userRepository.findById(id).orElseThrow(NotFoundException::new);
    if (user == null) {
      log.warn("user {} not found", id);
      throw new NotFoundException("user");
    }
    var userInfoModel = getUserInfoWithPermission(user, false);
    return ResponseModel.done(userInfoModel);
  }

  @Transactional(readOnly = true)
  public UserInfoModel getUserWithAuthorities() {
    return userRepository
        .findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
        .map(
            new Function<User, UserInfoModel>() {
              @Override
              public UserInfoModel apply(User user) {
                return getUserInfoWithPermission(user, true);
              }
            })
        .orElseThrow(
            new Supplier<NotFoundException>() {
              @Override
              public NotFoundException get() {
                return new NotFoundException("user");
              }
            });
  }

  private UserInfoModel getUserInfoWithPermission(User user, boolean includePermission) {
    log.debug("get user {} information with permission {}", user.getUserName(), includePermission);
    var userInfoModel = new UserModel(user);

    if (includePermission) {
      if (user.getType() == UserType.SYSTEM_ADMIN || user.getType() == UserType.SUPER_ADMIN) {
        permissionRepository
            .findByTypeInAndDeletedFalse(
                Arrays.asList(UserType.SYSTEM_ADMIN, UserType.SUPER_ADMIN, UserType.USER))
            .forEach(
                new Consumer<Permission>() {
                  @Override
                  public void accept(Permission permission) {
                    var val = permission.getCode().byteValue();
                    if (!userInfoModel.getPermissions().containsKey(permission.getKeyCode())) {
                      userInfoModel.getPermissions().put(permission.getKeyCode(), val);
                    } else if ((Integer.valueOf(
                                    userInfoModel
                                        .getPermissions()
                                        .get(permission.getKeyCode())
                                        .toString())
                                .byteValue()
                            & val)
                        != val) {
                      userInfoModel
                          .getPermissions()
                          .put(
                              permission.getKeyCode(),
                              Integer.valueOf(
                                          userInfoModel
                                              .getPermissions()
                                              .get(permission.getKeyCode())
                                              .toString())
                                      .byteValue()
                                  | val);
                    }
                  }
                });
      } else if (user.getCloudPackage() != null) {
        user.getCloudPackage()
            .getPermission()
            .forEach(
                new BiConsumer<String, Byte>() {
                  @Override
                  public void accept(String key, Byte value) {
                    byte val = value;
                    if (!userInfoModel.getPermissions().containsKey(key)) {
                      userInfoModel.getPermissions().put(key, val);
                    } else if ((Integer.valueOf(userInfoModel.getPermissions().get(key).toString())
                                .byteValue()
                            & val)
                        != val) {
                      userInfoModel
                          .getPermissions()
                          .put(
                              key,
                              Integer.valueOf(userInfoModel.getPermissions().get(key).toString())
                                      .byteValue()
                                  | val);
                    }
                  }
                });
      }

      Arrays.stream(SpaceRole.values())
          .forEach(
              new Consumer<SpaceRole>() {
                @Override
                public void accept(SpaceRole spaceRole) {
                  var objectMap =
                      userInfoModel
                          .getSpaceRolePermission()
                          .getOrDefault(spaceRole, new HashMap<>());
                  spaceRole
                      .getPermissions()
                      .forEach(
                          new BiConsumer<String, Byte>() {
                            @Override
                            public void accept(String k, Byte v) {
                              var val =
                                  (byte)
                                      (userInfoModel.getPermissions().containsKey(k)
                                          ? Byte.valueOf(String.valueOf(v))
                                              & Byte.valueOf(
                                                  userInfoModel.getPermissions().get(k).toString())
                                          : 0);
                              objectMap.put(k, val);
                            }
                          });
                  userInfoModel.getSpaceRolePermission().put(spaceRole, objectMap);
                }
              });
    }
    return userInfoModel;
  }

  private ResponseModel getUserUpdateResponseModel(UserCreateModel userUpdateModel, User user) {
    log.debug("map user from user create model");
    user.setBirthDate(DateConverter.convertLocalDateToDate(userUpdateModel.getBirthDate()));
    user.setFullName(userUpdateModel.getFullName());
    user.setCountry(userUpdateModel.getCountry());
    user.setProfession(userUpdateModel.getProfession());
    user.setMobile(userUpdateModel.getMobile());
    user.setInterests(userUpdateModel.getInterests());
    user.setUserStatus(userUpdateModel.getUserStatus());
    user.setThumbnail(userUpdateModel.getImage());
    user.setLangKey(userUpdateModel.getLang());
    user.setGender(userUpdateModel.getGender().getValue());
    user.setMailNotification(userUpdateModel.getEmailNotification());
    user.setNotification(userUpdateModel.getNotification());
    user.setSchool(userUpdateModel.getSchool());
    userRepository.save(user);
    log.debug("Changed Information for User: {}", user);
    return ResponseModel.done(null, new UserInfoMessage(user));
  }

  @Transactional
  @PreAuthorize("hasAuthority('USER_READ') AND hasAuthority('ADMIN')")
  public ResponseModel searchUser(UserSearchModel userSearchModel) {
    log.debug("search for user by specification", userSearchModel);

    Specification<User> byUserType = null;

    if (userSearchModel.getUserType() == null) {
      return ResponseModel.done(
          StreamSupport.stream(userRepository.findAll().spliterator(), false)
              .map(UserModel::new)
              .collect(Collectors.toList()));
    } else {
      if (userSearchModel.getUserType() != null) {
        byUserType = UserSpecifications.hasUserType(userSearchModel.getUserType());
      }

      return ResponseModel.done(
          userRepository.findAll(where(byUserType).and(UserSpecifications.notDeleted())).stream()
              .map(UserModel::new)
              .collect(Collectors.toList()));
    }
  }

  /** created by A.Alsayed 16-01-2019 */
  /** this method is used for returning sum of user space score and user level */
  @Transactional
  public ResponseModel getUserLevelAndPoints() {
    // 1. get current logged-in user:
    // ==============================
    // var userDetail = SecurityUtils.getCurrentUser();
    // if (userDetail != null) {
    // // 2. from joined table, get sum of space total grades for logged-in user:
    // var userScorePoints = userRepository.getUserTotalScore(userDetail.getId());
    //
    // userScorePoints = userScorePoints != null ? userScorePoints : 0.0f;
    //
    // // 3. calculate user level:
    // var userlevel = (int) (userScorePoints.floatValue() / 100);
    // userScorePoints = userScorePoints % 100;
    //
    // return ResponseModel.done(new LeaderboardModel(userDetail.getUsername(),
    // userlevel, userScorePoints));
    // } else {
    throw new NotPermittedException();
    // }

  }

  /** created by A.Alsayed 21-01-2019 */
  /** this method is used for User's global ranking */
  @Transactional
  public ResponseModel getUserGlobalRanking() {
    // // 1. get current logged-in user:
    // // ==============================
    // var userDetail = SecurityUtils.getCurrentUser();
    // if (userDetail != null) {
    // // 2. from joined table, get global ranking for logged-in user:
    // return
    // ResponseModel.done(userRepository.getUserGlobalRanking(userDetail.getId()));
    // } else {
    throw new NotPermittedException();
    // }
  }

  /** created by A.Alsayed 21-01-2019 */
  /** this method is used for returning User's rank for each space */
  @Transactional
  public ResponseModel getUserSpaceRanking() {
    // 1. get current logged-in user:
    // ==============================
    // var userDetail = SecurityUtils.getCurrentUser();
    // if (userDetail != null) {
    // // 2.get all user spaces:
    // Set<Joined> joineds =
    // joinedRepository.findByUserIdAndDeletedFalse(userDetail.getId())
    // .collect(Collectors.toSet());
    // List<UserSpaceRankingModel> userSpaceRanking = new ArrayList<>();
    // if (joineds != null && !joineds.isEmpty()) {
    // // 3. for each space, get user rank in this space:
    // joineds.forEach(new Consumer<Joined>() {
    // @Override
    // public void accept(Joined joindObj) {
    // var rank = joinedRepository.getUserSpaceRank(joindObj.getSpace().getId(),
    // userDetail.getId());
    // var countAllUsersInSpace = joinedRepository
    // .countBySpaceIdAndDeletedFalse(joindObj.getSpace().getId());
    // userSpaceRanking.add(
    // new UserSpaceRankingModel(joindObj.getSpace().getName(),
    // joindObj.getSpace().getThumbnail(),
    // rank != null ? rank : 0, countAllUsersInSpace != null ? countAllUsersInSpace
    // : 0));
    // }
    // });
    // }
    // return ResponseModel.done(userSpaceRanking);
    // } else {
    throw new NotPermittedException();
    // }
  }

  /** Created by A.Alsayed on 14/03/2019. */
  /** this method is used for Getting Top Users Ranking */
  @Transactional
  public ResponseModel getTopUsersRanking() {
    // 1. get current logged-in user:
    // ==============================
    // var userDetail = SecurityUtils.getCurrentUser();
    // if (userDetail != null) {
    // // 2. from joined table, get global ranking for logged-in user:
    // return ResponseModel.done(userRepository.getTopUsersRanking());
    // } else {
    throw new NotPermittedException();
    // }

  }

  public ResponseModel encodePasseword(String password) {
    return ResponseModel.done((Object) passwordEncoder.encode(password));
  }

  @Transactional
  @jakarta.annotation.PostConstruct
  protected void userInitializer() {
    userRepository
        .findOneByUserNameAndDeletedFalse("admin")
        .orElseGet(
            () -> {
              var user2 = new User();
              user2.setUserName("admin");
              user2.setFullName("mint adminstraor");
              user2.setType(UserType.SUPER_ADMIN);
              user2.setEmail("admin@edu-tek.net");
              user2.setStatus(Boolean.TRUE);
              user2.setPassword(passwordEncoder.encode("P@ssw0rd"));
              return userRepository.save(user2);
            });
  }
}
