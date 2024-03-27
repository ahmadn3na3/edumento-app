import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { MainDashboardComponent } from './main-dashboard/main-dashboard.component';
import { LoginService } from './login/login.service';
import { ModulesComponent } from './modules/modules.component';
import { inject } from '@angular/core';
import { ModuleComponent } from './modules/module/module.component';
import { UsersComponent } from './users/users.component';
import { UserComponent } from './users/user/user.component';

export const routes: Routes = [
	{
		path: 'login',

		component: LoginComponent,
		data: { title: 'Login' }
	},
	{
		path: '',
		redirectTo: '/main',
		pathMatch: 'full'
	},
	{
		path: 'main',
		component: MainDashboardComponent,
		canActivate: [() => { return inject(LoginService).isLoggedIn(); }],

	},
	{
		path: 'modules',
		component: ModulesComponent,
		canActivate: [() => { return inject(LoginService).isLoggedIn(); }],
		data: { title: 'Modules' }
	},
	{
		path: 'modules/:id',
		component: ModuleComponent,
		canActivate: [() => { return inject(LoginService).isLoggedIn(); }],
		data: { title: 'Modules' }
	},
	{
		path:'users',
		component:UsersComponent,
		canActivate: [() => { return inject(LoginService).isLoggedIn(); }],
		data: { title: 'Users' }
	},
	{
		path:'users/new',
		component:UserComponent,
		canActivate: [() => { return inject(LoginService).isLoggedIn(); }],
		data: { title: 'New User' }
	}


];
