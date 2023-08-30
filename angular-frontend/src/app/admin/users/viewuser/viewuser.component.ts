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

  closeFunction() {
    this.router.navigate(['admin', 'users']);
  }

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
