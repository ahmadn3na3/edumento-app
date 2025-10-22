import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { UserType } from '../../constant/usertype.enum';
import { Gender } from '../../constant/gender.enum';
import { CommonModule, Location } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';

@Component({
    selector: 'app-user',
    imports: [MatFormFieldModule, MatInputModule, ReactiveFormsModule, MatSelectModule, CommonModule, MatButtonModule],
    templateUrl: './user.component.html',
    styleUrl: './user.component.css'
})
export class UserComponent {
	userTypes = Object.keys(UserType);
	genders = Object.keys(Gender);

	form: FormGroup;
	constructor(private fb: FormBuilder,private _location:Location) {
		this.form = this.fb.group({
			username: ['', Validators.pattern(/^[a-zA-Z0-9]*(@[A-Za-z]*)?$/)],
			email: ['', [Validators.required,Validators.email]],
			firstName: ['',[Validators.required,Validators.maxLength(25)]],
			lastName: ['',[Validators.required,Validators.maxLength(25)]],
			gender: ['', Validators.required],
			userType: ['', Validators.required],
			password: ['', Validators.required],
			

		});
	}
	saveUser(){
		
	}
	cancel(){
		this._location.back();
		
	}


}
