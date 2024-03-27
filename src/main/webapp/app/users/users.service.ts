import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { LoginService } from '../login/login.service';
import { map } from 'rxjs';
import { UserModel } from './user.model';

@Injectable({
	providedIn: 'root'
})
export class UsersService {

	constructor(private _httpClient: HttpClient, private _loginService: LoginService) {

	}

	getUsers() {
		return this._httpClient.get('/api/user', {
			headers: {
				Authorization: `Bearer ${this._loginService.getToken()}`
			}
		}).pipe(map((response: any) => response.data),).pipe(map(users => {
			return users.map((user: any) => new UserModel(user.id, user.username, user.fullName, user.email));
		}));
	}
}

