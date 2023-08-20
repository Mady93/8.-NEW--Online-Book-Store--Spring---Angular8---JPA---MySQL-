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

  //Aggiunto
  countUsers(): Observable<number> {
    return this.httpClient.get<number>('http://localhost:8080/users/count').pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  getUsers(page: number, size: number): Observable<Book[]> {
    return this.httpClient.get<Book[]>('http://localhost:8080/users/get?page=' + (page - 1) + '&size=' + size).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  addUser(newUser: User): Observable<User> {
    const headers = { 'Content-Type': 'application/json' };
    const body = JSON.stringify(newUser);
    return this.httpClient.post<User>('http://localhost:8080/users/add', body, { headers: headers }).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  //Aggiunto
  getUser(id: number): Observable<User> {
    return this.httpClient.get<User>('http://localhost:8080/users/' + id + '/one').pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  updateUser(updatedUser: User): Observable<User> {
    const headers = { 'content-type': 'application/json' };
    const body = JSON.stringify(updatedUser);
    return this.httpClient.put<User>('http://localhost:8080/users/update/' + updatedUser.id, body, { headers: headers }).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  // rimpiazzato con void perche non mi deve ritornare alcun dato tipizzato
  deleteUser(id: number): Observable<void> {
    return this.httpClient.delete<void>('http://localhost:8080/users/' + id + '/delete').pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  //Aggiunto
  deleteUsers(): Observable<void> {
    return this.httpClient.delete<void>('http://localhost:8080/users/deleteAll').pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }



  // BOOK


  //Aggiunto
  /*uploadImageBook(book : Book, imageFile: Book): Observable<Book> {
   const headers = { 'Content-Type': 'application/json' };
   const body = JSON.stringify(book);

   return this.httpClient.post<Book>('http://localhost:8080/books/upload' +'?imageFile=' + imageFile, body, { headers: headers }).pipe(
     catchError((err: HttpErrorResponse) => this.handleError(err))
   );
 }*/

  uploadImageBook(book: Book, imageFile: any): Observable<Book> {
    const formData = new FormData();
    formData.append('imageFile', imageFile, imageFile.name);

    const headers = { 'Content-Type': 'application/json' };
    const body = JSON.stringify(book);

    return this.httpClient.post<Book>('http://localhost:8080/books/upload', formData, { params: { imageFile: imageFile.name }, headers: headers })
      .pipe(
        catchError((err: HttpErrorResponse) => this.handleError(err))
      );
  }

  //Aggiunto
  countBooks(): Observable<number> {
    return this.httpClient.get<number>('http://localhost:8080/books/count').pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  getBooks(page: number, size: number): Observable<Book[]> {
    return this.httpClient.get<Book[]>('http://localhost:8080/books/get?page=' + (page - 1) + '&size=' + size).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  addBook(newBook: Book): Observable<Book> {
    const headers = { 'Content-Type': 'application/json' };
    const body = JSON.stringify(newBook);
    return this.httpClient.post<Book>('http://localhost:8080/books/add', body, { headers: headers }).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  //Aggiunto
  getBook(id: number): Observable<Book> {
    return this.httpClient.get<Book>('http://localhost:8080/books/' + id + '/one').pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  // rimpiazzato con void perche non mi deve ritornare alcun dato tipizzato
  deleteBook(id: number): Observable<void> {
    return this.httpClient.delete<void>('http://localhost:8080/books/' + id + '/delete').pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  updateBook(updatedBook: Book): Observable<Book> {
    const headers = { 'content-type': 'application/json' };
    const body = JSON.stringify(updatedBook);
    return this.httpClient.put<Book>('http://localhost:8080/books/update/' + updatedBook.id, body, { headers: headers }).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  //Aggiunto
  deleteBooks(): Observable<void> {
    return this.httpClient.delete<void>('http://localhost:8080/books/deleteAll').pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }





  // Order

  countOrders(uid: number): Observable<number> {
    return this.httpClient.get<number>('http://localhost:8080/orders/'+uid+'/count').pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  getOrders(uid: number, page: number, size: number): Observable<Order[]> {
    return this.httpClient.get<Order[]>('http://localhost:8080/orders/'+uid+'/get?page=' + (page - 1) + '&size=' + size).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  addOrder(uid: number): Observable<Order> {
    //const headers = { 'content-type': 'application/json' };
    //const body = JSON.stringify(updatedOrder);
    return this.httpClient.get<Order>('http://localhost:8080/orders/'+uid+'/add').pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  getOrder(id: number): Observable<Order> {
    return this.httpClient.get<Order>('http://localhost:8080/orders/' + id + '/one').pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  deleteOrder(id: number): Observable<void> {
    return this.httpClient.delete<void>('http://localhost:8080/orders/' + id + '/delete').pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  updateOrder(updatedOrder: Order): Observable<Order> {
    const headers = { 'content-type': 'application/json' };
    const body = JSON.stringify(updatedOrder);
    return this.httpClient.put<Order>('http://localhost:8080/orders/update/' + updatedOrder.id, body, { headers: headers }).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }


  deleteOrders(): Observable<void> {
    return this.httpClient.delete<void>('http://localhost:8080/orders/deleteAll').pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }






  // JoinTable

  addJoinTable(newJoinTable: JoinTable): Observable<JoinTable> {
    const headers = { 'Content-Type': 'application/json' };
    const body = JSON.stringify(newJoinTable);
    return this.httpClient.post<JoinTable>('http://localhost:8080/joinTables/add', body, { headers: headers }).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }


  getJoinTablesByOrderId(oid: number): Observable<JoinTable[]>{
    return this.httpClient.get<JoinTable[]>('http://localhost:8080/joinTables/' + oid + '/get').pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }


  deleteJoinTablesByBookId(bid: number): Observable<void>{
    return this.httpClient.delete<void>('http://localhost:8080/joinTables/'+bid+"/delete").pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  deleteAllRecordsOnJoinTable():Observable<void> {
    return this.httpClient.delete<void>('http://localhost:8080/joinTables/deleteAll').pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  /*

  countJoinTables(bookId: Book, orderId: Order): Observable<number> {
    return this.httpClient.get<number>('http://localhost:8080/joinTables/' + bookId.id + '/' + orderId.id + '/count').pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  getJoinTables(bookId: Book, orderId: Order, page: number, size: number): Observable<JoinTable[]> {
    return this.httpClient.get<JoinTable[]>('http://localhost:8080/joinTables/' + bookId.id + '/' + orderId.id + '/get?page=' + (page - 1) + '&size=' + size).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  getJoinTable(bookId: Book, orderId: Order): Observable<JoinTable> {
    return this.httpClient.get<JoinTable>('http://localhost:8080/joinTables/' + bookId.id + '/' + orderId.id + '/one').pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  updateJoinTable(bookId: Book, orderId: Order, updatedJoinTable: JoinTable): Observable<JoinTable> {
    const headers = { 'content-type': 'application/json' };
    const body = JSON.stringify(updatedJoinTable);
    return this.httpClient.put<JoinTable>('http://localhost:8080/joinTables/update/' + bookId.id + '/' + orderId.id, body, { headers: headers }).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }



  */




}

