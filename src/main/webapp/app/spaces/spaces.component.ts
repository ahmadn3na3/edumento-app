import { Component } from '@angular/core';
import { HeaderComponent } from "../layout/header/header.component";
import { Space } from '../core/models/space.model';
import { MOCK_SPACE } from '../core/models/mock-data';
import { SpaceCardComponent } from './space-card/space-card.component';
import { JoinSpaceCardComponent } from './join-space-card/join-space-card.component';

@Component({
  selector: 'app-spaces',
  standalone: true,
  imports: [HeaderComponent, SpaceCardComponent, JoinSpaceCardComponent],
  templateUrl: './spaces.component.html',
  styleUrl: './spaces.component.css'
})
export class SpacesComponent {
  spaces: Space[] = [MOCK_SPACE, MOCK_SPACE, MOCK_SPACE];
}
