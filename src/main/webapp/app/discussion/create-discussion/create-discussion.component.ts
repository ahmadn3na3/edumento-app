import { Component, inject, OnInit } from "@angular/core";
import { CommonModule, Location } from "@angular/common";
import {
    FormBuilder,
    FormGroup,
    ReactiveFormsModule,
    Validators,
} from "@angular/forms";
import { ActivatedRoute, Router, RouterModule } from "@angular/router";
import { DiscussionService } from "../services/discussion.service";
import { DiscussionType } from "../models/discussion.model";
import { HeaderComponent } from "../../layout/header/header.component";

@Component({
    selector: "app-create-discussion",
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule, RouterModule, HeaderComponent],
    templateUrl: "./create-discussion.component.html",
    styleUrl: "./create-discussion.component.css",
})
export class CreateDiscussionComponent implements OnInit {
    discussionForm!: FormGroup;
    spaceId!: number;
    loading = false;
    error = "";
    DiscussionType = DiscussionType;

    constructor(
        private fb: FormBuilder,
        private route: ActivatedRoute,
        private router: Router,
        private discussionService: DiscussionService,
        private location: Location,
    ) {}

    ngOnInit() {
        this.spaceId =
            Number(this.route.parent?.snapshot.paramMap.get("id")) ||
            Number(this.route.snapshot.paramMap.get("id"));

        // If route doesn't have ID directly, we might need a better way to get it depending on route structure.
        // Assuming route structure is spaces/:id/discussion/create, then id is in parent or params.

        this.initForm();
    }

    initForm() {
        this.discussionForm = this.fb.group({
            title: ["", [Validators.required]],
            body: ["", [Validators.required]], // This will be the "Details" textarea
            category: ["DISCUSSION", [Validators.required]], // Mapped to DiscussionType
            // visibility: ['Everyone'], // Not in backend model yet, UI only for now
            // pin: [false], // Not in backend
            // lock: [false] // Not in backend
        });
    }

    onSubmit() {
        if (this.discussionForm.invalid) {
            return;
        }

        this.loading = true;
        this.error = "";

        const formValue = this.discussionForm.value;
        const discussionData = {
            title: formValue.title,
            body: formValue.body,
            spaceId: this.spaceId,
            type:
                formValue.category === "Q&A"
                    ? DiscussionType.INQUIRY
                    : DiscussionType.DISCUSSION,
            // Mapping "Announcement" to Discussion for now as backend enum only has DISCUSSION/INQUIRY?
            // Actually backend enum has: DISCUSSION, INQUIRY.
            // "General Discussion" -> DISCUSSION
            // "Q&A" -> INQUIRY
            // "Announcement" -> DISCUSSION (or maybe title prefix?)
        };

        this.discussionService.createDiscussion(discussionData).subscribe({
            next: () => {
                this.loading = false;
                this.router.navigate(["/spaces", this.spaceId]); // Navigate back to space details default or discussions tab
            },
            error: (err) => {
                this.loading = false;
                this.error = "Failed to create discussion";
                console.error(err);
            },
        });
    }

    onCancel() {
        this.location.back();
    }
}
