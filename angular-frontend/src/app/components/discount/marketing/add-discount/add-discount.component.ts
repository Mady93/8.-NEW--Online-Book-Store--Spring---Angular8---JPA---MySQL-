import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { HttpClientService } from 'src/app/service/http-client.service';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { faAdd } from '@fortawesome/free-solid-svg-icons';
import { Discount } from 'src/app/model/Discount';

@Component({
  selector: 'app-add-discount',
  templateUrl: './add-discount.component.html',
  styleUrls: ['./add-discount.component.scss']
})
export class AddDiscountComponent implements OnInit {

 
  @Input()
  sale: Discount;

  @Output()
  saleAddedEvent = new EventEmitter();

  msg: any;
  ok: any;
  faAdd = faAdd;

  constructor(private httpClientService: HttpClientService,
    private router: Router) { }

    // ngOnInit(): Questo metodo viene chiamato durante l'inizializzazione del componente.
  ngOnInit() {
    
  }

  // escapeRegExp(string) e replaceAll(str, find, replace): Queste funzioni servono per effettuare la sostituzione di determinati caratteri nella stringa, utilizzate per gestire eventuali caratteri speciali o di escape nei messaggi di errore.
  escapeRegExp(string) {
    return string.replace(/[.*+?^${}()|[\]\\]/g, '\\$&'); // $& means the whole matched string
  }

    // escapeRegExp(string) e replaceAll(str, find, replace): Queste funzioni servono per effettuare la sostituzione di determinati caratteri nella stringa, utilizzate per gestire eventuali caratteri speciali o di escape nei messaggi di errore.
  replaceAll(str, find, replace) {
    return str.replace(new RegExp(this.escapeRegExp(find), 'g'), replace);
  }

  // addSale(): Questo metodo invia una richiesta al server per aggiungere un nuovo sconto utilizzando il servizio HttpClientService. Gestisce le risposte e gli errori del server, mostrando messaggi di conferma o di errore. Dopo aver aggiunto lo sconto con successo, emette l'evento saleAddedEvent per segnalare agli altri componenti che un nuovo sconto è stato aggiunto. Inoltre, reindirizza l'utente alla pagina degli sconti.
  addSale() {
    this.httpClientService.addDiscount(this.sale).subscribe({
      next: (res: any) => {
        this.ok = res.message;

        setTimeout(() => {
          this.ok = ''; 
          this.saleAddedEvent.emit(); 
          this.router.navigate(['marketing', 'discount']); 
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

// closeFunction(): Questo metodo reindirizza l'utente alla pagina degli sconti senza effettuare alcuna operazione.
  closeFunction() {
    this.router.navigate(['marketing', 'discount']);
  }

}
