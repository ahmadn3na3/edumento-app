import { Component, OnInit, ViewChild } from "@angular/core";
import { CategoryService } from "./service/category.service";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatTableDataSource, MatTableModule } from "@angular/material/table";
import { MatSort, MatSortModule } from "@angular/material/sort";
import { MatPaginator, MatPaginatorModule } from "@angular/material/paginator";
import { MatButtonModule } from "@angular/material/button";
import { RouterModule } from "@angular/router";
import { Category } from "./model/category.model";
import { LoginService } from "../login/login.service";

@Component({
    selector: "app-category",
    imports: [
        MatFormFieldModule,
        MatInputModule,
        MatTableModule,
        MatSortModule,
        MatPaginatorModule,
        MatButtonModule,
        RouterModule,
    ],
    templateUrl: "./category.component.html",
    styleUrl: "./category.component.css"
})
export class CategoryComponent implements OnInit {
    dataSource: MatTableDataSource<Category>;
    displayedColumns: string[] = ["id", "name", "nameAr"];

    @ViewChild(MatPaginator)
    paginator!: MatPaginator;
    @ViewChild(MatSort)
    sort!: MatSort;

    constructor(
        private categoryService: CategoryService,
        private loginService: LoginService,
    ) {
        this.dataSource = new MatTableDataSource();
    }

    ngOnInit() {
        this.categoryService.getCategories().subscribe({
            next: (data: any) => {
                this.dataSource.data = data;
                this.dataSource.paginator = this.paginator;
                this.dataSource.sort = this.sort;
            },
            error: (error) => {
                if (error.status === 404) {
                    console.log("No categories found");
                } else if (error.status === 401) {
                    this.loginService.logout();
                }
                console.error(error);
            },
            complete: () => {
                console.log("categories loaded");
            },
        });
    }
}
