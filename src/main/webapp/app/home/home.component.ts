import { Component, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { RouterLink, RouterOutlet } from '@angular/router';
import { LoginService } from '../login/login.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrl: './home.component.css',
  imports: [

    RouterOutlet,
  ],
  providers: [
    LoginService
  ]
})
export class HomeComponent {
  private loginService = inject(LoginService);

  isLoggedIn$: Observable<boolean> = this.loginService.isLoggedIn$;
  isSidebarOpen = true;

  toggleSidebar() {
    this.isSidebarOpen = !this.isSidebarOpen;
  }

  logout() {
    this.loginService.logout();
  }
}
