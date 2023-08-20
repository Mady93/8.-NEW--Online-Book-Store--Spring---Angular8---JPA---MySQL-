import { HttpClient, HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { catchError, shareReplay, tap } from 'rxjs/operators';
import { User } from '../model/User ';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  role: string = "";
  uid: string = "";
  exp: number;


  constructor(private http: HttpClient) {
    this.checkState();
  }



  checkState(){
    
    let token = localStorage.getItem("token");
    if (!token) return;



    let a = token.indexOf(".");
    let b = token.indexOf(".", a+1);
    
    token = token.substring(a+1,b);
    let data = JSON.parse(atob(token));

    this.role = data.role;
    this.uid = data.sub;
    this.exp = data.exp;
    
  }


  login(email: string, password: string): Observable<HttpResponse<any>> {
    const url = 'http://localhost:3000/login'; // URL del tuo middleware Node.js
    const headers = { 'Content-Type': 'application/json' };
    const body = { email, password };

  
    return this.http.post<any>(url, body, { headers: headers, observe: 'response' }).pipe(
        tap(response => {
          if (response.status === 200) {
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
    localStorage.removeItem("token");
    this.role = "";
  }


  isLogged(): boolean {
    return (localStorage["token"] != null)
  }

  register (name: string, email: string, password: string):Observable<any> {

    let user: User = new User();
    user.name = name;
    user.email = email;
    user.password = password;

    const url = 'http://localhost:3000/users/register'; // URL del tuo middleware Node.js
    const headers = { 'Content-Type': 'application/json' };

    return this.http.post<any>(url, user, {headers: headers})
  }




}
