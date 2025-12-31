import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { LoginService } from '../core/auth/login.service';

@Component({
    selector: 'app-activation',
    templateUrl: './activation.component.html',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule, RouterModule]
})
export class ActivationComponent implements OnInit {
    error = false;
    success = false;
    activationForm: FormGroup;

    constructor(
        private loginService: LoginService,
        private route: ActivatedRoute,
        private router: Router,
        private fb: FormBuilder
    ) {
        this.activationForm = this.fb.group({
            key: ['', Validators.required]
        });
    }

    ngOnInit() {
        this.route.queryParams.subscribe(params => {
            const key = params['key'];
            if (key) {
                this.activationForm.patchValue({ key });
            }
        });
    }

    activate() {
        if (this.activationForm.invalid) {
            return;
        }
        
        const key = this.activationForm.get('key')?.value;
        this.loginService.activate(key).subscribe({
            next: () => {
                this.error = false;
                this.success = true;
                setTimeout(() => this.router.navigate(['/login']), 3000);
            },
            error: () => {
                this.success = false;
                this.error = true;
            }
        });
    }
}
