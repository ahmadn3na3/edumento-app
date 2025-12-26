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
    this.spaceService.getSpaces().subscribe(spaces => {
      this.spaces = spaces;
    });
  }
}
