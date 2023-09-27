import { Component, OnInit } from '@angular/core';
import { Order } from 'src/app/model/Order';
import { HttpClientService } from 'src/app/service/http-client.service';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from 'src/app/service/auth.service';
import { OrderBook } from 'src/app/model/OrderBook';

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

  openDetail(oid: number) {
    
    if (this.ordersDetails[oid] === undefined) this.fetchOrderById(oid);
  }

  fetchOrderById(oid: number) {
   
    this.service.getOrderBooksByOrderId(oid).subscribe({
      next: (jt: OrderBook[]) => {
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
            this.orders = orders;

            setTimeout(() => {
              console.log(orders);
              for (let order of orders) {
                  this.openDetail(order.id);
              }
              
            }, 500);
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
    this.page = (event);
    this.ordersDetails = [];
    this.fetchOrders();
  }

}








