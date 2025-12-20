import { Component, OnInit, ViewChild } from "@angular/core";
import { ModuleService } from "./module.service";
import { Module } from "./module";
import { LoginService } from "../login/login.service";
import { RouterModule } from "@angular/router";

@Component({
    selector: "app-modules",
    imports: [
        RouterModule
    ],
    templateUrl: "./modules.component.html",
    styleUrl: "./modules.component.css"
})
export class ModulesComponent implements OnInit {
    modules: Module[] = [];
    displayedColumns: string[] = ["id", "name", "description"];

    constructor(
        private moduleService: ModuleService,
        private loginService: LoginService,
    ) { }

    ngOnInit() {
        this.moduleService.getModules().subscribe({
            next: (data: Module[]) => {
                this.modules = data;
            },
            error: (error) => {
                if (error.status === 404) {
                    console.log("No modules found");
                } else if (error.status === 401) {
                    this.loginService.logout();
                }
                console.error(error);
            },
            complete: () => {
                console.log("Modules loaded");
            },
        });
    }
}
