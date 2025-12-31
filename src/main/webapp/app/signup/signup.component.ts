import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { LoginService } from '../core/auth/login.service';
import { CommonModule } from '@angular/common';

@Component({
    selector: 'app-signup',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule, RouterLink, FormsModule],
    templateUrl: './signup.component.html',
    styleUrl: './signup.component.css'
})
export class SignupComponent {
    signupForm: FormGroup;

    constructor(
        private formBuilder: FormBuilder,
        private loginService: LoginService,
        private router: Router
    ) {
        this.signupForm = this.formBuilder.group({
            fullName: ['', Validators.required],
            email: ['', [Validators.required, Validators.email]],
            password: ['', Validators.required],
            confirmPassword: ['', Validators.required]
        });
    }

    signup() {
        if (this.signupForm.invalid) {
            return;
        }
        const { password, confirmPassword } = this.signupForm.value;
        if (password !== confirmPassword) {
            // TODO: Show mismatch error
            console.error('Passwords do not match');
            return;
        }

        this.loginService.register(this.signupForm.value).subscribe({
            next: () => {
                this.router.navigate(['/login']);
            },
            error: (err) => {
                console.error('Signup failed', err);
            }
        });
    }
}
