import { Component, OnInit } from '@angular/core';
import { OrderBook } from 'src/app/model/OrderBook';
import { Order } from 'src/app/model/Order';
import { AuthService } from 'src/app/service/auth.service';
import { HttpClientService } from 'src/app/service/http-client.service';
import { HttpErrorResponse } from '@angular/common/http';
import { Book } from 'src/app/model/Book';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-order',
  templateUrl: './order.component.html',
  styleUrls: ['./order.component.scss']
})
export class OrderComponent implements OnInit {

  constructor(private service: HttpClientService, private auth: AuthService, private route: ActivatedRoute) { }



  page: number = 1;
  size: number = 3;
  orders: Order[] = [];
  allOrders: number = 0;
  results: Book[] = [];
  selectedResult: Book = null;
  selectedOrderId: number;

  ordersDetails: { orders: any[], total: number }[] = [];

  msg: any;
  ok: any;


  ngOnInit() {

    const id = this.route.snapshot.queryParamMap.get('id');

    if (id == undefined) this.fetchOrdersByUid(this.auth.uid);
    else this.fetchOrderById(parseInt(id));


  }

  openDetail(oid: number) {
    if (this.ordersDetails[oid] === undefined) this.fetchOrderBookByOrderid(oid);
  }

  // Aggiunto regex errori
  escapeRegExp(string) {
    return string.replace(/[.*+?^${}()|[\]\\]/g, '\\$&'); // $& means the whole matched string
  }

  // Aggiunto regex errori
  replaceAll(str, find, replace) {
    return str.replace(new RegExp(this.escapeRegExp(find), 'g'), replace);
  }


  fetchOrderById(oid: number) {

    let uid: number = this.auth.uid;

    this.service.getOrder(uid, oid).subscribe({
      next: (order: Order) => {
        this.orders = [];
        this.orders.push(order);

        this.allOrders = 1;

      }
    });
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

        setTimeout(() => {
          this.msg = '';
        }, 2000);


      },
      complete: () => { }
    });

  }

  fetchOrderBookByOrderid(oid: number) {

    this.service.getOrderBooksByOrderId(oid).subscribe({
      next: (jt: OrderBook[]) => {
        this.msg = "";
        this.ok = "";
        this.ordersDetails[oid] = { orders: [], total: 0 };

        let total = 0;
        for (let ele of jt) total += ele.quantity * ele.price;

        this.ordersDetails[oid].orders = jt;
        this.ordersDetails[oid].total = total;

        if (jt.length == 0) this.deleteOrder(oid);

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
    this.fetchOrdersByUid(this.auth.uid);
  }


  deleteOrder(oid: number) {
    //TODO
    this.service.deleteOrder(oid).subscribe({
      next: (res: any) => {
        this.msg = "";
        this.ok = res.message;

        setTimeout(() => {
          this.ok = '';
         
          this.fetchOrdersByUid(this.auth.uid);
        }, 2000);

      },
      error: (err: HttpErrorResponse) => {

        this.msg = this.replaceAll(err.message, "#", "<br>");

        setTimeout(() => {
          this.msg = '';
        }, 2000);


      },
      complete: () => { }

    })
  }

  addBook(oid: number) {

    if (!this.selectedResult) return;

    let ob: OrderBook = new OrderBook();
    ob.book = this.selectedResult;
    ob.order = this.orders.find(order => {
      return order.id === +oid;
    });
    ob.quantity = 1;
    delete ob.book.picByte;

    this.service.addOrderBook(ob).subscribe({
      next: () => {
        this.fetchOrderBookByOrderid(oid);
      }
    })

  }


  updateQuantity(ob: OrderBook) {

    delete ob.book.picByte;

    var that = this;

    this.service.updateOrderBook(ob).subscribe({
      next: (res: any) => {

        this.msg = "";
        this.ok = res.message;

        setTimeout(() => {
          this.ok = '';
          this.fetchOrderBookByOrderid(ob.order.id);
        }, 2000);
        //console.log("update successfully");//TODO_MSG
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



  findBook(event, oid: number) {

    this.selectedResult = null;
    this.selectedOrderId = oid;

    let that = this;

    let name = event.target.value;
    this.service.findBookByName(name).subscribe({
      next: (books: Book[]) => {

        //creo un array di id di libri gia' presenti nell'ordine
        let alreadyPresentBid = that.ordersDetails[oid].orders.map(x => x.id.bookId)
        //filtro i libri che sono gia presenti nel carrello
        this.results = books.filter(x => !alreadyPresentBid.includes(x.id));
        //prendo come default il primo risultato filtrato
        if (this.results.length > 0) this.selectedResult = this.results[0];

      },
      error: () => {

      }
    });

  }

  selectNewBook(event) {
    let bid: number = parseInt(event.target.value);
    this.selectedResult = this.results.find(book => {
      return book.id === +bid;
    });
  }

}
