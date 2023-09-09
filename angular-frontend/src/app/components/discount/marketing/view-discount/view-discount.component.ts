import { Component, OnInit, Input, EventEmitter, Output } from '@angular/core';
import { HttpClientService } from 'src/app/service/http-client.service';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { Discount } from 'src/app/model/Discount';

@Component({
  selector: 'app-view-discount',
  templateUrl: './view-discount.component.html',
  styleUrls: ['./view-discount.component.scss']
})
export class ViewDiscountComponent implements OnInit {

  @Input()
  sale: Discount;

  @Output()
  saleDeletedEvent = new EventEmitter();

  msg: any;
  ok: any;

  constructor(private httpClientService: HttpClientService,
    private router: Router) { }

    // ngOnInit(): Questo metodo viene chiamato durante l'inizializzazione del componente. In questo caso, non esegue alcuna operazione particolare.
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

  // deleteSale(): Questo metodo invia una richiesta al server per eliminare l'offerta attualmente visualizzata. Gestisce le risposte e gli errori del server, mostrando messaggi di conferma o di errore. Dopo aver eliminato l'offerta con successo, emette l'evento 
  deleteSale() {

    this.httpClientService.deleteDiscount(this.sale.id).subscribe({

      next: (res: any) => {

        this.ok = res.message;
        this.msg = "";

        setTimeout(() => {
          this.ok = '';
          this.saleDeletedEvent.emit();
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

  // closeFunction(): Questo metodo reindirizza l'utente alla pagina degli utenti senza effettuare alcuna operazione.
  closeFunction() {
    this.router.navigate(['marketing', 'discount']);
  }

  

}
