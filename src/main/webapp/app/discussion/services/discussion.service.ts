import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { map } from "rxjs/operators";
import { DiscussionSummary, DiscussionCreateModel } from "../models/discussion.model";
import { ResponseModel } from "../../core/models/response.model";

@Injectable({
    providedIn: "root",
})
export class DiscussionService {
    private apiUrl = "/api/discussion";

    constructor(private http: HttpClient) { }

    createDiscussion(discussion: DiscussionCreateModel): Observable<DiscussionSummary> {
        return this.http
            .post<ResponseModel<DiscussionSummary>>(this.apiUrl, discussion)
            .pipe(map((response) => response.data!));
    }

    getDiscussions(spaceId: number): Observable<DiscussionSummary[]> {
        return this.http
            .get<
                ResponseModel<DiscussionSummary[]>
            >(`${this.apiUrl}/listAll/${spaceId}`)
            .pipe(map((response) => response.data || []));
    }
}
