import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { HeaderComponent } from '../../layout/header/header.component';
import { SpaceService } from '../../core/services/space.service';
import { SpaceCreate } from '../../core/models/space.model';

@Component({
    selector: 'app-create-space',
    standalone: true,
    imports: [CommonModule, RouterLink, FormsModule, HeaderComponent],
    templateUrl: './create-space.component.html',
    styleUrl: './create-space.component.css'
})
export class CreateSpaceComponent {
    space: SpaceCreate = {
        name: '',
        description: '',
        tags: [],
        isPrivate: false,
        joinRequestsAllowed: true,
        showCommunity: true,
        // defaults
        color: '#000000',
        image: '',
        thumbnail: ''
    };

    tagInput: string = '';

    constructor(
        private spaceService: SpaceService,
        private router: Router
    ) { }

    addTag() {
        if (this.tagInput.trim()) {
            if (!this.space.tags) {
                this.space.tags = [];
            }
            this.space.tags.push(this.tagInput.trim());
            this.tagInput = '';
        }
    }

    removeTag(index: number) {
        if (this.space.tags) {
            this.space.tags.splice(index, 1);
        }
    }

    createSpace() {
        if (!this.space.name) {
            // Basic validation
            return;
        }

        this.spaceService.createSpace(this.space).subscribe(() => {
            this.router.navigate(['/spaces']);
        });
    }
}
