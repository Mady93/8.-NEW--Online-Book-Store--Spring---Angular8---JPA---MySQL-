import { Component, OnInit } from '@angular/core';
import { User } from 'src/app/model/User ';
import { HttpClientService } from 'src/app/service/http-client.service';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from 'src/app/service/auth.service';

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.scss']
})
export class UsersComponent implements OnInit {


  users: Array<User>;
  action: string;
  selectedUser: User;

  // Aggiunto paginazione
  allUsers: number;
  page: number = 1;
  size: number = 3;

  // Aggiunto errori
  msg: any;
  ok: any;
  status: any;

  constructor(private httpClientService: HttpClientService, private router: Router, private activatedRoute: ActivatedRoute, private authService: AuthService) { }

  // ngOnInit(): Questo metodo viene chiamato durante l'inizializzazione del componente. Inizializza alcune variabili e chiama il metodo refreshData() per ottenere gli utenti e gestire la paginazione.
  ngOnInit() {

    this.selectedUser = new User();
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

  // refreshData(): Questo metodo ottiene il numero totale di utenti e la lista degli utenti attraverso il servizio HttpClientService. Gestisce le risposte e gli errori del server, mostrando messaggi di errore se necessario. Aggiorna anche la proprietà allUsers per la paginazione.
  refreshData() {
    this.httpClientService.countUsers().subscribe({
      next: (num: number) => {
        console.log("Array length = " + num)
        this.allUsers = num;
        this.msg = "";
        this.ok = ""

        this.httpClientService.getUsers(this.page, this.size).subscribe({
          next: (res) => {
            console.log(res);
            this.msg = "";
            this.ok = "";
            this.handleSuccessfulResponse(res);
            this.activatedRoute.queryParams.subscribe(
              (params) => {
                this.action = params['action'];
                const selectedUserId = params['id'];

                if (selectedUserId) {
                  this.selectedUser = this.users.find(user => user.id === +selectedUserId);
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
            console.log("Completed getUsers()");
          }
        });

      },
      error: (err: HttpErrorResponse) => {
        this.allUsers = 0;
        this.users = [];
        this.msg = this.replaceAll(err.message, "#", "<br>");

        /*setTimeout(() => {
          this.msg = '';
        }, 2000);
        */

      },
      complete: () => {
        console.log("Completed countUsers()");
      }
    });
  }


  // renderPage(event: number): Questo metodo gestisce l'evento di cambio pagina per la paginazione, reindirizzando l'utente alla stessa pagina ma con il numero di pagina aggiornato. Chiama anche refreshData() per aggiornare la lista degli utenti.
  renderPage(event: number) {
    this.page = (event);

    let qp = JSON.parse(JSON.stringify(this.activatedRoute.snapshot.queryParams));
    if (qp.action == 'view') delete qp.action;

    this.router.navigate(['admin', 'users'], { queryParams: qp });
    this.refreshData();
  }


  // ho aggiunto un query param page per mantenere pa paginazione al refresh della pagina sul browser
  // viewUser(id: number): Questo metodo reindirizza l'utente alla pagina di visualizzazione di un utente specifico, tenendo conto del numero di pagina corrente.
  viewUser(id: number) {
    //debugger;
    this.router.navigate(['admin', 'users'], { queryParams: { id, action: 'view', page: this.page } });
    this.refreshData();
  }

  // addUser(): Questo metodo reindirizza l'utente alla pagina di aggiunta di un nuovo utente, mantenendo il numero di pagina corrente.
  addUser() {
    this.selectedUser = new User();
    this.router.navigate(['admin', 'users'], { queryParams: { action: 'add', page: this.page } });
    this.refreshData();
  }

  // handleSuccessfulResponse(res): Questa funzione gestisce la risposta del server che contiene la lista degli utenti e la assegna alla variabile users.
  handleSuccessfulResponse(res) {
    this.users = res;
  }

  // deleteAll(): Questo metodo invia una richiesta al server per eliminare tutti gli utenti presenti nel sistema. Gestisce le risposte e gli errori del server, mostrando messaggi di conferma o di errore. Dopo aver eliminato tutti gli utenti con successo, aggiorna la lista degli utenti richiamando refreshData().
  deleteAll() {
    this.httpClientService.deleteUsers().subscribe({
      next: (res: any) => {

        this.ok = res.message;
        this.msg = "";
        this.allUsers = 0;
        this.users = [];

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
