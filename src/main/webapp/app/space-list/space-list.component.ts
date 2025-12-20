import { Component } from '@angular/core';
import { DatePipe } from '@angular/common';

@Component({
    selector: 'app-space-list',
    imports: [DatePipe],
    templateUrl: './space-list.component.html',
    styleUrl: './space-list.component.css'
})
export class SpaceListComponent {
    spaces: any[] = []; // Replace with actual model interface if available

    constructor() {
        // Mock data for now or fetch from service if available
        this.spaces = [
            { id: 1, name: 'Space 1', thumbnail: 'assets/space1.jpg', rating: 4, favorite: false },
            { id: 2, name: 'Space 2', thumbnail: 'assets/space2.jpg', rating: 5, favorite: true },
            { id: 3, name: 'Space 3', thumbnail: 'assets/space3.jpg', rating: 3, favorite: false }
        ];
    }

    goToSpaceDetails(id: number) {
        console.log('Navigate to space', id);
    }

    toggleFavorite(space: any) {
        space.favorite = !space.favorite;
    }
}
