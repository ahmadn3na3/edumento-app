import { Component, ViewChild } from '@angular/core';
import { MatFormField, MatFormFieldModule } from '@angular/material/form-field';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { UserCreatedModel, UserModel } from './user.model';
import { UsersService } from './users.service';
import { LoginService } from '../login/login.service';
import { MatInputModule } from '@angular/material/input';

@Component({
	selector: 'app-users',
	imports: [MatTableModule, MatPaginatorModule, MatSortModule, MatFormFieldModule, MatInputModule],
	templateUrl: './users.component.html',
	styleUrl: './users.component.css'
})
export class UsersComponent {
	displayedColumns: string[] = ['id', 'fullName', 'username', 'email'];
	users: UserModel[] = [];

	constructor(private _userService: UsersService, private loginService: LoginService) {
		this._userService.getUsers().subscribe((users: UserModel[]) => {
			this.users = users;
		}, (error) => {
			if (error.status === 404) {
				console.log('Error 404');
			} else if (error.status === 401) {
				this.loginService.logout();
			}
			console.error(error);
		});
	}

	applyFilter(event: Event) {
		const filterValue = (event.target as HTMLInputElement).value;
		// Simple client-side filtering logic if needed, or call API
		// For simplicity, doing nothing or refreshing list
		console.log('Filter not implemented yet for bootstrap table:', filterValue);
	}
}

