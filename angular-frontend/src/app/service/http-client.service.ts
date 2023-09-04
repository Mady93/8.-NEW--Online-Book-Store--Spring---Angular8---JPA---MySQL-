import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { User } from '../model/User ';
import { Book } from '../model/Book';
import { catchError } from 'rxjs/operators';
import { Observable, throwError } from 'rxjs';
import { Order } from '../model/Order';
import { OrderBook } from '../model/OrderBook';
import { Email } from '../model/Email';

@Injectable({
  providedIn: 'root'
})
export class HttpClientService {

  private baseURL = 'http://localhost:3000'; // Dichiarazione come membro della classe -- porta middleware

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

    let body = { uid: uid, role: role };
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


  findBookByName(name: string): Observable<Book[]> {
    return this.httpClient.get<Book[]>(`${this.baseURL}/books/${name}/find`).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }


  uploadImageBook(book: Book, imageFile: any): Observable<Book> {
    const formData = new FormData();
    formData.append('imageFile', imageFile, imageFile.name);

    //const headers = { 'Content-Type': 'application/json' };
    //const body = JSON.stringify(book);

    return this.httpClient.post<Book>(`${this.baseURL}/books/upload`, formData).pipe(
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

    delete newBook.picByte;

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

  updateBook(updatedBook: Book): Observable<Book> {


    delete updatedBook.picByte;
    //delete updatedBook.retrievedImage;

    let url: string = `${this.baseURL}/books/update/${updatedBook.id}`;
    const headers = { 'content-type': 'application/json' };

    const body = JSON.stringify(updatedBook);
    return this.httpClient.put<Book>(url, body, { headers: headers }).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  updateBookJustPrice(updatedBook: Book): Observable<Book> {
    delete updatedBook.picByte;
    //delete updatedBook.retrievedImage;

    let url: string = `${this.baseURL}/books/update/${updatedBook.id}/price`;
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

  getOrder(uid: number, oid: number): Observable<Order> {
    return this.httpClient.get<Order>(`${this.baseURL}/orders/${uid}/${oid}/one`).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }


  deleteOrder(id: number, reason: string): Observable<void> {

    const headers = { 'content-type': 'application/json' };
    let encReason: string = encodeURIComponent(reason);

    return this.httpClient.delete<void>(`${this.baseURL}/orders/${id}/delete?reason=${encReason}`, { headers: headers }).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  updateOrderState(updatedOrder: Order): Observable<Order> {
    /*
    const headers = { 'content-type': 'application/json' };
    const body = JSON.stringify(updatedOrder);
    return this.httpClient.put<Order>(`${this.baseURL}/orders/update/${updatedOrder.id}/${state}`, body, { headers: headers }).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
    */

    const headers = { 'content-type': 'application/json' };
    const body = JSON.stringify(updatedOrder);
    return this.httpClient.put<Order>(`${this.baseURL}/orders/update?action=state`, body, { headers: headers }).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );

  }


  updateOrderEdit(updatedOrder: Order): Observable<Order> {

    /*
    const headers = { 'content-type': 'application/json' };
    const body = JSON.stringify(updatedOrder);
    return this.httpClient.put<Order>(`${this.baseURL}/orders/update/edit`, body, { headers: headers }).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
    */

    const headers = { 'content-type': 'application/json' };
    const body = JSON.stringify(updatedOrder);
    return this.httpClient.put<Order>(`${this.baseURL}/orders/update?action=edit`, body, { headers: headers }).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );

  }

  countTotalOrders(): Observable<number> {
    return this.httpClient.get<number>(`${this.baseURL}/orders/count/all`).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  getWorkingOrders(page: number, size: number): Observable<Order[]> {
    return this.httpClient.get<Order[]>(`${this.baseURL}/orders/inbox/all?page=${page - 1}&size=${size}`).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  // AGGIUNTO
  countTotalOrdersStateCanceled(): Observable<number> {
    return this.httpClient.get<number>(`${this.baseURL}/orders/count/allCanceled`).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  // AGGIUNTO
  getTotalOrdersStateCanceled(page: number, size: number): Observable<Order[]> {
    return this.httpClient.get<Order[]>(`${this.baseURL}/orders/inbox/allCanceled?page=${page - 1}&size=${size}`).pipe(
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


  getOrderBooksByOrderId(orderId: number): Observable<OrderBook[]> {
    return this.httpClient.get<OrderBook[]>(`${this.baseURL}/order_book/${orderId}/get`).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }


  updateOrderBook(ob: OrderBook): Observable<OrderBook> {

    const headers = { 'Content-Type': 'application/json' };
    const body = JSON.stringify(ob);

    return this.httpClient.put<OrderBook>(`${this.baseURL}/order_book/update/${ob.book.id}/${ob.order.id}`, body, { headers: headers }).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }






  // Email
  getEmailsByUserId(userId: number): Observable<Email[]> {
    return this.httpClient.get<Email[]>(`${this.baseURL}/emails/${userId}/list`).pipe(
      catchError((err: HttpErrorResponse) => this.handleError(err))
    );
  }

  


}

