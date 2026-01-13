import { inject } from "@angular/core";
import {
	ActivatedRouteSnapshot,
	RouterStateSnapshot,
	Routes,
} from "@angular/router";
import { LoginService } from "./core/auth/login.service";
import { LoginComponent } from "./login/login.component";
import { SpacesComponent } from "./spaces/spaces.component";

export const routes: Routes = [
	{
		path: "login",

		component: LoginComponent,
		data: { title: "Login" },
	},
	{
		path: "signup",
		loadComponent: () =>
			import("./signup/signup.component").then((m) => m.SignupComponent),
		data: { title: "Sign Up" },
	},
	{
		path: "",
		redirectTo: "/spaces",
		pathMatch: "full",
	},
	{
		path: "spaces",
		component: SpacesComponent,
		canActivate: [
			(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
				return inject(LoginService).canActivate(route, state);
			},
		],
		data: { title: "My Spaces" },
	},
	{
		path: "spaces/create",
		loadComponent: () =>
			import("./spaces/create-space/create-space.component").then(
				(m) => m.CreateSpaceComponent,
			),
		data: { title: "Create Space" },
	},
	{
		path: "spaces/:id",
		loadComponent: () =>
			import("./spaces/space-detail/space-detail.component").then(
				(m) => m.SpaceDetailComponent,
			),
		canActivate: [
			(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
				return inject(LoginService).canActivate(route, state);
			},
		],
		data: { title: "Space Details" },
	},
	{
		path: "spaces/:id/discussion/create",
		loadComponent: () =>
			import("./discussion/create-discussion/create-discussion.component").then(
				(m) => m.CreateDiscussionComponent,
			),
		canActivate: [
			(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
				return inject(LoginService).canActivate(route, state);
			},
		],
		data: { title: "Create Discussion" },
	},
	{
		path: "profile",
		loadComponent: () =>
			import("./user-profile/user-profile.component").then(
				(m) => m.UserProfileComponent,
			),
		data: { title: "Account Settings" },
	},
	{
		path: "activate",
		loadComponent: () =>
			import("./activation/activation.component").then(
				(m) => m.ActivationComponent,
			),
		data: { title: "Activate Account" },
	},
];
