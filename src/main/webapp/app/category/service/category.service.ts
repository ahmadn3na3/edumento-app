import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { LoginService } from "../../login/login.service";
import { map } from "rxjs";
import { Category } from "../model/category.model";

@Injectable({
    providedIn: "root",
})
export class CategoryService {
    constructor(
        private httpClient: HttpClient,
        private loginService: LoginService,
    ) {}

    getCategories() {
        return this.httpClient
            .get("/api/category", {
                headers: new HttpHeaders({
                    Authorization: `Bearer ${this.loginService.getToken()}`,
                }),
            })
            .pipe(map((response: any) => response.data))
            .pipe(
                map((categories: any[]) => {
                    return categories.map((category) => {
                        return new Category({
                            name: category.name,
                            nameAr: category.nameAr,
                            id: category.id,
                        });
                    });
                }),
            );
    }

    getCategory(id: number) {
        const url = `/api/category/${id}`;
        const headers = new HttpHeaders({
            Authorization: `Bearer ${this.loginService.getToken()}`,
        });
        return this.httpClient
            .get<{ data: Category }>(url, { headers })
            .pipe(map(({ data }) => new Category({ ...data })));
    }
}
