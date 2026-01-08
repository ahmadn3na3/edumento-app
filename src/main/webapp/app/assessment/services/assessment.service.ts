import { Injectable } from "@angular/core";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Observable } from "rxjs";
import { map } from "rxjs/operators";
import { AssessmentList } from "../models/assessment.model";
import { ResponseModel } from "../../core/models/response.model";

@Injectable({
    providedIn: "root",
})
export class AssessmentService {
    private apiUrl = "/api/assessment";

    constructor(private http: HttpClient) {}

    getAssessments(spaceId: number): Observable<AssessmentList[]> {
        const headers = new HttpHeaders().set("spaceId", spaceId.toString());
        return this.http
            .get<ResponseModel<AssessmentList[]>>(this.apiUrl, { headers })
            .pipe(map((response) => response.data || []));
    }
}
