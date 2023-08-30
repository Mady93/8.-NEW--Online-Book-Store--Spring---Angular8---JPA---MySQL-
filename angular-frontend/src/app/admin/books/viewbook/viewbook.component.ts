import { Component, OnInit, Input, EventEmitter, Output } from '@angular/core';
import { Book } from 'src/app/model/Book';
import { HttpClientService } from 'src/app/service/http-client.service';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from 'src/app/service/auth.service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-viewbook',
  templateUrl: './viewbook.component.html',
  styleUrls: ['./viewbook.component.scss']
})
export class ViewbookComponent implements OnInit {

  @Input()
  book: Book;

  @Output()
  bookDeletedEvent = new EventEmitter();

  msg: any;
  ok: any;

  constructor(private httpClientService: HttpClientService, private router: Router, private auth: AuthService, private activedRoute: ActivatedRoute) { }

  ngOnInit() {
  }

  escapeRegExp(string) {
    return string.replace(/[.*+?^${}()|[\]\\]/g, '\\$&'); // $& means the whole matched string
  }


  replaceAll(str, find, replace) {
    return str.replace(new RegExp(this.escapeRegExp(find), 'g'), replace);
  }


  deleteBook() {
    this.httpClientService.deleteBook(this.book.id).subscribe({
      next: (res: any) => {

        this.msg = "";
        this.ok = res.message;

        setTimeout(() => {
          this.ok = '';
          this.bookDeletedEvent.emit();
          this.router.navigate([this.auth.role.toLowerCase(), 'books']);
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


  editBook() {


    let qp = JSON.parse(JSON.stringify(this.activedRoute.snapshot.queryParams));

    qp.action = 'edit'
    qp.id = this.book.id

    this.router.navigate([this.auth.role.toLowerCase(), 'books'], { queryParams: qp });
  }


  closeFunction() {
    this.router.navigate([this.auth.role.toLowerCase(), 'books']);
  }

}
