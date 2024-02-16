package com.edumento.core.model.messages.user;

import java.io.Serializable;
import com.edumento.core.model.SimpleModel;
import com.edumento.user.domain.User;

public class UserInfoMessage extends SimpleModel implements Serializable {

  /** */
  private static final long serialVersionUID = 3003204323734710145L;

  private String login;
  private String resetKey;
  private String activationKey;
  private String password;
  private String lang;
  private String image;
  private String email;
  private String chatId;
  private Boolean notification;
  private Boolean mailNotification;

  public UserInfoMessage() {
    // TODO Auto-generated constructor stub
  }

  public UserInfoMessage(User user, String password) {
    this(user, password, null);
  }

  public UserInfoMessage(User user) {
    this(user, null, null);
  }

  public UserInfoMessage(User user, String password, String lang) {
    super(user.getId(), user.getFullName());
    this.login = user.getUserName();
    this.resetKey = user.getResetKey();
    this.activationKey = user.getActivationKey();
    this.password = password;
    this.lang = lang == null ? user.getLangKey() : lang;
    this.email = user.getEmail();
    this.image = user.getThumbnail();
    this.chatId = user.getChatId();
    this.notification = user.getNotification();
    this.mailNotification = user.getMailNotification();
  }

  public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    this.login = login;
  }

  public String getResetKey() {
    return resetKey;
  }

  public void setResetKey(String resetKey) {
    this.resetKey = resetKey;
  }

  public String getActivationKey() {
    return activationKey;
  }

  public void setActivationKey(String activationKey) {
    this.activationKey = activationKey;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getLang() {
    if (lang == null) lang = "en";
    return lang;
  }

  public void setLang(String lang) {
    this.lang = lang;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public String getChatId() {
    return chatId;
  }

  public void setChatId(String chatId) {
    this.chatId = chatId;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((email == null) ? 0 : email.hashCode());
    result = prime * result + ((login == null) ? 0 : login.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    UserInfoMessage other = (UserInfoMessage) obj;
    if (email == null) {
      if (other.email != null) return false;
    } else if (!email.equals(other.email)) return false;
    if (login == null) {
        return other.login == null;
    } else return login.equals(other.login);
  }

public Boolean getNotification() {
	return notification;
}

public void setNotification(Boolean notification) {
	this.notification = notification;
}

public Boolean getMailNotification() {
	return mailNotification;
}

public void setMailNotification(Boolean mailNotification) {
	this.mailNotification = mailNotification;
}
}
