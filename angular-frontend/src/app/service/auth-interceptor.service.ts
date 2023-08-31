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

  /* intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>>: 
  Questo metodo è il cuore dell'interceptor. Riceve la richiesta HTTP in ingresso e 
  il gestore successivo. Il codice aggiunge l'intestazione di autorizzazione al token JWT, 
  se disponibile, attraverso la clonazione della richiesta originale. */
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    let res = req;
    let token = localStorage.getItem("token");

    if (token) {
      // .clone fa una coppia superficiale (side effect).. in cui le modifiche si riflettano sull'originale in un secondo momento 
      res = req.clone({
        headers: req.headers.set("Authorization", "Bearer " + token)
      });
    }

    // return next.handle(res).pipe(tap(...)): Viene inviata la richiesta modificata e si ascolta la risposta utilizzando l'operatore tap. Questo operatore consente di eseguire azioni sulle risposte HTTP senza modificarle.
    /* Dentro il blocco tap, se la risposta è un'istanza di HttpResponse con uno status 200 o 201, 
    viene verificato se c'è un'intestazione di autorizzazione nell'header. 
    Se presente, il token viene estratto e memorizzato in localStorage.
    In caso di errore (err), se lo status è 401 (non autorizzato), 
    il token viene rimosso da localStorage, l'utente viene scollegato tramite il metodo logout() 
    del servizio AuthService, e viene reindirizzato alla pagina di login tramite il servizio Router. */
    return next.handle(res).pipe(
      tap(
        event => {
          if (event instanceof HttpResponse && (event.status == 200 || event.status == 201)) {
            let token = event.headers.get("Authorization");

            if (token != null) {
              console.log(token);
              localStorage.setItem("token", token);
            }
          }
        },

        err => {
          if (err.status == 401) {
            localStorage.removeItem("token");
            this.auth.logout();
            this.router.navigate(["/login"]);
          }
        }

      )
    );

  }

}
