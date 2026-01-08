import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { DiscussionService } from '../services/discussion.service';
import { DiscussionSummary } from '../models/discussion.model';

@Component({
    selector: 'app-discussion-list',
    standalone: true,
    imports: [CommonModule, DatePipe],
    templateUrl: './discussion-list.component.html',
    styles: [`
    .avatar-sm { width: 32px; height: 32px; }
    .avatar-xs { width: 24px; height: 24px; }
  `]
})
export class DiscussionListComponent implements OnInit, OnChanges {
    @Input() spaceId!: number;
    discussions: DiscussionSummary[] = [];
    loading = false;
    error = '';

    constructor(private discussionService: DiscussionService) { }

    ngOnInit() {
        if (this.spaceId) {
            this.loadDiscussions();
        }
    }

    ngOnChanges(changes: SimpleChanges) {
        if (changes['spaceId'] && this.spaceId) {
            this.loadDiscussions();
        }
    }

    loadDiscussions() {
        this.loading = true;
        this.error = '';
        this.discussionService.getDiscussions(this.spaceId).subscribe({
            next: (data) => {
                this.discussions = data;
                this.loading = false;
            },
            error: (err) => {
                this.error = 'Failed to load discussions';
                this.loading = false;
                console.error(err);
            }
        });
    }
}
