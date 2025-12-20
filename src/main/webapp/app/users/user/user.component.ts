import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { UserType } from '../../constant/usertype.enum';
import { Gender } from '../../constant/gender.enum';
import { CommonModule, Location } from '@angular/common';

@Component({
	selector: 'app-user',
	imports: [ReactiveFormsModule, CommonModule],
	templateUrl: './user.component.html',
	styleUrl: './user.component.css'
})
export class UserComponent {
	userTypes = Object.keys(UserType);
	genders = Object.keys(Gender);

	form: FormGroup;
	constructor(private fb: FormBuilder, private _location: Location) {
		this.form = this.fb.group({
			username: ['', Validators.pattern(/^[a-zA-Z0-9]*(@[A-Za-z]*)?$/)],
			email: ['', [Validators.required, Validators.email]],
			firstName: ['', [Validators.required, Validators.maxLength(25)]],
			lastName: ['', [Validators.required, Validators.maxLength(25)]],
			gender: ['', Validators.required],
			userType: ['', Validators.required],
			password: ['', Validators.required],


		});
	}
	saveUser() {

	}
	cancel() {
		this._location.back();

	}


}
