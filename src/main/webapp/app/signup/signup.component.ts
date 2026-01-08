import { Component } from "@angular/core";
import {
    FormBuilder,
    FormGroup,
    Validators,
    ReactiveFormsModule,
    FormsModule,
} from "@angular/forms";
import { Router, RouterLink } from "@angular/router";
import { LoginService } from "../core/auth/login.service";
import { CommonModule } from "@angular/common";

@Component({
    selector: "app-signup",
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule, RouterLink, FormsModule],
    templateUrl: "./signup.component.html",
    styleUrl: "./signup.component.css",
})
export class SignupComponent {
    signupForm: FormGroup;

    constructor(
        private formBuilder: FormBuilder,
        private loginService: LoginService,
        private router: Router,
    ) {
        this.signupForm = this.formBuilder.group({
            username: [
                "",
                [
                    Validators.required,
                    Validators.maxLength(50),
                    Validators.pattern("^[a-zA-Z0-9]*(@[a-zA-Z]*)?$"),
                ],
            ],
            fullName: ["", [Validators.required, Validators.maxLength(50)]],
            email: ["", [Validators.required, Validators.email]],
            password: [
                "",
                [
                    Validators.required,
                    Validators.minLength(8),
                    Validators.maxLength(24),
                ],
            ],
            confirmPassword: ["", Validators.required],
        });
    }

    signup() {
        if (this.signupForm.invalid) {
            return;
        }
        const { password, confirmPassword } = this.signupForm.value;
        if (password !== confirmPassword) {
            // TODO: Show mismatch error
            console.error("Passwords do not match");
            return;
        }

        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const { confirmPassword: _, ...registerPayload } =
            this.signupForm.value;

        this.loginService.register(registerPayload).subscribe({
            next: () => {
                alert(
                    "Registration successful! Please check the backend console for the activation key to activate your account.",
                );
                this.router.navigate(["/activate"]);
            },
            error: (err) => {
                console.error("Signup failed", err);
            },
        });
    }
}
