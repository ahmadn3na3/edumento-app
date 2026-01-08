import { Component } from "@angular/core";
import { CommonModule } from "@angular/common";
import { Router, RouterLink } from "@angular/router";
import { FormsModule } from "@angular/forms";
import { HeaderComponent } from "../../layout/header/header.component";
import { SpaceService } from "../../core/services/space.service";
import { SpaceCreate } from "../../core/models/space.model";

@Component({
    selector: "app-create-space",
    standalone: true,
    imports: [CommonModule, RouterLink, FormsModule, HeaderComponent],
    templateUrl: "./create-space.component.html",
    styleUrl: "./create-space.component.css",
})
export class CreateSpaceComponent {
    space: SpaceCreate = {
        name: "",
        description: "",
        tags: [],
        isPrivate: false,
        joinRequestsAllowed: true,
        showCommunity: true,
        // defaults
        color: "#000000",
        image: "",
        thumbnail: "",
    };

    tagInput: string = "";
    selectedFile: File | null = null;
    imagePreview: string | ArrayBuffer | null = null;
    imageError: string | null = null;
    readonly MIN_WIDTH = 200;
    readonly MIN_HEIGHT = 200;

    // Cover Image
    selectedCoverFile: File | null = null;
    coverPreview: string | ArrayBuffer | null = null;
    coverError: string | null = null;
    readonly MIN_COVER_WIDTH = 1200; // Adjusted for cover aspect ratio
    readonly MIN_COVER_HEIGHT = 400;

    constructor(
        private spaceService: SpaceService,
        private router: Router,
    ) {}

    addTag() {
        if (this.tagInput.trim()) {
            if (!this.space.tags) {
                this.space.tags = [];
            }
            this.space.tags.push(this.tagInput.trim());
            this.tagInput = "";
        }
    }

    removeTag(index: number) {
        if (this.space.tags) {
            this.space.tags.splice(index, 1);
        }
    }

    triggerFileInput(fileInput: HTMLInputElement) {
        fileInput.click();
    }

    onFileSelected(event: any) {
        const file = event.target.files[0];
        if (file) {
            this.imageError = null;
            const reader = new FileReader();
            reader.onload = (e: any) => {
                const img = new Image();
                img.src = e.target.result;
                img.onload = () => {
                    if (
                        img.width > this.MIN_WIDTH ||
                        img.height > this.MIN_HEIGHT
                    ) {
                        this.imageError = `Image must be at least ${this.MIN_WIDTH}x${this.MIN_HEIGHT}px.`;
                        this.selectedFile = null;
                        this.imagePreview = null;
                    } else {
                        this.selectedFile = file;
                        this.imagePreview = e.target.result;
                    }
                };
            };
            reader.readAsDataURL(file);
        }
    }

    onCoverSelected(event: any) {
        const file = event.target.files[0];
        if (file) {
            this.coverError = null;
            const reader = new FileReader();
            reader.onload = (e: any) => {
                const img = new Image();
                img.src = e.target.result;
                img.onload = () => {
                    // Validate dimensions
                    if (
                        img.width > this.MIN_COVER_WIDTH ||
                        img.height > this.MIN_COVER_HEIGHT
                    ) {
                        this.coverError = `Cover image must be at least ${this.MIN_COVER_WIDTH}x${this.MIN_COVER_HEIGHT}px.`;
                        this.selectedCoverFile = null;
                        this.coverPreview = null;
                    } else {
                        this.selectedCoverFile = file;
                        this.coverPreview = e.target.result;
                    }
                };
            };
            reader.readAsDataURL(file);
        }
    }

    removeCover() {
        this.selectedCoverFile = null;
        this.coverPreview = null;
        this.coverError = null;
    }

    createSpace() {
        if (!this.space.name) {
            // Basic validation
            return;
        }

        if (this.selectedFile) {
            this.spaceService
                .uploadImage(this.selectedCoverFile!, this.selectedFile)
                .subscribe({
                    next: (res) => {
                        if (res && res.data) {
                            this.space.image = res.data.image;
                            if (res.data.thumbnail) {
                                this.space.thumbnail = res.data.thumbnail;
                            }
                        }
                        this.finalizeCreateSpace();
                    },
                    error: (err) => {
                        console.error("Failed to upload image", err);
                        alert(
                            "Failed to upload image. Please try again or continue without an image.",
                        );
                    },
                });
        } else {
            this.finalizeCreateSpace();
        }
    }

    finalizeCreateSpace() {
        this.spaceService.createSpace(this.space).subscribe({
            next: () => {
                this.router.navigate(["/spaces"]);
            },
            error: (err) => {
                console.error("Failed to create space", err);
                // TODO: Show user friendly error message
            },
        });
    }
}
