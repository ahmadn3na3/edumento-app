import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { LoginService } from '../login/login.service';
import { catchError, map } from 'rxjs';
import { Module } from './module';

@Injectable({
	providedIn: 'root'
})
export class ModuleService {
	

	constructor(private httpClient: HttpClient, private loginService: LoginService) { }


	getModules() {
		return this.httpClient.get('/api/module', {
			headers: new HttpHeaders({
				'Authorization': `Bearer ${this.loginService.getToken()}`
			})
		}).pipe(map((response: any) => response.data)).pipe(map((modules: any[]) => {
			return modules.map(module => {
				return new Module(module.name, module.description, module.key, module.id);
			})
		}));
	}
	
	getModule(id: any) {
		return this.httpClient.get(`/api/module/${id}`, {
			headers: new HttpHeaders({
				'Authorization': `Bearer ${this.loginService.getToken()}`
			})
		}).pipe(map((response: any) => response.data)).pipe(map((modules: any) => modules));
	}
	
}
