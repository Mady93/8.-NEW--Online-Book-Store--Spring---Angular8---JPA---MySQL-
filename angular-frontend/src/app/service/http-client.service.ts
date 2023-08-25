import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { User } from '../model/User ';
import { Book } from '../model/Book';
import { catchError } from 'rxjs/operators';
import { Observable, throwError } from 'rxjs';
import { Order } from '../model/Order';
import { OrderBook } from '../model/OrderBook';

@Injectable({
  providedIn: 'root'
})
export class HttpClientService {

  private baseURL = 'http://localhost:3000'; // Dichiarazione come membro della classe

  constructor(private httpClient: HttpClient) { }


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

    return this.httpClient.post<User>(`${this.baseURL}/users/setRole`, body, { headers: headers }).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  } 

  countUsers(): Observable<number> {
    return this.httpClient.get<number>(`${this.baseURL}/users/count`).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  getUsers(page: number, size: number): Observable<Book[]> {
    return this.httpClient.get<Book[]>(`${this.baseURL}/users/get?page=${page - 1}&size=${size}`).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  addUser(newUser: User): Observable<User> {
    const headers = { 'Content-Type': 'application/json' };
    const body = JSON.stringify(newUser);
    return this.httpClient.post<User>(`${this.baseURL}/users/add`, body, { headers: headers }).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

 
  getUser(id: number): Observable<User> {
    return this.httpClient.get<User>(`${this.baseURL}/users/${id}/one`).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  // Non ancora usato
  updateUser(updatedUser: User): Observable<User> {
    const headers = { 'content-type': 'application/json' };
    const body = JSON.stringify(updatedUser);
    return this.httpClient.put<User>(`${this.baseURL}/users/update/${updatedUser.id}`, body, { headers: headers }).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  deleteUser(id: number): Observable<void> {
    return this.httpClient.delete<void>(`${this.baseURL}/users/${id}/delete`).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  deleteUsers(): Observable<void> {
    return this.httpClient.delete<void>(`${this.baseURL}/users/deleteAll`).pipe(
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

    return this.httpClient.post<Book>(`${this.baseURL}/books/upload`, formData, { params: { imageFile: imageFile.name }, headers: headers }).pipe(
        catchError((err: HttpErrorResponse) => this.handleError(err))
      );
  }

  countBooks(): Observable<number> {
    return this.httpClient.get<number>(`${this.baseURL}/books/count`).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  getBooks(page: number, size: number): Observable<Book[]> {
    return this.httpClient.get<Book[]>(`${this.baseURL}/books/get?page=${page - 1}&size=${size}`).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  addBook(newBook: Book): Observable<Book> {
    const headers = { 'Content-Type': 'application/json' };
    const body = JSON.stringify(newBook);
    return this.httpClient.post<Book>(`${this.baseURL}/books/add`, body, { headers: headers }).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  getBook(id: number): Observable<Book> {
    return this.httpClient.get<Book>(`${this.baseURL}/books/${id}/one`).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  deleteBook(id: number): Observable<void> {
    return this.httpClient.delete<void>(`${this.baseURL}/books/${id}/delete`).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  updateBook(updatedBook: Book, role: string): Observable<Book> {

    delete updatedBook.picByte;
    delete updatedBook.retrievedImage;

    let url: string = `${this.baseURL}/books/update/${updatedBook.id}`;
    url += (role === "seller") ? '/price' : '';

    const headers = { 'content-type': 'application/json' };
    
    const body = JSON.stringify(updatedBook);
    return this.httpClient.put<Book>(url, body, { headers: headers }).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  deleteBooks(): Observable<void> {
    return this.httpClient.delete<void>(`${this.baseURL}/books/deleteAll`).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }





  // Order

  countOrders(uid: number): Observable<number> {
    return this.httpClient.get<number>(`${this.baseURL}/orders/${uid}/count`).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  getOrders(uid: number, page: number, size: number): Observable<Order[]> {
    return this.httpClient.get<Order[]>(`${this.baseURL}/orders/${uid}/get?page=${page - 1}&size=${size}`).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  addOrder(uid: number): Observable<any> {
    return this.httpClient.get<any>(`${this.baseURL}/orders/${uid}/add`).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  getOrder(id: number): Observable<Order> {
    return this.httpClient.get<Order>(`${this.baseURL}/orders/${id}/one`).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  // Non ancora usato
  deleteOrder(id: number): Observable<void> {
    return this.httpClient.delete<void>(`${this.baseURL}/orders/${id}/delete`).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

// Non ancora usato
  updateOrder(updatedOrder: Order): Observable<Order> {
    const headers = { 'content-type': 'application/json' };
    const body = JSON.stringify(updatedOrder);
    return this.httpClient.put<Order>(`${this.baseURL}/orders/update/${updatedOrder.id}`, body, { headers: headers }).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

 
  deleteAlRecordsOnOrder():Observable<void> {
    return this.httpClient.delete<void>(`${this.baseURL}/orders/deleteAll`).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }




  // INTERSECT TABLE => OrderBook

  addOrderBook(newOrderBook: OrderBook): Observable<OrderBook> {
    const headers = { 'Content-Type': 'application/json' };
    const body = JSON.stringify(newOrderBook);
    return this.httpClient.post<OrderBook>(`${this.baseURL}/order_book/add`, body, { headers: headers }).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }


  getOrderBooksByOrderId(orderId: number): Observable<OrderBook[]>{
    return this.httpClient.get<OrderBook[]>(`${this.baseURL}/order_book/${orderId}/get`).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

/*
  deleteOrderBooksByBookId(bookId: number): Observable<void>{
    return this.httpClient.delete<void>(`${this.baseURL}/order_book/${bookId}/delete`).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }*/

  //aggiunto mo
  deleteAllRecordsOnOrderBookByOrderId(orderId: number): Observable<void>{
    return this.httpClient.delete<void>(`${this.baseURL}/order_book/${orderId}/delete/orderId`).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }
  

  deleteAllRecordsOnOrderBook():Observable<void> {
    return this.httpClient.delete<void>(`${this.baseURL}/order_book/deleteAll`).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

 

}

