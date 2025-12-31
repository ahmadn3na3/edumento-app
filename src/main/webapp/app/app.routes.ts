import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { HomeComponent } from './home/home.component';
import { SpacesComponent } from './spaces/spaces.component';
import { inject } from '@angular/core';
import { LoginService } from './core/auth/login.service';

export const routes: Routes = [
	{
		path: 'login',

		component: LoginComponent,
		data: { title: 'Login' }
	},
	{
		path: 'signup',
		loadComponent: () => import('./signup/signup.component').then(m => m.SignupComponent),
		data: { title: 'Sign Up' }
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
	{
		path: 'spaces/create',
		loadComponent: () => import('./spaces/create-space/create-space.component').then(m => m.CreateSpaceComponent),
		data: { title: 'Create Space' }
	},
	{
		path: 'spaces/:id',
		loadComponent: () => import('./spaces/space-detail/space-detail.component').then(m => m.SpaceDetailComponent),
		data: { title: 'Space Details' }
	},
	{
		path: 'profile',
		loadComponent: () => import('./user-profile/user-profile.component').then(m => m.UserProfileComponent),
		data: { title: 'Account Settings' }
	}


];
