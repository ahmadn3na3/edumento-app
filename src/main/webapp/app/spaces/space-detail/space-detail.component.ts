import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';

import { HeaderComponent } from "../../layout/header/header.component";
import { Space } from '../../core/models/space.model';
import { MOCK_SPACE } from '../../core/models/mock-data';

@Component({
  selector: 'app-space-detail',
  standalone: true,
  imports: [RouterModule, HeaderComponent],
  templateUrl: './space-detail.component.html',
  styleUrl: './space-detail.component.css'
})
export class SpaceDetailComponent {
  activeTab: string = 'overview';
  space: Space = MOCK_SPACE;

  setActiveTab(tab: string) {
    this.activeTab = tab;
  }
}
