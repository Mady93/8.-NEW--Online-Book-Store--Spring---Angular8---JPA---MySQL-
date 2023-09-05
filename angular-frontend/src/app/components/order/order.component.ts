import { Component, OnInit } from '@angular/core';
import { OrderBook } from 'src/app/model/OrderBook';
import { Order } from 'src/app/model/Order';
import { AuthService } from 'src/app/service/auth.service';
import { HttpClientService } from 'src/app/service/http-client.service';
import { HttpErrorResponse } from '@angular/common/http';
import { Book } from 'src/app/model/Book';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-order',
  templateUrl: './order.component.html',
  styleUrls: ['./order.component.scss']
})
export class OrderComponent implements OnInit {

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

  constructor(private service: HttpClientService, private auth: AuthService, private route: ActivatedRoute, private router: Router) { }

  // ngOnInit(): Questo metodo viene chiamato durante l'inizializzazione del componente. Ottiene l'ID dell'ordine dalla query parametrica dell'URL e decide se recuperare tutti gli ordini dell'utente o solo l'ordine con l'ID specificato.
  ngOnInit() {

    const id = this.route.snapshot.queryParamMap.get('id');

    if (id == undefined) this.fetchOrdersByUid(this.auth.uid);
    else this.fetchOrderById(parseInt(id));
  }

  // openDetail(oid: number): Questo metodo viene chiamato quando l'utente desidera visualizzare i dettagli di un ordine specifico. Richiama fetchOrderBookByOrderid(oid) solo se i dettagli dell'ordine non sono già stati caricati.
  openDetail(oid: number) {
    if (this.ordersDetails[oid] === undefined) this.fetchOrderBookByOrderid(oid);
  }

  // escapeRegExp(string) e replaceAll(str, find, replace): Queste funzioni servono per effettuare la sostituzione di determinati caratteri nella stringa, utilizzate per gestire eventuali caratteri speciali o di escape nei messaggi di errore.
  escapeRegExp(string) {
    return string.replace(/[.*+?^${}()|[\]\\]/g, '\\$&'); // $& means the whole matched string
  }

  // escapeRegExp(string) e replaceAll(str, find, replace): Queste funzioni servono per effettuare la sostituzione di determinati caratteri nella stringa, utilizzate per gestire eventuali caratteri speciali o di escape nei messaggi di errore.
  replaceAll(str, find, replace) {
    return str.replace(new RegExp(this.escapeRegExp(find), 'g'), replace);
  }


  // fetchOrderById(oid: number): Questo metodo ottiene un singolo ordine attraverso il servizio HttpClientService e assegna i dettagli all'array orders.
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


  // fetchOrdersByUid(uid: number): Questo metodo ottiene tutti gli ordini dell'utente attraverso il servizio HttpClientService e gestisce la visualizzazione degli ordini.
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


            let cnt: number = 0;
            setTimeout(() => {
              let details = document.querySelectorAll("details");
              console.log(orders);
              for (let order of orders) {
                if (order.edit && order.editFrom == (""+this.auth.uid)) {
                  this.openDetail(order.id);
                }
                cnt++;
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


  // fetchOrderBookByOrderid(oid: number): Questo metodo ottiene i dettagli degli oggetti dell'ordine attraverso il servizio HttpClientService, calcola il totale dell'importo e aggiorna gli ordiniDetails.
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


  // renderPage(event: number): Questo metodo viene chiamato quando l'utente cambia pagina nella visualizzazione degli ordini. Aggiorna il numero di pagina e richiama fetchOrdersByUid(this.auth.uid).
  renderPage(event: number) {
    this.page = (event);
    this.ordersDetails = [];
    this.fetchOrdersByUid(this.auth.uid);
  }


  // deleteOrder(oid: number): Questo metodo permette di eliminare un ordine. Utilizza il servizio HttpClientService per effettuare la cancellazione e aggiorna la visualizzazione degli ordini.
  deleteOrder(oid: number) {



    let x = confirm("Are you sure you want to cancel the order?");
    if (!x) return;
    let reason = prompt("Why are you canceling the order?");



    this.service.deleteOrder(oid, reason).subscribe({
      next: (res: any) => {
        this.msg = "";
        this.ok = res.message;
        
        setTimeout(() => {
          this.ok = '';
          //fix aggiornamento indice pagina
          if (this.allOrders == 1) this.page = 1;
          else if ((this.allOrders - ((this.page - 1) * this.size)) == 1) this.page--;
          this.fetchOrdersByUid(this.auth.uid);

          // aggiunto per un refresh forzato della pagina
          window.location.reload();
          
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


  // addBook(oid: number): Questo metodo aggiunge un nuovo libro a un ordine. Utilizza il servizio HttpClientService per aggiungere un oggetto all'ordine e aggiorna la visualizzazione degli ordiniDetails.
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


    let nob: OrderBook = new OrderBook();
    nob.book.id = ob.book.id;
    nob.order.id = ob.order.id;
    nob.quantity = ob.quantity;




    this.service.updateOrderBook(nob).subscribe({
     
      next: (res: any) => {

        this.msg = "";
        this.ok = res.message;

        setTimeout(() => {
          this.ok = '';
          this.fetchOrderBookByOrderid(ob.order.id);
        }, 2000);

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


// findBook(event, oid: number): Questo metodo cerca un libro per nome e restituisce i risultati che non sono già presenti nell'ordine specificato.
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


  // selectNewBook(event): Questo metodo viene chiamato quando l'utente seleziona un nuovo libro da aggiungere all'ordine.
  selectNewBook(event) {
    let bid: number = parseInt(event.target.value);
    this.selectedResult = this.results.find(book => {
      return book.id === +bid;
    });
  }




  changeEditStatus(order: Order, evt){

    //debugger;

    if (evt.target.tagName != "SUMMARY") return;

    let no: Order = new Order();
    no.id = order.id;
    no.edit = !order.edit;

    this.service.updateOrderEdit(no).subscribe({
      next: (updOrder: Order) => {
        this.openDetail(updOrder.id);
        order.edit = updOrder.edit;
        order.editFrom = updOrder.editFrom;
      },
      error: (err: HttpErrorResponse) => {
        //this.orders[order.id]["opened"] = false;
        alert("At the moment the order is taken over by a company consultant and therefore it is not possible to make changes");
      }
    });

    
  }





}
