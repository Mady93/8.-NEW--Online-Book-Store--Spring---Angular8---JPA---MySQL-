import { HttpClient, HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { catchError, shareReplay, tap } from 'rxjs/operators';
import { User } from '../model/User ';
import { HttpClientService } from './http-client.service';
import { Order } from '../model/Order';
import { OrderBook } from '../model/OrderBook';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  role: string = "";
  uid: number = null;
  exp: number;
  private upd = new Subject<void>();


  constructor(private http: HttpClient, private httpClientService: HttpClientService) {
    this.checkState();
  }

  checkState() {

    let token = localStorage.getItem("token");
    if (!token) return;

    let a = token.indexOf(".");
    let b = token.indexOf(".", a + 1);

    token = token.substring(a + 1, b);
    let data = JSON.parse(atob(token));

    //debugger;

    this.role = data.role;
    this.uid = data.sub;
    this.exp = data.exp;

    this.upd.next();

  }


  update(): Observable<void> {
    return this.upd.asObservable();
  }


  buy() {

    if (this.uid == 0) return;

    let cart = JSON.parse(localStorage["cart"]);
    let order: Order;

    order = new Order();
    order.user = new User();
    order.user.id = this.uid;

    return new Promise((resolve, reject) => {
      this.httpClientService.addOrder(this.uid).subscribe({
        next: (ret: any) => {
          let oid = ret.orderId;

          // Creare un array di promesse per le chiamate addOrderBook
          const orderBookPromises = cart.map(ele => {
            let jt: OrderBook = new OrderBook();
            jt.book.id = ele.id;
            jt.order.id = oid;
            jt.quantity = ele.q;

            // Restituire la promessa dalla chiamata addOrderBook
            return this.httpClientService.addOrderBook(jt).toPromise();
          });

          // Attendere il completamento di tutte le promesse utilizzando Promise.all()
          Promise.all(orderBookPromises)
            .then(() => {
              // Le chiamate addOrderBook sono state completate
              resolve(null);
            })
            .catch(error => {
              // Gestire eventuali errori
              console.error("Errore durante il completamento delle promesse:", error);
              reject();
            });
        },
        error: error => {
          // Gestire eventuali errori nella chiamata addOrder
          console.error("Errore nella chiamata addOrder:", error);
          reject();
        }
      });
    });
  }


  login(email: string, password: string): Observable<HttpResponse<any>> {
    const url = 'http://localhost:3000/login'; // URL del tuo middleware Node.js
    const headers = { 'Content-Type': 'application/json' };
    const body = { email, password };


    return this.http.post<any>(url, body, { headers: headers, observe: 'response' }).pipe(
      tap(response => {
        if (response.status === 200) {
          //debugger;
          const token = response.headers.get("Authorization");
          localStorage.setItem('token', token); // Salva il token nella localStorage
          this.checkState(); // Aggiorna i dati dell'utente loggato
        }
      }),
      catchError((error: HttpErrorResponse) => {
        console.log('Errore durante il login:', error);
        throw error;
      }),
      shareReplay()
    );
  }


  logout() {
    this.uid = null;
    localStorage.removeItem("token");
    this.role = "";
  }


  isLogged(): boolean {
    return (localStorage["token"] != null)
  }


  register(name: string, email: string, password: string): Observable<any> {

    let user: User = new User();
    user.name = name;
    user.email = email;
    user.password = password;

    const url = 'http://localhost:3000/users/register'; // URL del tuo middleware Node.js
    const headers = { 'Content-Type': 'application/json' };

    return this.http.post<any>(url, user, { headers: headers })
  }



  /*
      role, uid, exp: Queste variabili vengono utilizzate per memorizzare il ruolo dell'utente, l'ID dell'utente e la scadenza del token JWT.

    private upd = new Subject<void>(): È un oggetto Subject che viene utilizzato per inviare aggiornamenti quando lo stato dell'utente cambia.

    checkState(): Questo metodo verifica lo stato dell'utente leggendo il token JWT da localStorage e analizzandone i dati. In base ai dati del token, vengono aggiornate le variabili role, uid e exp. Viene anche emesso un segnale attraverso il soggetto upd per indicare un aggiornamento dello stato.

    update(): Questo metodo restituisce un'Observable associata al soggetto upd. Consentirà ad altre parti dell'applicazione di ascoltare gli aggiornamenti dello stato.

    buy(): Questo metodo gestisce il processo di acquisto. Legge il carrello dal localStorage, crea un ordine e poi crea gli elementi OrderBook corrispondenti al carrello. Utilizza promesse per garantire che tutte le operazioni siano completate correttamente.

    login(email: string, password: string): Questo metodo invia una richiesta POST al server per effettuare il login. Se la richiesta ha successo, il token viene salvato in localStorage e lo stato dell'utente viene aggiornato.

    logout(): Questo metodo effettua il logout, rimuovendo il token e reimpostando le variabili uid e role.

    isLogged(): boolean: Questo metodo restituisce true se l'utente è autenticato, altrimenti restituisce false.

    register(name: string, email: string, password: string): Questo metodo invia una richiesta POST per registrare un nuovo utente.
  */

}
