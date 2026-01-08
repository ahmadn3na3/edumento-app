package com.edumento.core.security;

import com.edumento.user.constant.UserType;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

/** Created by ahmad on 12/1/16. */
public class CurrentUserDetail extends User {
  /** */
  private static final long serialVersionUID = -7508965931175219258L;

  private final Long id;
  private final String fullName;
  private final String email;
  private final String image;

  private final UserType type;
  private String currentClientId;
  private String chatId;

  public CurrentUserDetail(
      Long id,
      String username,
      String password,
      Collection<? extends GrantedAuthority> authorities,
      String fullName,
      String image,
      String email,
      UserType type,
      String chatId) {
    this(
        id,
        username,
        password,
        true,
        true,
        true,
        true,
        authorities,
        fullName,
        email,
        image,
        type,
        chatId);
  }

  public CurrentUserDetail(
      Long id,
      String username,
      String password,
      boolean enabled,
      boolean accountNonExpired,
      boolean credentialsNonExpired,
      boolean accountNonLocked,
      Collection<? extends GrantedAuthority> authorities,
      String fullName,
      String email,
      String image,
      UserType type,
      String chatId) {
    super(
        username,
        password,
        enabled,
        accountNonExpired,
        credentialsNonExpired,
        accountNonLocked,
        authorities);
    this.id = id;
    this.fullName = fullName;
    this.email = email;
    this.image = image;
    this.type = type;
    setChatId(chatId);
  }

  public Long getId() {
    return id;
  }

  public UserType getType() {
    return type;
  }

  public String getFullName() {
    return fullName;
  }

  public String getEmail() {
    return email;
  }

  public String getImage() {
    return image;
  }

  public String getCurrentClientId() {
    return currentClientId;
  }

  public void setCurrentClientId(String currentClient) {
    currentClientId = currentClient;
  }

  public String getChatId() {
    return chatId;
  }

  public void setChatId(String chatId) {
    this.chatId = chatId;
  }
}
