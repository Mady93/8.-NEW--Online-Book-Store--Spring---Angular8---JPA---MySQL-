import { Component, OnInit } from '@angular/core';
import { Book } from 'src/app/model/Book';
import { HttpClientService } from 'src/app/service/http-client.service';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from 'src/app/service/auth.service';

@Component({
  selector: 'app-books',
  templateUrl: './books.component.html',
  styleUrls: ['./books.component.css']
})
export class BooksComponent implements OnInit {

  books: Array<Book>;
  booksRecieved: Array<Book>;
  action: string;
  selectedBook: Book;


  // Aggiunto paginazione
  allBooks: number;
  page: number = 1;
  size: number = 5;

 
  msg: any;
  ok: any;

  constructor(private httpClientService: HttpClientService,
    private activedRoute: ActivatedRoute, private router: Router, private auth: AuthService) { }

  ngOnInit() {
    this.selectedBook = new Book();
    this.page = parseInt(this.activedRoute.snapshot.queryParamMap.get('page')) || 1;
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
    this.httpClientService.countBooks().subscribe({
      next: (num: number) => {
        console.log("Length array = " + num);
        this.allBooks = num;
        this.ok = "";
        this.msg = "";

        this.httpClientService.getBooks(this.page, this.size).subscribe({
          next: (res: any) => {
            console.log(res);
            this.ok = "";
            this.msg = "";
            this.handleSuccessfulResponse(res);

            this.activedRoute.queryParams.subscribe(
              (params) => {

                this.action = params['action'];

                const id = params['id'];

                if (id) {
                  this.selectedBook = this.books.find(book => {
                    return book.id === +id;
                  });
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
            console.log("Completed getBooks()");
          }
        });


      },
      error: (err: HttpErrorResponse) => {
        this.allBooks = 0;
        this.books = [];
        this.msg = this.replaceAll(err.message, "#", "<br>");

       /* setTimeout(() => {
          this.msg = '';
        }, 2000);
      */
        
      },
      complete: () => {
        console.log("Completed countBooks()")
      }
    });
  }

  renderPage(event: number) {
    this.page = (event);

    let qp = JSON.parse(JSON.stringify(this.activedRoute.snapshot.queryParams));
    if (qp.action == 'view') delete qp.action;



    this.router.navigate([this.auth.role.toLowerCase(), 'books'], { queryParams: qp });
    this.refreshData();
  }


  // we will be taking the books response returned from the database
  // and we will be adding the retrieved   
  handleSuccessfulResponse(response) {
    this.books = new Array<Book>();
    //get books returned by the api call
    this.booksRecieved = response;
    for (const book of this.booksRecieved) {

      const bookwithRetrievedImageField = new Book();
      bookwithRetrievedImageField.id = book.id;
      bookwithRetrievedImageField.name = book.name;
      //populate retrieved image field so that book image can be displayed
      bookwithRetrievedImageField.retrievedImage = 'data:image/jpeg;base64,' + book.picByte;
      bookwithRetrievedImageField.author = book.author;
      bookwithRetrievedImageField.price = book.price;
      bookwithRetrievedImageField.picByte = book.picByte;
      this.books.push(bookwithRetrievedImageField);
    }
  }

  addBook() {

    let t = document.querySelector("form");

    if (t != undefined) t.style.backgroundImage = "none";

    this.selectedBook = new Book();
    this.router.navigate([this.auth.role.toLowerCase(), 'books'], { queryParams: { action: 'add', page: this.page } });

  }

  viewBook(id: number) {
    this.router.navigate([this.auth.role.toLowerCase(), 'books'], { queryParams: { id, action: 'view', page: this.page } });
  }

  deleteAll(){
    this.httpClientService.deleteBooks().subscribe({
      next: (res: any) => {
        this.ok = res.message;

        setTimeout(() => {
          this.ok = '';
          this.allBooks = 0;
          this.books = [];
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
