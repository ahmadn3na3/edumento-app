import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { Space, SpaceCreate } from '../models/space.model';
import { MOCK_SPACE } from '../models/mock-data';

@Injectable({
    providedIn: 'root'
})
export class SpaceService {
    // Initialize with some mock data
    private spaces = new BehaviorSubject<Space[]>([MOCK_SPACE, MOCK_SPACE, MOCK_SPACE]);
    private apiUrl = '/api/space';

    constructor(private http: HttpClient) { }

    getSpaces(): Observable<Space[]> {
        return this.spaces.asObservable();
    }

    createSpace(newSpace: SpaceCreate): Observable<Space> {
        return this.http.post<any>(this.apiUrl, newSpace).pipe(
            tap((response) => {
                // Assuming the backend returns a wrapper ResponseModel where 'data' is the ID or similar, 
                // but based on typical REST, we might want to fetch the created space or construct it.
                // The backend returns: ResponseModel.done(space.getId(), new SpaceInfoMessage(...))
                // The SpaceInfoMessage likely contains the displayable data.
                // For now, let's just log it and maybe add to the local list if we can map it.
                console.log('Space created:', response);

                // TODO: Ideally we should fetch the full space or map the response to Space
                // For now, we will just rely on the component reloading or similar. 
                // If we want to optimistically update the list:
                // const createdSpace = ... (map response to Space)
                // this.spaces.next([...this.spaces.value, createdSpace]);
            })
        );
    }
}
