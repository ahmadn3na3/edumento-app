import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Authentication } from './model/authentication';
import { map } from 'rxjs';

@Injectable({
	providedIn: 'root',
})
export class LoginService {


	constructor(private httpclient: HttpClient) { }

	authenticateUser(user: Authentication) {
		return this.httpclient.post('/api/authenticate', user).pipe(map((res: any) => {
			if (res && res.id_token) {
				localStorage.setItem('currentUser', JSON.stringify({ username: user.username, token: res.id_token }));
			}
			return res;
		}));
	}

	logout() {
		localStorage.removeItem('currentUser');
	}

	isLoggedIn() {
		return localStorage.getItem('currentUser') != null;
	}

	getToken() {
		let currentUser = JSON.parse(localStorage.getItem('currentUser') ?? '{}');
		if (currentUser && currentUser.token) {
			return currentUser.token;
		}
		return null;
	}
}
