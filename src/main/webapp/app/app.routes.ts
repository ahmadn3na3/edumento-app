import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { MainDashboardComponent } from './main-dashboard/main-dashboard.component';
import { LoginService } from './login/login.service';
import { ModulesComponent } from './modules/modules.component';
import { inject } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { ModuleComponent } from './modules/module/module.component';

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
	}


];
