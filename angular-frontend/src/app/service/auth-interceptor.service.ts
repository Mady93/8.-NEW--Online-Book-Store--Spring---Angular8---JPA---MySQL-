import { Injectable } from '@angular/core';
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest, HttpResponse } from "@angular/common/http";
import { Observable } from "rxjs";
import { tap } from "rxjs/operators";
import { AuthService } from './auth.service';
import { Router } from "@angular/router";

@Injectable({
  providedIn: 'root'
})
export class AuthInterceptorService implements HttpInterceptor {

  constructor(private auth: AuthService, private router: Router) { }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    let res = req;
    let token = localStorage.getItem("token");

    if (token) {
      // .clone fa una coppia superficiale (side effect).. in cui le modifiche si riflettano sull'originale in un secondo momento 
      res = req.clone({
        headers: req.headers.set("Authorization", "Bearer " + token)
      });
    }

    return next.handle(res).pipe(
      tap(
        event => {
          if (event instanceof HttpResponse && event.status == 200) {
            let token = event.headers.get("Authorization");

            if (token != null) {
              console.log(token);
              localStorage.setItem("token", token);
            }
          }
        },

        err => {
          if (err.status == 401) {
            //debugger;
            localStorage.removeItem("token");
            this.auth.logout();
            this.router.navigate(["/login"]);
          }
        }
        
      )
    );

  }

}
