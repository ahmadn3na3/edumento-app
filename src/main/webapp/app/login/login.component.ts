import { Component } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { LoginService } from './login.service';


@Component({
	selector: 'app-login',
	standalone: true,
	imports: [MatInputModule, MatButtonModule, MatCardModule, MatFormFieldModule, FormsModule,ReactiveFormsModule],
	templateUrl: './login.component.html',
	styleUrl: './login.component.css',
	providers: [LoginService]
})
export class LoginComponent {
	
	form: FormGroup;
	

	constructor(private loginService: LoginService,private formBuilder: FormBuilder) {
		
		this.form = this.formBuilder.group({
			username: [''],
			password: ['']
		});
	}

	login() {
		this.loginService.authenticateUser(this.form.getRawValue()).subscribe((response) => {
			console.log('response', response);
		})

	}
}