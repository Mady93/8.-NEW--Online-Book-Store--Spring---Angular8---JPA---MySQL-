import { Component, OnInit } from '@angular/core';
import { HttpClientService } from 'src/app/service/http-client.service';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from 'src/app/service/auth.service';
import { Discount } from 'src/app/model/Discount';

@Component({
  selector: 'app-discount',
  templateUrl: './discount.component.html',
  styleUrls: ['./discount.component.scss']
})
export class DiscountComponent implements OnInit {


  sales: Array<Discount>;
  action: string;
  selectedSale: Discount;

  // Aggiunto paginazione
  allSales: number;
  page: number = 1;
  size: number = 3;

  // Aggiunto errori
  msg: any;
  ok: any;
  status: any;

  constructor(private httpClientService: HttpClientService, private router: Router, private activatedRoute: ActivatedRoute, private authService: AuthService) { }

  // ngOnInit(): Questo metodo viene chiamato durante l'inizializzazione del componente. Inizializza alcune variabili e chiama il metodo refreshData() per ottenere gli utenti e gestire la paginazione.
  ngOnInit() {

    this.selectedSale = new Discount();
    this.page = parseInt(this.activatedRoute.snapshot.queryParamMap.get('page')) || 1;
    this.refreshData();

  }

  // escapeRegExp(string) e replaceAll(str, find, replace): Queste funzioni servono per effettuare la sostituzione di determinati caratteri nella stringa, utilizzate per gestire eventuali caratteri speciali o di escape nei messaggi di errore.
  escapeRegExp(string) {
    return string.replace(/[.*+?^${}()|[\]\\]/g, '\\$&'); // $& means the whole matched string
  }

  // escapeRegExp(string) e replaceAll(str, find, replace): Queste funzioni servono per effettuare la sostituzione di determinati caratteri nella stringa, utilizzate per gestire eventuali caratteri speciali o di escape nei messaggi di errore.
  replaceAll(str, find, replace) {
    return str.replace(new RegExp(this.escapeRegExp(find), 'g'), replace);
  }

  // refreshData(): Questo metodo ottiene il numero totale di discount e la lista degli discount attraverso il servizio HttpClientService. Gestisce le risposte e gli errori del server, mostrando messaggi di errore se necessario. Aggiorna anche la proprietà allSales per la paginazione.
  refreshData() {
    this.httpClientService.countDiscounts().subscribe({
      next: (num: number) => {
        console.log("Array length = " + num)
        this.allSales = num;
        this.msg = "";
        this.ok = ""

        this.httpClientService.getDiscounts(this.page, this.size).subscribe({
          next: (res) => {
            console.log(res);
            this.msg = "";
            this.ok = "";
            this.handleSuccessfulResponse(res);
            this.activatedRoute.queryParams.subscribe(
              (params) => {
                this.action = params['action'];
                const selectedSaleId = params['id'];

                if (selectedSaleId) {
                  this.selectedSale = this.sales.find(sale => sale.id === +selectedSaleId);
                }
              }
            );
          },
          error: (err: HttpErrorResponse) => {
            console.log(err.message);
            this.msg = this.replaceAll(err.message, "#", "<br>");

            setTimeout(() => {
              this.msg = '';
            }, 2000);


          },
          complete: () => {
            console.log("Completed getSales()");
          }
        });

      },
      error: (err: HttpErrorResponse) => {
        this.allSales = 0;
        this.sales = [];
        this.msg = this.replaceAll(err.message, "#", "<br>");

        /*setTimeout(() => {
          this.msg = '';
        }, 2000);
        */

      },
      complete: () => {
        console.log("Completed countSales()");
      }
    });
  }


  // renderPage(event: number): Questo metodo gestisce l'evento di cambio pagina per la paginazione, reindirizzando l'utente alla stessa pagina ma con il numero di pagina aggiornato. Chiama anche refreshData() per aggiornare la lista degli discounts.
  renderPage(event: number) {
    this.page = (event);

    let qp = JSON.parse(JSON.stringify(this.activatedRoute.snapshot.queryParams));
    if (qp.action == 'view') delete qp.action;

    this.router.navigate(['marketing', 'discount'], { queryParams: qp });
    this.refreshData();
  }


  // ho aggiunto un query param page per mantenere pa paginazione al refresh della pagina sul browser
  // viewSale(id: number): Questo metodo reindirizza l'utente alla pagina di visualizzazione di un discount specifico, tenendo conto del numero di pagina corrente.
  viewSale(id: number) {
    //debugger;
    this.router.navigate(['marketing', 'discount'], { queryParams: { id, action: 'view', page: this.page } });
    this.refreshData();
  }

  // addSale(): Questo metodo reindirizza l'utente alla pagina di aggiunta di un nuovo discount, mantenendo il numero di pagina corrente.
  addSale() {
    this.selectedSale = new Discount();
    this.router.navigate(['marketing', 'discount'], { queryParams: { action: 'add', page: this.page } });
    this.refreshData();
  }

  // handleSuccessfulResponse(res): Questa funzione gestisce la risposta del server che contiene la lista degli utenti e la assegna alla variabile users.
  handleSuccessfulResponse(res) {
    this.sales = res;
  }

  // deleteAll(): Questo metodo invia una richiesta al server per eliminare tutti i discounts presenti nel sistema. Gestisce le risposte e gli errori del server, mostrando messaggi di conferma o di errore. Dopo aver eliminato tutti i discount con successo, aggiorna la lista degli discount richiamando refreshData().
  deleteAll() {
    this.httpClientService.deleteDiscounts().subscribe({
      next: (res: any) => {

        this.ok = res.message;
        this.msg = "";
        this.allSales = 0;
        this.sales = [];

        setTimeout(() => {
          this.ok = '';
          this.refreshData();
        }, 2000);

      },
      error: (err: HttpErrorResponse) => {

        this.msg = this.replaceAll(err.message, "#", "<br>");

        setTimeout(() => {
          this.msg = '';
        }, 2000);


      },
      complete: () => {
      }
    });
  }



}

