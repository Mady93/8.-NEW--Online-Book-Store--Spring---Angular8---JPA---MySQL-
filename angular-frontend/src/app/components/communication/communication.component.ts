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

  ngOnInit() {
    this.getEmByUserId();
  }

  // Aggiunto regex errori
  escapeRegExp(string) {
    return string.replace(/[.*+?^${}()|[\]\\]/g, '\\$&'); // $& means the whole matched string
  }

  // Aggiunto regex errori
  replaceAll(str, find, replace) {
    return str.replace(new RegExp(this.escapeRegExp(find), 'g'), replace);
  }
  
  getEmByUserId(){
    const userId = this.auth.uid; // Ottieni l'uid dall'AuthService
    this.httpClientService.getEmailsByUserId(userId).subscribe({
      next: (res: Email[]) => {
        this.emailData = res;
      },
      error: (err: HttpErrorResponse) => {
        this.msg = this.replaceAll(err.message, "#", "<br>");

       /* setTimeout(() => {
          this.msg = '';
        }, 2000);*/
        
      },
      complete: () => { }
    })
  }

}
