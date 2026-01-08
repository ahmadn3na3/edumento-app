import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable, map, tap } from "rxjs";
import { Space, SpaceCreate } from "../models/space.model";
import { PageResponseModel, ResponseModel } from "../models/response.model";

@Injectable({
    providedIn: "root",
})
export class SpaceService {
    private apiUrl = "/api/space";

    constructor(private http: HttpClient) {}

    getSpaces(): Observable<Space[]> {
        return this.http
            .get<PageResponseModel<Space[]>>(this.apiUrl)
            .pipe(map((response) => response.data || []));
    }

    getSpaceById(id: number): Observable<Space> {
        return this.http
            .get<ResponseModel<any>>(`${this.apiUrl}/${id}`)
            .pipe(map((response) => response.data));
    }

    createSpace(newSpace: SpaceCreate): Observable<Space> {
        return this.http.post<any>(this.apiUrl, newSpace).pipe(
            tap((response) => {
                console.log("Space created:", response);
            }),
        );
    }

    uploadImage(image: File, thumbnail?: File): Observable<any> {
        const formData = new FormData();
        formData.append("image", image);
        if (thumbnail) {
            formData.append("thumbnail", thumbnail);
        }
        return this.http.post<any>("/upload/image", formData);
    }
}
