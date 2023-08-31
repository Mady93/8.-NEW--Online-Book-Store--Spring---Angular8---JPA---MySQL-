import { Component, OnInit, Input, EventEmitter, Output } from '@angular/core';
import { User } from 'src/app/model/User ';
import { HttpClientService } from 'src/app/service/http-client.service';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-viewuser',
  templateUrl: './viewuser.component.html',
  styleUrls: ['./viewuser.component.scss']
})
export class ViewuserComponent implements OnInit {

  @Input()
  user: User

  @Output()
  userDeletedEvent = new EventEmitter();

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

  // deleteUser(): Questo metodo invia una richiesta al server per eliminare l'utente attualmente visualizzato. Gestisce le risposte e gli errori del server, mostrando messaggi di conferma o di errore. Dopo aver eliminato l'utente con successo, emette l'evento 
  deleteUser() {

    this.httpClientService.deleteUser(this.user.id).subscribe({

      next: (res: any) => {

        this.ok = res.message;
        this.msg = "";

        setTimeout(() => {
          this.ok = '';
          this.userDeletedEvent.emit();
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

  // onRoleChange(newRole: string): Questo metodo gestisce il cambiamento del ruolo dell'utente tramite una richiesta al server utilizzando il servizio HttpClientService. Dopo aver eseguito la richiesta con successo, mostra un messaggio di conferma per alcuni secondi.
  onRoleChange(newRole: string) {
    
    this.httpClientService.setRole(this.user.id, newRole).subscribe({
      next: (res: any) => {
        console.log(res);
        this.ok = res.res;

        setTimeout(() => {
          this.ok = '';
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
