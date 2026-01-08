import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { ContentService } from '../services/content.service';
import { Content } from '../models/content.model';

@Component({
    selector: 'app-content-list',
    standalone: true,
    imports: [CommonModule, DatePipe],
    templateUrl: './content-list.component.html',
    styles: []
})
export class ContentListComponent implements OnInit, OnChanges {
    @Input() spaceId!: number;
    contents: Content[] = [];
    loading = false;
    error = '';

    constructor(private contentService: ContentService) { }

    ngOnInit() {
        if (this.spaceId) {
            this.loadContents();
        }
    }

    ngOnChanges(changes: SimpleChanges) {
        if (changes['spaceId'] && this.spaceId) {
            this.loadContents();
        }
    }

    loadContents() {
        this.loading = true;
        this.error = '';
        this.contentService.getContents(this.spaceId).subscribe({
            next: (data) => {
                this.contents = data;
                this.loading = false;
            },
            error: (err) => {
                this.error = 'Failed to load content';
                this.loading = false;
                console.error(err);
            }
        });
    }
}
