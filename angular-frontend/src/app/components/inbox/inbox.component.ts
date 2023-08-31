import { Component, OnInit } from '@angular/core';
import { Order } from 'src/app/model/Order';
import { HttpClientService } from 'src/app/service/http-client.service';
import { AuthService } from 'src/app/service/auth.service';
import { HttpErrorResponse } from '@angular/common/http';
import { OrderBook } from 'src/app/model/OrderBook';

@Component({
  selector: 'app-inbox',
  templateUrl: './inbox.component.html',
  styleUrls: ['./inbox.component.scss']
})
export class InboxComponent implements OnInit {

  constructor(private service: HttpClientService, private auth: AuthService) { }

  page: number = 1;
  size: number = 3;
  orders: Order[] = [];
  allOrders: number = 0;
  ordersDetails: { orders: any[], total: number }[] = [];

  msg: any;
  ok: any;

  // ngOnInit(): Questo metodo viene chiamato durante l'inizializzazione del componente e richiama la funzione fetchOrders() per ottenere gli ordini dell'utente corrente.
  ngOnInit() {
    this.fetchOrders();
  }

  // openDetail(oid: number): Questo metodo viene chiamato quando l'utente vuole visualizzare i dettagli di un ordine specifico. Richiama fetchOrderById(oid) solo se i dettagli dell'ordine non sono già stati caricati.
  openDetail(oid: number) {
    if (this.ordersDetails[oid] === undefined) this.fetchOrderById(oid);
  }


  // fetchOrderById(oid: number): Questo metodo ottiene i dettagli di un ordine specifico attraverso il servizio HttpClientService. Calcola il totale degli importi degli oggetti dell'ordine e memorizza i dettagli nell'oggetto ordersDetails utilizzando l'ID dell'ordine come chiave.
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


  // escapeRegExp(string) e replaceAll(str, find, replace): Queste funzioni servono per gestire eventuali caratteri speciali o di escape nei messaggi di errore.
  escapeRegExp(string) {
    return string.replace(/[.*+?^${}()|[\]\\]/g, '\\$&'); // $& means the whole matched string
  }

  // escapeRegExp(string) e replaceAll(str, find, replace): Queste funzioni servono per gestire eventuali caratteri speciali o di escape nei messaggi di errore.
  replaceAll(str, find, replace) {
    return str.replace(new RegExp(this.escapeRegExp(find), 'g'), replace);
  }

  // fetchOrders(): Questo metodo ottiene gli ordini dell'utente attraverso il servizio HttpClientService. Gestisce le risposte e gli errori del server, mostrando messaggi di errore se necessario, e assegna i dati degli ordini alla variabile orders per la visualizzazione.
  fetchOrders() {
    this.service.countTotalOrders().subscribe({
      next: (num: number) => {
        this.ok = "";
        this.msg = "";
        this.allOrders = num;

        this.service.getWorkingOrders(this.page, this.size).subscribe({
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


  // renderPage(event: number): Questo metodo viene chiamato quando l'utente cambia pagina nella visualizzazione degli ordini. Aggiorna il numero di pagina e richiama fetchOrders() per ottenere gli ordini corrispondenti.
  renderPage(event: number) {
    this.page = (event);
    this.ordersDetails = [];
    this.fetchOrders();
  }


  // updateOrderState(order: Order): Questo metodo viene chiamato quando l'utente aggiorna lo stato di un ordine a "Send". Utilizza il servizio HttpClientService per aggiornare lo stato dell'ordine. Gestisce le risposte e gli errori del server, mostrando messaggi di errore se necessario, e aggiorna la visualizzazione degli ordini chiamando fetchOrders()
  updateOrderState(order: Order) {

    this.service.updateOrder(order, "Send").subscribe({
      next: (res: any) => {
        this.ok = res.message;

        setTimeout(() => {
          this.ok = '';

          //fix aggiornamento indice pagina
          if (this.allOrders == 1) this.page = 1;
          else if ((this.allOrders - ((this.page - 1) * this.size)) == 1) this.page--;

          this.fetchOrders();
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

}






