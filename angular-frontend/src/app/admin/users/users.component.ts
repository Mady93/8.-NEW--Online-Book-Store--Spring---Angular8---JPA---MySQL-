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

  ngOnInit() {

    this.selectedUser = new User();
    this.page = parseInt(this.activatedRoute.snapshot.queryParamMap.get('page')) || 1;
    this.refreshData();

  }

  // Aggiunto regex errori
  escapeRegExp(string) {
    return string.replace(/[.*+?^${}()|[\]\\]/g, '\\$&'); // $& means the whole matched string
  }

  // Aggiunto regex errori
  replaceAll(str, find, replace) {
    return str.replace(new RegExp(this.escapeRegExp(find), 'g'), replace);
  }

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


  // evento per la paginazione
  renderPage(event: number) {
    this.page = (event);

    let qp = JSON.parse(JSON.stringify(this.activatedRoute.snapshot.queryParams));
    if (qp.action == 'view') delete qp.action;



    this.router.navigate(['admin', 'users'], { queryParams: qp });
    this.refreshData();
  }

  // ho aggiunto un query param page per mantenere pa paginazione al refresh della pagina sul browser
  viewUser(id: number) {
    //debugger;
    this.router.navigate(['admin', 'users'], { queryParams: { id, action: 'view', page: this.page } });
    this.refreshData();
  }

  addUser() {
    this.selectedUser = new User();
    this.router.navigate(['admin', 'users'], { queryParams: { action: 'add', page: this.page } });
    this.refreshData();
  }

  handleSuccessfulResponse(res) {
    this.users = res;
  }

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
