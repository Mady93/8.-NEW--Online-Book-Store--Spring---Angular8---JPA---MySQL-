import { Component, OnInit, Input, EventEmitter, Output } from '@angular/core';
import { User } from 'src/app/model/User ';
import { HttpClientService } from 'src/app/service/http-client.service';
import { Router } from '@angular/router';
import { faDeleteLeft, faList, faRemove } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-viewuser',
  templateUrl: './viewuser.component.html',
  styleUrls: ['./viewuser.component.css']
})
export class ViewuserComponent implements OnInit {

  @Input()
  user: User

  @Output()
  userDeletedEvent = new EventEmitter();

  msg: any;


  constructor(private httpClientService: HttpClientService,
    private router: Router) { }

  ngOnInit() {
  }

  /*
  deleteUser() {
    this.httpClientService.deleteUser(this.user.id).subscribe(
      (user) => {
        this.userDeletedEvent.emit();
        this.router.navigate(['admin', 'users']);
      }
    );
  }
   */

  escapeRegExp(string) {
    return string.replace(/[.*+?^${}()|[\]\\]/g, '\\$&'); // $& means the whole matched string
  }


  replaceAll(str, find, replace) {
    return str.replace(new RegExp(this.escapeRegExp(find), 'g'), replace);
  }


  deleteUser() {
    this.httpClientService.deleteUser(this.user.id).subscribe({
      next: (res) => {
        this.msg = "";
        this.userDeletedEvent.emit();
        this.router.navigate(['admin', 'users']);
      },
      error: (err) => {
       this.msg = this.replaceAll(err.message, "#", "<br>");
      },
      complete: () => {

      }
    })
  }


  closeFunction() {
    this.router.navigate(['admin', 'users']);
  }

}
