import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { AssessmentService } from '../services/assessment.service';
import { AssessmentList } from '../models/assessment.model';

@Component({
    selector: 'app-assessment-list',
    standalone: true,
    imports: [CommonModule, DatePipe],
    templateUrl: './assessment-list.component.html',
    styles: [] // Add styles if needed
})
export class AssessmentListComponent implements OnInit, OnChanges {
    @Input() spaceId!: number;
    assessments: AssessmentList[] = [];
    loading = false;
    error = '';

    constructor(private assessmentService: AssessmentService) { }

    ngOnInit() {
        if (this.spaceId) {
            this.loadAssessments();
        }
    }

    ngOnChanges(changes: SimpleChanges) {
        if (changes['spaceId'] && this.spaceId) {
            this.loadAssessments();
        }
    }

    loadAssessments() {
        this.loading = true;
        this.error = '';
        this.assessmentService.getAssessments(this.spaceId).subscribe({
            next: (data) => {
                this.assessments = data;
                this.loading = false;
            },
            error: (err) => {
                this.error = 'Failed to load assessments';
                this.loading = false;
                console.error(err);
            }
        });
    }
}
