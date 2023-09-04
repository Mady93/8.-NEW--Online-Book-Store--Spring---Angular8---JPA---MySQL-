import { Component, OnInit } from '@angular/core';
import { Order } from 'src/app/model/Order';
import { HttpClientService } from 'src/app/service/http-client.service';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from 'src/app/service/auth.service';

@Component({
  selector: 'app-inbox-cancelled',
  templateUrl: './inbox-cancelled.component.html',
  styleUrls: ['./inbox-cancelled.component.scss']
})
export class InboxCancelledComponent implements OnInit {

  page: number = 1;
  size: number = 3;
  orders: Order[] = [];
  allOrders: number = 0;
  ordersDetails: { orders: any[], total: number }[] = [];

  msg: any;
  ok: any;

  constructor(private service: HttpClientService, private auth: AuthService) { }

  ngOnInit() {
    this.fetchOrders();
  }

  // openDetail(oid: number): Questo metodo viene chiamato quando l'utente vuole visualizzare i dettagli di un ordine specifico. Richiama fetchOrderById(oid) solo se i dettagli dell'ordine non sono già stati caricati.
  openDetail(oid: number) {
    if (this.ordersDetails[oid] === undefined) this.fetchOrderById(oid);
  }

  fetchOrderById(oid: number) {
    this.service.getOrderBooksByOrderId(oid).subscribe({
      next: (jt: any[]) => {
        this.msg = "";
        this.ok = "";
        this.ordersDetails[oid] = { orders: [], total: 0 };

        let total = 0;
        for (let ele of jt) total += ele.quantity * ele.price;

        this.ordersDetails[oid].orders = jt;
        this.ordersDetails[oid].total = total;
      },
      error: (err: HttpErrorResponse) => {
        this.msg = this.replaceAll(err.message, "#", "<br>");

        setTimeout(() => {
          this.msg = '';
        }, 2000);
      },
      complete: () => { }
    });
  }

  escapeRegExp(string) {
    return string.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
  }

  replaceAll(str, find, replace) {
    return str.replace(new RegExp(this.escapeRegExp(find), 'g'), replace);
  }

  fetchOrders() {
    this.service.countTotalOrdersStateCanceled().subscribe({
      next: (num: number) => {
        this.ok = "";
        this.msg = "";
        this.allOrders = num;
  
        this.service.getTotalOrdersStateCanceled(this.page, this.size).subscribe({
          next: (orders: Order[]) => {
            this.msg = "";
            this.ok = "";
            // Inizializza lo stato aperto o chiuso dei dettagli per ciascun ordine.
            this.orders = orders.map(order => ({ ...order, detailsOpen: true }));
          },
          error: (err: HttpErrorResponse) => {
            this.msg = this.replaceAll(err.message, "#", "<br>");
  
            setTimeout(() => {
              this.msg = '';
            }, 2000);
          },
          complete: () => { }
        });
      },
      error: (err: HttpErrorResponse) => {
        this.msg = this.replaceAll(err.message, "#", "<br>");
  
        setTimeout(() => {
          this.msg = '';
        }, 2000);
      },
      complete: () => { }
    });
  }
  

  renderPage(event: number) {
    this.page = event;
    this.ordersDetails = [];
    this.fetchOrders();
  }
}
