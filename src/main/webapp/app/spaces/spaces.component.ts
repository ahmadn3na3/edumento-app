import { Component, OnInit } from '@angular/core';
import { HeaderComponent } from "../layout/header/header.component";
import { Space } from '../core/models/space.model';
import { SpaceService } from '../core/services/space.service';
import { SpaceCardComponent } from './space-card/space-card.component';
import { JoinSpaceCardComponent } from './join-space-card/join-space-card.component';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-spaces',
  standalone: true,
  imports: [HeaderComponent, SpaceCardComponent, JoinSpaceCardComponent, RouterLink],
  templateUrl: './spaces.component.html',
  styleUrl: './spaces.component.css'
})
export class SpacesComponent implements OnInit {
  spaces: Space[] = [];

  constructor(private spaceService: SpaceService) { }

  ngOnInit() {
    this.spaceService.getSpaces().subscribe({
      next: (spaces) => {
        this.spaces = spaces;
        console.log('Spaces loaded:', spaces);
      },
      error: (err) => {
        console.error('Error loading spaces:', err);
        // Optional: set an error message property to display in the UI
      }
    });
  }
}
