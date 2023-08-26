import { Component, OnInit } from '@angular/core';
import { OrderBook } from 'src/app/model/OrderBook';
import { Order } from 'src/app/model/Order';
import { AuthService } from 'src/app/service/auth.service';
import { HttpClientService } from 'src/app/service/http-client.service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-order',
  templateUrl: './order.component.html',
  styleUrls: ['./order.component.scss']
})
export class OrderComponent implements OnInit {

  constructor(private service: HttpClientService, private auth: AuthService) { }



  page: number = 1;
  size: number = 3;
  orders: Order[] = [];
  allOrders: number = 0;

  ordersDetails: { orders: any[], total: number }[] = [];

  msg: any;
  ok: any;


  ngOnInit() {
    this.fetchOrdersByUid(this.auth.uid);
  }

  openDetail(oid: number) {
    if (this.ordersDetails[oid] === undefined) this.fetchOrderById(oid);
  }

  // Aggiunto regex errori
  escapeRegExp(string) {
    return string.replace(/[.*+?^${}()|[\]\\]/g, '\\$&'); // $& means the whole matched string
  }

  // Aggiunto regex errori
  replaceAll(str, find, replace) {
    return str.replace(new RegExp(this.escapeRegExp(find), 'g'), replace);
  }

  fetchOrdersByUid(uid: number) {

    this.service.countOrders(uid).subscribe({
      next: (num: number) => {
        this.ok = "";
        this.msg = "";
        this.allOrders = num;

        this.service.getOrders(uid, this.page, this.size).subscribe({
          next: (orders: Order[]) => {
            this.msg = "";
            this.ok = "";
            this.orders = orders;
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

        /*setTimeout(() => {
          this.msg = '';
        }, 1000);
        */

      },
      complete: () => { }
    });

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

  renderPage(event: number) {
    this.page = (event);
    this.ordersDetails = [];
    this.fetchOrdersByUid(1);
  }

}
