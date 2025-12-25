import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Space } from '../../core/models/space.model';
import { RouterLink } from "@angular/router";

@Component({
    selector: 'app-space-card',
    standalone: true,
    imports: [CommonModule, RouterLink],
    templateUrl: './space-card.component.html',
    styleUrl: './space-card.component.css'
})
export class SpaceCardComponent {
    @Input() space!: Space;
}
