import { Component, OnInit, Input, Output,EventEmitter } from '@angular/core';
import { User } from 'src/app/model/User ';
import { HttpClientService } from 'src/app/service/http-client.service';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { faAdd } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-adduser',
  templateUrl: './adduser.component.html',
  styleUrls: ['./adduser.component.css']
})
export class AdduserComponent implements OnInit {

  @Input()
  user: User

  @Output()
  userAddedEvent = new EventEmitter();

  msg: any;
  faAdd = faAdd;

  constructor(private httpClientService: HttpClientService,
    private router: Router) { }

  ngOnInit() {
  }

/*  addUser() {
    this.httpClientService.addUser(this.user).subscribe(
      (user) => {
        this.userAddedEvent.emit();
        this.router.navigate(['admin', 'users']);
      }
    );
  } */

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
      next: (res) => {
        this.msg = "";
        this.userAddedEvent.emit();
        this.router.navigate(['admin', 'users']);
      },
      error: (err:HttpErrorResponse) => {
        this.msg = this.replaceAll(err.message, "#", "<br>");
      },
      complete: () => {
       
      }
    })
  }


  closeFunction(){
    this.router.navigate(['admin', 'users']);
  }

}
