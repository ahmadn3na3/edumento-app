import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Content } from '../models/content.model';
import { ResponseModel } from '../../core/models/response.model';

@Injectable({
    providedIn: 'root'
})
export class ContentService {
    private apiUrl = '/api/content';

    constructor(private http: HttpClient) { }

    getContents(spaceId: number): Observable<Content[]> {
        return this.http.get<ResponseModel<Content[]>>(`${this.apiUrl}/space/${spaceId}`).pipe(
            map(response => response.data || [])
        );
    }
}
