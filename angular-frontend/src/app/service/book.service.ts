import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { Book } from '../model/Book';
import { Router } from '@angular/router';
import { catchError} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class BookService {
  private UserAPIBaseUrl = 'http://localhost:3000/api/books/'; 

  flag: boolean = false;

  constructor(private http: HttpClient, private router: Router) {
  }

  handleError(error: HttpErrorResponse): Observable<never> {

 
    let errors = "";

    if ((typeof error.error.errors) == "object") {
  
      for (let err of error.error.errors) {
        errors += err + "#";
      }
      errors = errors.substring(0, errors.length - 1);
    } else {
      
      errors = "Timestamp: " + error.error.timestamp + "#Status: " + error.error.status + "#Message: " + error.error.message;
    }

    return throwError(Error(errors));
  }

  addBook(book : Book): Observable<Book> {
    const headers = { 'Content-Type': 'application/json' };
    const body = JSON.stringify(book);

    return this.http.post<Book>(this.UserAPIBaseUrl + 'create', body, { headers: headers }).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }



  upload(book : Book,imageFile: Book): Observable<Book> {
    const headers = { 'Content-Type': 'application/json' };
    const body = JSON.stringify(book);

    return this.http.post<Book>(this.UserAPIBaseUrl + 'upload' +'?imageFile=' + imageFile, body, { headers: headers }).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }



  count(): Observable<number> {
    return this.http.get<number>(this.UserAPIBaseUrl + "count").pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  getBooks(page: number, size: number): Observable<Book> {
   
    return this.http.get<Book>(this.UserAPIBaseUrl + 'all' + '?page=' + (page - 1) + '&size=' + size).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  getById(id: number): Observable<Book> {

    return this.http.get<any>(this.UserAPIBaseUrl + id + '/one').pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  putById(book: Book): Observable<Book> {
    const headers = { 'content-type': 'application/json' };
    const body = JSON.stringify(book);
    const addr = this.UserAPIBaseUrl + book.id + '/put';

    return this.http.put<Book>(addr, body, { headers: headers }).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  deleteById(id: number): Observable<void> {
    return this.http.delete<void>(this.UserAPIBaseUrl + id + '/delete/one').pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  deleteAll(): Observable<void> {
    return this.http.delete<void>(this.UserAPIBaseUrl + 'delete/all').pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

}