import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { HomeComponent } from './home/home.component';
import { SpacesComponent } from './spaces/spaces.component';
import { inject } from '@angular/core';
import { LoginService } from './login/login.service';

export const routes: Routes = [
	{
		path: 'login',

		component: LoginComponent,
		data: { title: 'Login' }
	},
	{
		path: '',
		redirectTo: '/spaces',
		pathMatch: 'full'
	},
	{
		path: 'spaces',
		component: SpacesComponent,
		// canActivate: [() => { return inject(LoginService).isLoggedIn(); }],
		data: { title: 'My Spaces' }
	},


];
