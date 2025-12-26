import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Space, SpaceCreate } from '../models/space.model';
import { MOCK_SPACE } from '../models/mock-data';

@Injectable({
    providedIn: 'root'
})
export class SpaceService {
    // Initialize with some mock data
    private spaces = new BehaviorSubject<Space[]>([MOCK_SPACE, MOCK_SPACE, MOCK_SPACE]);

    constructor() { }

    getSpaces(): Observable<Space[]> {
        return this.spaces.asObservable();
    }

    createSpace(newSpace: SpaceCreate): Observable<Space> {
        // Mock implementation: Create a full Space object from SpaceCreate
        const spaceToCreate: Space = {
            id: Math.floor(Math.random() * 10000), // Random ID
            name: newSpace.name,
            description: newSpace.description || '',
            color: newSpace.color || '#000000',
            image: newSpace.image || 'https://lh3.googleusercontent.com/aida-public/AB6AXuDmL6AoI-y_5iPdYmh7qOs_BB5t6q-m6dPdDAts9YXkW0jFNhhLWk-qUR_JNT_984mGkcPUkq0AhY9GVo9X5ZDIyiKD4DL5m8ji_SwVpzPrW2WoFnF6RaPNCReCqo_nqyg3aBUHC0FvGTu9uR11KAgoaiLZDAQnJFz0wcYZdAFmUkKd5r449bXCeRVRwvv11dnSJVVJ-IRNaWde80gVJjLlpz9hMBT3NpniFOBKA0sjoZ7xOrPrn2ezUNMzJvvjF1k4PkOp_9xgLOo', // Default image
            thumbnail: newSpace.thumbnail || '',
            isPrivate: newSpace.isPrivate || false,
            rating: 0,

            // Default Status flags
            newContent: false,
            newAssessment: false,
            newComments: false,
            favorite: false,
            owner: true, // Creator is owner

            // Default Counts & Settings
            communitySize: 1,
            contentSize: 0,
            joinRequestsAllowed: newSpace.joinRequestsAllowed || true,
            autoWifiSyncAllowed: newSpace.autoWifiSyncAllowed || false,
            showCommunity: newSpace.showCommunity || true,
            allowRecommendation: newSpace.allowRecommendation || true,
            allowLeave: newSpace.allowLeave || true,
            joinedStatus: 'JOINED',

            tags: newSpace.tags || [],
            creationDate: newSpace.creationDate || new Date().toISOString(),
            lastModified: new Date().toISOString(),
            lastAccessed: new Date().toISOString(),

            // Relations (Mocked)
            creator: { id: 1, name: 'CurrentUser', thumbnail: '', role: 'OWNER' },
            community: [],
            role: 'OWNER',
            permissions: { 'WRITE': 1 }
        };

        const currentSpaces = this.spaces.value;
        this.spaces.next([...currentSpaces, spaceToCreate]);

        // Return the created space (as observable to mimic API)
        return new BehaviorSubject(spaceToCreate).asObservable();
    }
}
