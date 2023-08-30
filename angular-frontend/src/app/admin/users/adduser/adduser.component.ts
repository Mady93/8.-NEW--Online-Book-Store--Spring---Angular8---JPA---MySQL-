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

  ngOnInit() {
    this.user.type = "User";
  }

  // Aggiunto regex errori
  escapeRegExp(string) {
    return string.replace(/[.*+?^${}()|[\]\\]/g, '\\$&'); // $& means the whole matched string
  }

  // Aggiunto regex errori
  replaceAll(str, find, replace) {
    return str.replace(new RegExp(this.escapeRegExp(find), 'g'), replace);
  }

  addUser() {
    this.httpClientService.addUser(this.user).subscribe({
      next: (res: any) => {
        this.ok = res.message; // Mostra il messaggio "ok"

        setTimeout(() => {
          this.ok = ''; // Pulisci il messaggio "ok" dopo 1 secondo
          this.userAddedEvent.emit(); // Emetti l'evento
          this.router.navigate(['admin', 'users']); // Naviga alla pagina
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


  closeFunction() {
    this.router.navigate(['admin', 'users']);
  }

}
