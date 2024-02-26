import { Component, OnInit } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { Module } from '../module';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, ActivatedRouteSnapshot, RouterModule } from '@angular/router';
import { ModuleService } from '../module.service';

@Component({
	selector: 'app-module',
	standalone: true,
	imports: [MatCardModule, MatIconModule, MatButtonModule, MatChipsModule, CommonModule, RouterModule],
	templateUrl: './module.component.html',
	styleUrl: './module.component.css'
})
export class ModuleComponent implements OnInit {

	module: Module = {
		name: 'Module',
		description: 'This is a module',
		key: 'module',
		id: 1, permissions: [
			{
				name: 'View',
				keycode: 'view',
				code: 1, type: 'USER',
				id: 1
			},
			{
				name: 'Create',
				keycode: 'create',
				code: 2, type: 'USER',
				id: 2
			},

		]

	};
	constructor(private _activatedRoute: ActivatedRoute, private _moduleservice: ModuleService) { }


	ngOnInit() {

		this._activatedRoute.params.subscribe((params:any) => {
			console.log('ModuleComponent', params);
			this._moduleservice.getModule(params.id).subscribe((module) => {
				this.module = module;
				console.log('ModuleComponent', module);
			});
		});


	}
	}