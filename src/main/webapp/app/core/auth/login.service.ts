import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Authentication } from "./model/authentication";
import {
    ActivatedRouteSnapshot,
    Router,
    RouterStateSnapshot,
} from "@angular/router";
import { Subject, map } from "rxjs";
import { User } from "../models/user.model";
import { MOCK_USER } from "../models/mock-data";

@Injectable({
    providedIn: "root",
})
export class LoginService {
    private isLoggedInSubject = new Subject<boolean>();
    isLoggedIn$ = this.isLoggedInSubject.asObservable();

    constructor(
        private httpclient: HttpClient,
        private router: Router,
    ) {
        this.isLoggedInSubject.next(this.isLoggedIn());
    }

    authenticateUser(user: Authentication) {
        return this.httpclient.post("/api/authenticate", user).pipe(
            map((res: any) => {
                if (res && res.id_token) {
                    localStorage.setItem(
                        "currentUser",
                        JSON.stringify({
                            username: user.username,
                            token: res.id_token,
                        }),
                    );
                }
                this.isLoggedInSubject.next(this.isLoggedIn());
                return res;
            }),
        );
    }

    register(user: any) {
        // Stub implementation
        return this.httpclient.post("/api/register", user);
    }

    logout() {
        localStorage.removeItem("currentUser");
        this.isLoggedInSubject.next(this.isLoggedIn());
        this.router.navigate(["/login"]);
    }

    // turn this into observable

    isLoggedIn() {
        console.log("isLoggedIn", localStorage.getItem("currentUser") !== null);
        return localStorage.getItem("currentUser") !== null;
    }

    getToken() {
        let currentUser = JSON.parse(
            localStorage.getItem("currentUser") ?? "{}",
        );
        if (currentUser && currentUser.token) {
            return currentUser.token;
        }
        return null;
    }

    activate(key: string) {
        return this.httpclient.get(`/api/activate/${key}`);
    }

    getCurrentUser(): User | null {
        // In a real app, you'd parse the token or fetch user details.
        // For now, we return the mock user if logged in, or null.
        if (this.isLoggedIn()) {
            return MOCK_USER;
        }
        return null;
    }

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        if (this.isLoggedIn()) {
            return true;
        }
        this.router.navigate(["/login"]);
        return false;
    }
}
