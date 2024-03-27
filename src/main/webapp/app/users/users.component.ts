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
	standalone: true,
	imports: [MatTableModule, MatPaginatorModule, MatSortModule, MatFormFieldModule,MatInputModule],
	templateUrl: './users.component.html',
	styleUrl: './users.component.css'
})
export class UsersComponent {
	displayedColumns: string[] = ['id', 'fullName', 'username', 'email'];
	dataSource: MatTableDataSource<UserModel>;

	@ViewChild(MatPaginator) paginator!: MatPaginator;
	@ViewChild(MatSort) sort!: MatSort;

	constructor(private _userService: UsersService, private loginService: LoginService) {
		// Create 100 users
		const users: UserModel[] = [];

		this._userService.getUsers().subscribe((users: UserModel[]) => {
			users.forEach((user: UserModel) => {
				users.push(user);
			});
			
		}, (error) => {
			if (error.status === 404) {
				console.log('Error 404');
			} else if (error.status === 401) {
				this.loginService.logout();
			}
			console.error(error);

		});

		// Assign the data to the data source for the table to render
		this.dataSource = new MatTableDataSource(users);
		}
		
		
	

	ngAfterViewInit() {
			this.dataSource.paginator = this.paginator;
			this.dataSource.sort = this.sort;
		}

	applyFilter(event: Event) {
			const filterValue = (event.target as HTMLInputElement).value;
			if (filterValue !== '') {
               this.dataSource.filter = filterValue.trim().toLowerCase();
            }
			

			if(this.dataSource.paginator) {
			this.dataSource.paginator.firstPage();
		}
	}
}

