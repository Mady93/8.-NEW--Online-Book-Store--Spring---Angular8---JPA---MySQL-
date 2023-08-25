import { Component, OnInit, Input, EventEmitter, Output } from '@angular/core';
import { Book } from 'src/app/model/Book';
import { HttpClientService } from 'src/app/service/http-client.service';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/service/auth.service';

@Component({
  selector: 'app-viewbook',
  templateUrl: './viewbook.component.html',
  styleUrls: ['./viewbook.component.css']
})
export class ViewbookComponent implements OnInit {

  @Input()
  book: Book;

  @Output()
  bookDeletedEvent = new EventEmitter();

  msg: any;

  constructor(private httpClientService: HttpClientService, private router: Router, private auth: AuthService) { }

  ngOnInit() {
  }

  escapeRegExp(string) {
    return string.replace(/[.*+?^${}()|[\]\\]/g, '\\$&'); // $& means the whole matched string
  }


  replaceAll(str, find, replace) {
    return str.replace(new RegExp(this.escapeRegExp(find), 'g'), replace);
  }


  /*
  deleteBook() {
    // debugger;
    // cancello da tutti gli ordini il libro eliminando la relazione con la intersect table => OrderBook
    this.httpClientService.deleteOrderBooksByBookId(this.book.id).subscribe({
      next: (res) => {
        this.msg = "";

       // debugger;

        // elimino il libro 
        this.httpClientService.deleteBook(this.book.id).subscribe({
          next: (res) => {
            this.msg = "";
            this.bookDeletedEvent.emit();
            this.router.navigate(['admin', 'books']);
          },
          error: (err) => {
            this.msg = this.replaceAll(err.message, "#", "<br>");
          },
          complete: () => {

          }
        });

      },
      error: (err) => {
        this.msg = this.replaceAll(err.message, "#", "<br>");
      },
      complete: () => {

      }
    });
  }
  */

  deleteBook() {
    this.httpClientService.deleteBook(this.book.id).subscribe({
      next: (res) => {
        this.msg = "";
        this.bookDeletedEvent.emit();
        this.router.navigate(['admin', 'books']);
      },
      error: (err) => {
        this.msg = this.replaceAll(err.message, "#", "<br>");
      },
      complete: () => {

      }
    });

  }


  editBook() {
    this.router.navigate(['admin', 'books'], { queryParams: { action: 'edit', id: this.book.id } });
  }


  closeFunction() {
    this.router.navigate(['admin', 'books']);
  }

}
