import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Email } from 'src/app/model/Email';
import { AuthService } from 'src/app/service/auth.service';
import { HttpClientService } from 'src/app/service/http-client.service';

@Component({
  selector: 'app-communication',
  templateUrl: './communication.component.html',
  styleUrls: ['./communication.component.scss']
})
export class CommunicationComponent implements OnInit {

  msg: any;
  emailData: Email[]; // Array di email da visualizzare
  displayedColumns: string[] = ['from', 'to', 'subject', 'body', 'sendedAt', 'viewOrder']; // Nomi delle colonne visualizzate

  constructor(private httpClientService: HttpClientService, private auth: AuthService) { }

  // ngOnInit(): Questo metodo viene chiamato durante l'inizializzazione del componente e richiama la funzione getEmByUserId() per ottenere le email dell'utente corrente.
  ngOnInit() {
    this.getEmByUserId();
  }

  // escapeRegExp(string) e replaceAll(str, find, replace): Queste funzioni servono per gestire eventuali caratteri speciali o di escape nei messaggi di errore.
  escapeRegExp(string) {
    return string.replace(/[.*+?^${}()|[\]\\]/g, '\\$&'); // $& means the whole matched string
  }

  // escapeRegExp(string) e replaceAll(str, find, replace): Queste funzioni servono per gestire eventuali caratteri speciali o di escape nei messaggi di errore.
  replaceAll(str, find, replace) {
    return str.replace(new RegExp(this.escapeRegExp(find), 'g'), replace);
  }

  // getEmByUserId(): Questo metodo ottiene le email dell'utente corrente attraverso il servizio HttpClientService utilizzando l'UID fornito dal servizio AuthService. Gestisce le risposte e gli errori del server, mostrando messaggi di errore se necessario, e assegna i dati delle email alla variabile emailData per la visualizzazione.
  getEmByUserId() {
    const userId = this.auth.uid; 
    this.httpClientService.getEmailsByUserId(userId).subscribe({
      next: (res: Email[]) => {
        this.emailData = res;
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

}
