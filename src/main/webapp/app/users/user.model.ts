import { Gender } from "../constant/gender.enum";
import { UserType } from "../constant/usertype.enum";

//UserCreateModel.java 
export class UserCreatedModel {
	/*@Pattern(regexp = "^[a-zA-Z0-9]*(@[A-Za-z]*)?$", message = "error.login.pattern")
		@NotNull(message = "error.login.null")
		@Size(max = 50, message = "error.login.length")
		@NotEmpty()*/
	username: string;

	/*	@NotNull(message = "error.fname.null")
		@Size(max = 50, message = "error.fname.length")
		@NotEmpty*/
	fullName: string;
	/*
		@NotNull(message = "error.email.null")
		@Email(message = "error.email.invalid")
		@NotEmpty*/
	email: string;

	mobile!: string;

	gender: Gender = Gender.MALE;

	type: UserType = UserType.USER;


	birthDate!: Date;
	profession!: string;
	country!: string;
	userStatus!: string;
	interests!: string;
	imageUrl!: string;
	lang: string = "en";
	notification: boolean = true;
	emailNotification: boolean = true;

	//initialization
	constructor(username: string, fullName: string, email: string) {
		this.username = username;
		this.fullName = fullName;
		this.email = email;
	}
}

export class UserModel extends UserCreatedModel {
	id: number;
	constructor(id: number, username: string, fullName: string, email: string) {
		super(username, fullName, email);
		this.id = id;
	}
}
