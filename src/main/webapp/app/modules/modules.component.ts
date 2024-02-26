import { Component, OnInit, ViewChild } from '@angular/core';
import { ModuleService } from './module.service';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { Module } from './module';
import { LoginService } from '../login/login.service';
import { MatButtonModule } from '@angular/material/button';
import { RouterModule } from '@angular/router';


@Component({
	selector: 'app-modules',
	standalone: true,
	imports: [MatFormFieldModule, MatInputModule, MatTableModule, MatSortModule, MatPaginatorModule, MatButtonModule,RouterModule],
	templateUrl: './modules.component.html',
	styleUrl: './modules.component.css'
})
export class ModulesComponent implements OnInit {
	dataSource: MatTableDataSource<Module>;
	displayedColumns: string[] = ['id', 'name', 'description'];

	@ViewChild(MatPaginator)
	paginator!: MatPaginator;
	@ViewChild(MatSort)
	sort!: MatSort;

	constructor(private moduleService: ModuleService, private loginService: LoginService) {
		this.dataSource = new MatTableDataSource();
	}

	ngOnInit() {
		this.moduleService.getModules().subscribe((data: Module[]) => {
			this.dataSource.data = data;
			this.dataSource.paginator = this.paginator;
			this.dataSource.sort = this.sort;
		}, (error) => {
			if (error.status === 404) {
				console.log('No modules found');
			} else if (error.status === 401) {
				this.loginService.logout();
			}
			console.error(error);
		}, () => {
			console.log('Modules loaded');
		});
	}


}
