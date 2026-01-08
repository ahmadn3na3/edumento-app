import { Component, OnInit } from "@angular/core";
import { ActivatedRoute, RouterModule } from "@angular/router";

import { HeaderComponent } from "../../layout/header/header.component";
import { Space } from "../../core/models/space.model";
import { SpaceService } from "../../core/services/space.service";
import { DiscussionListComponent } from "../../discussion/discussion-list/discussion-list.component";
import { AssessmentListComponent } from "../../assessment/assessment-list/assessment-list.component";
import { ContentListComponent } from "../../content/content-list/content-list.component";

@Component({
    selector: "app-space-detail",
    standalone: true,
    imports: [
        RouterModule,
        HeaderComponent,
        DiscussionListComponent,
        AssessmentListComponent,
        ContentListComponent,
    ],
    templateUrl: "./space-detail.component.html",
    styleUrl: "./space-detail.component.css",
})
export class SpaceDetailComponent implements OnInit {
    activeTab: string = "overview";
    space: Space | null = null;
    loading = true;
    error: string | null = null;

    constructor(
        private route: ActivatedRoute,
        private spaceService: SpaceService,
    ) {}

    ngOnInit() {
        this.route.paramMap.subscribe((params) => {
            const id = Number(params.get("id"));
            if (id) {
                this.loadSpace(id);
            }
        });
    }

    loadSpace(id: number) {
        this.loading = true;
        this.spaceService.getSpaceById(id).subscribe({
            next: (space) => {
                this.space = space;
                this.loading = false;
            },
            error: (err) => {
                console.error("Error loading space:", err);
                this.error = "Failed to load space details.";
                this.loading = false;
            },
        });
    }

    setActiveTab(tab: string) {
        this.activeTab = tab;
    }
}
