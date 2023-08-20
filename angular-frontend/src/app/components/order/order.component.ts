import { Component, OnInit } from '@angular/core';
import { JoinTable } from 'src/app/model/JoinTable';
import { Order } from 'src/app/model/Order';
import { HttpClientService } from 'src/app/service/http-client.service';

@Component({
  selector: 'app-order',
  templateUrl: './order.component.html',
  styleUrls: ['./order.component.scss']
})
export class OrderComponent implements OnInit {

  constructor(private service: HttpClientService) { }



  page: number = 1;
  size: number = 3;
  orders: Order[] = [];
  allOrders: number = 0;
  ordersDetails: { orders: any[], total: number }[] = [];

  ngOnInit() {

    this.fetchOrdersByUid(1);

  }

  openDetail(oid: number) {

    if (this.ordersDetails[oid] === undefined) this.fetchOrderById(oid);

  }

  fetchOrdersByUid(uid: number) {

    this.service.countOrders(uid).subscribe({
      next: (num: number) => {
        this.allOrders = num;

        this.service.getOrders(uid, this.page, this.size).subscribe({
          next: (orders: Order[]) => {
            this.orders = orders;
          }
        });

      }
    });

  }

  fetchOrderById(oid: number) {

    this.service.getJoinTablesByOrderId(oid).subscribe({
      next: (jt: JoinTable[]) => {

        this.ordersDetails[oid] = { orders: [], total: 0 };

        let total = 0;
        for (let ele of jt) total += ele.quantity * ele.price;

        this.ordersDetails[oid].orders = jt;
        this.ordersDetails[oid].total = total;
      }

    })

  }

  renderPage(event: number) {
    this.page = (event);
    this.ordersDetails = [];
    this.fetchOrdersByUid(1);
  }

}
