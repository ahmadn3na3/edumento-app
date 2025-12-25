import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

import { HeaderComponent } from "../layout/header/header.component";
import { User } from '../core/models/user.model';
import { MOCK_USER } from '../core/models/mock-data';

@Component({
  selector: 'app-user-profile',
  standalone: true,
  imports: [CommonModule, HeaderComponent],
  templateUrl: './user-profile.component.html',
  styleUrl: './user-profile.component.css'
})
export class UserProfileComponent {
  activeTab: string = 'settings';
  user: User = MOCK_USER;

  setActiveTab(tab: string) {
    this.activeTab = tab;
  }
}
