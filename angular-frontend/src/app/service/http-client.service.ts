import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { User } from '../model/User ';
import { Book } from '../model/Book';
import { catchError } from 'rxjs/operators';
import { Observable, throwError } from 'rxjs';
import { Order } from '../model/Order';
import { JoinTable } from '../model/JoinTable';

@Injectable({
  providedIn: 'root'
})
export class HttpClientService {

  constructor(
    private httpClient: HttpClient
  ) { }


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



  // USER


  setRole(uid: number, role: string): Observable<User> {

    let body = {uid: uid, role: role};
    const headers = { 'Content-Type': 'application/json' };

    return this.httpClient.post<User>('http://localhost:3000/users/setRole', body, { headers: headers }).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  } 

  countUsers(): Observable<number> {
    return this.httpClient.get<number>('http://localhost:3000/users/count').pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  getUsers(page: number, size: number): Observable<Book[]> {
    return this.httpClient.get<Book[]>('http://localhost:3000/users/get?page=' + (page - 1) + '&size=' + size).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  addUser(newUser: User): Observable<User> {
    const headers = { 'Content-Type': 'application/json' };
    const body = JSON.stringify(newUser);
    return this.httpClient.post<User>('http://localhost:3000/users/add', body, { headers: headers }).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

 
  getUser(id: number): Observable<User> {
    return this.httpClient.get<User>('http://localhost:3000/users/' + id + '/one').pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  // Non ancora usato
  updateUser(updatedUser: User): Observable<User> {
    const headers = { 'content-type': 'application/json' };
    const body = JSON.stringify(updatedUser);
    return this.httpClient.put<User>('http://localhost:3000/users/update/' + updatedUser.id, body, { headers: headers }).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  deleteUser(id: number): Observable<void> {
    return this.httpClient.delete<void>('http://localhost:3000/users/' + id + '/delete').pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  deleteUsers(): Observable<void> {
    return this.httpClient.delete<void>('http://localhost:3000/users/deleteAll').pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }



  // BOOK


  // Non ancora usato
  uploadImageBook(book: Book, imageFile: any): Observable<Book> {
    const formData = new FormData();
    formData.append('imageFile', imageFile, imageFile.name);

    const headers = { 'Content-Type': 'application/json' };
    const body = JSON.stringify(book);

    return this.httpClient.post<Book>('http://localhost:3000/books/upload', formData, { params: { imageFile: imageFile.name }, headers: headers })
      .pipe(
        catchError((err: HttpErrorResponse) => this.handleError(err))
      );
  }

  countBooks(): Observable<number> {
    return this.httpClient.get<number>('http://localhost:3000/books/count').pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  getBooks(page: number, size: number): Observable<Book[]> {
    return this.httpClient.get<Book[]>('http://localhost:3000/books/get?page=' + (page - 1) + '&size=' + size).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  addBook(newBook: Book): Observable<Book> {
    const headers = { 'Content-Type': 'application/json' };
    const body = JSON.stringify(newBook);
    return this.httpClient.post<Book>('http://localhost:3000/books/add', body, { headers: headers }).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  getBook(id: number): Observable<Book> {
    return this.httpClient.get<Book>('http://localhost:3000/books/' + id + '/one').pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  deleteBook(id: number): Observable<void> {
    return this.httpClient.delete<void>('http://localhost:3000/books/' + id + '/delete').pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  updateBook(updatedBook: Book, role: string): Observable<Book> {

    delete updatedBook.picByte;
    delete updatedBook.retrievedImage;

    let url: string = 'http://localhost:3000/books/update/' + updatedBook.id;
    url += (role=="seller")?'/price':'';

    const headers = { 'content-type': 'application/json' };
    
    const body = JSON.stringify(updatedBook);
    return this.httpClient.put<Book>(url, body, { headers: headers }).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  deleteBooks(): Observable<void> {
    return this.httpClient.delete<void>('http://localhost:3000/books/deleteAll').pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }





  // Order

  countOrders(uid: number): Observable<number> {
    return this.httpClient.get<number>('http://localhost:3000/orders/'+uid+'/count').pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  getOrders(uid: number, page: number, size: number): Observable<Order[]> {
    return this.httpClient.get<Order[]>('http://localhost:3000/orders/'+uid+'/get?page=' + (page - 1) + '&size=' + size).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  addOrder(uid: number): Observable<any> {
    

    return this.httpClient.get<any>('http://localhost:3000/orders/'+uid+'/add').pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  getOrder(id: number): Observable<Order> {
    return this.httpClient.get<Order>('http://localhost:3000/orders/' + id + '/one').pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  // Non ancora usato
  deleteOrder(id: number): Observable<void> {
    return this.httpClient.delete<void>('http://localhost:3000/orders/' + id + '/delete').pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

// Non ancora usato
  updateOrder(updatedOrder: Order): Observable<Order> {
    const headers = { 'content-type': 'application/json' };
    const body = JSON.stringify(updatedOrder);
    return this.httpClient.put<Order>('http://localhost:3000/orders/update/' + updatedOrder.id, body, { headers: headers }).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

// Non ancora usato
  deleteOrders(): Observable<void> {
    return this.httpClient.delete<void>('http://localhost:3000/orders/deleteAll').pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  //per cancellare tutti i ordini ------------ prova
  deleteAlRecordsOnOrder():Observable<void> {
    return this.httpClient.delete<void>('http://localhost:3000/orders/deleteAll').pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }




  // JoinTable

  addJoinTable(newJoinTable: JoinTable): Observable<JoinTable> {
    const headers = { 'Content-Type': 'application/json' };
    const body = JSON.stringify(newJoinTable);
    return this.httpClient.post<JoinTable>('http://localhost:3000/joinTables/add', body, { headers: headers }).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }


  getJoinTablesByOrderId(oid: number): Observable<JoinTable[]>{
    return this.httpClient.get<JoinTable[]>('http://localhost:3000/joinTables/' + oid + '/get').pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }


  deleteJoinTablesByBookId(bid: number): Observable<void>{
    return this.httpClient.delete<void>('http://localhost:3000/joinTables/'+bid+"/delete").pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  deleteAllRecordsOnJoinTable():Observable<void> {
    return this.httpClient.delete<void>('http://localhost:3000/joinTables/deleteAll').pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

 

}

