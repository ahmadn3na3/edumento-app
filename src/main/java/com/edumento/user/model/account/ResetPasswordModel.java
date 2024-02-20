package com.edumento.user.model.account;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

/** Created by ahmad on 3/3/16. */
public class ResetPasswordModel {

	@NotNull(message = "error.email.null")
	@Email
	private String mail;

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	@Override
	public String toString() {
		return "ResetPasswordModel{" + "mail='" + mail + '\'' + '}';
	}
}
