import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { User } from 'src/app/model/User ';
import { HttpClientService } from 'src/app/service/http-client.service';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { faAdd } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-adduser',
  templateUrl: './adduser.component.html',
  styleUrls: ['./adduser.component.scss']
})
export class AdduserComponent implements OnInit {

  @Input()
  user: User

  @Output()
  userAddedEvent = new EventEmitter();

  msg: any;
  ok: any;
  faAdd = faAdd;

  constructor(private httpClientService: HttpClientService,
    private router: Router) { }

    // ngOnInit(): Questo metodo viene chiamato durante l'inizializzazione del componente e imposta il tipo di utente predefinito come "User".
  ngOnInit() {
    this.user.type = "User";
  }

  // escapeRegExp(string) e replaceAll(str, find, replace): Queste funzioni servono per effettuare la sostituzione di determinati caratteri nella stringa, utilizzate per gestire eventuali caratteri speciali o di escape nei messaggi di errore.
  escapeRegExp(string) {
    return string.replace(/[.*+?^${}()|[\]\\]/g, '\\$&'); // $& means the whole matched string
  }

    // escapeRegExp(string) e replaceAll(str, find, replace): Queste funzioni servono per effettuare la sostituzione di determinati caratteri nella stringa, utilizzate per gestire eventuali caratteri speciali o di escape nei messaggi di errore.
  replaceAll(str, find, replace) {
    return str.replace(new RegExp(this.escapeRegExp(find), 'g'), replace);
  }

  // addUser(): Questo metodo invia una richiesta al server per aggiungere un nuovo utente utilizzando il servizio HttpClientService. Gestisce le risposte e gli errori del server, mostrando messaggi di conferma o di errore. Dopo aver aggiunto l'utente con successo, emette l'evento userAddedEvent per segnalare agli altri componenti che un nuovo utente è stato aggiunto. Inoltre, reindirizza l'utente alla pagina degli utenti.
  addUser() {
    this.httpClientService.addUser(this.user).subscribe({
      next: (res: any) => {
        this.ok = res.message;

        setTimeout(() => {
          this.ok = ''; 
          this.userAddedEvent.emit(); 
          this.router.navigate(['admin', 'users']); 
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
    this.router.navigate(['admin', 'users']);
  }

}
