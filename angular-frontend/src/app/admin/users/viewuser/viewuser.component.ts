import { Component, OnInit, Input, EventEmitter, Output } from '@angular/core';
import { User } from 'src/app/model/User ';
import { HttpClientService } from 'src/app/service/http-client.service';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';

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

  onRoleChange(newRole: string) {
    this.httpClientService.setRole(this.user.id, newRole).subscribe({
      next: (msg: any) => {
        console.log(msg);
      },
      error: (msg: any) => {
        console.log("role not changed");
      }
    })
  }

}
