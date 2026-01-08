import { Component } from '@angular/core';

import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { LoginService } from '../core/auth/login.service';
import { Router } from '@angular/router';


@Component({
	selector: 'app-login',
	imports: [FormsModule, ReactiveFormsModule],
	templateUrl: './login.component.html',
	styleUrl: './login.component.css'
})
export class LoginComponent {

	form: FormGroup;


	constructor(private loginService: LoginService, private formBuilder: FormBuilder, private router: Router) {
		if (this.loginService.isLoggedIn()) {
			this.router.navigate(['/spaces']);
		}
		this.form = this.formBuilder.group({
			username: [''],
			password: ['']
		});
	}

	login() {
		this.loginService.authenticateUser(this.form.getRawValue()).subscribe((response) => {
			console.log('response', response);
			this.router.navigate(['/spaces']);
		});

	}
}