import { HttpErrorResponse, HttpInterceptorFn } from "@angular/common/http";
import { inject } from "@angular/core";
import { LoginService } from "../auth/login.service";
import { catchError, throwError } from "rxjs";

export const authInterceptor: HttpInterceptorFn = (req, next) => {
    const loginService = inject(LoginService);
    const token = loginService.getToken();
    // console.log('AuthInterceptor: found token:', token ? 'YES' : 'NO');

    let authReq = req;
    if (token) {
        authReq = req.clone({
            setHeaders: {
                Authorization: `Bearer ${token}`,
            },
        });
    }

    return next(authReq).pipe(
        catchError((error: HttpErrorResponse) => {
            if (error.status === 401) {
                console.log("AuthInterceptor: 401 error detected, logging out");
                // Avoid infinite loops if logout endpoint itself returns 401 (though logout usually cleans client side)
                if (
                    !req.url.includes("/api/authenticate") &&
                    !req.url.includes("/api/account")
                ) {
                    loginService.logout();
                } else {
                    // Just logout anyway to be safe, client side cleanup
                    loginService.logout();
                }
            }
            return throwError(() => error);
        }),
    );
};
