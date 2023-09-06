import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClientService } from 'src/app/service/http-client.service';
import { Book } from 'src/app/model/Book';
import { HttpErrorResponse } from '@angular/common/http';
import { User } from 'src/app/model/User ';
import { faCartPlus, faCartShopping, faDollarSign, faQuestion, faUserEdit } from '@fortawesome/free-solid-svg-icons';
import { AuthService } from 'src/app/service/auth.service';
@Component({
  selector: 'app-discount-books',
  templateUrl: './discount-books.component.html',
  styleUrls: ['./discount-books.component.scss']
})
export class DiscountBooksComponent implements OnInit {
  iconaAdd = faCartPlus;
  iconaCart = faCartShopping;
  iconaBuy = faDollarSign;
  iconaUserEdit = faUserEdit;
  iconaEmpty = faQuestion;

  books: Array<Book>;
  booksRecieved: Array<Book>;
  cartBooks: any;
  user: User;

  // Aggiunto paginazione
  allBooks: number;
  page: number = 1;
  size: number = 8;

  msg: any;

  constructor(private router: Router, private httpClientService: HttpClientService, public authService: AuthService) { }

  // Aggiunto regex errori
  escapeRegExp(string) {
    return string.replace(/[.*+?^${}()|[\]\\]/g, '\\$&'); // $& means the whole matched string
  }

  // Aggiunto regex errori
  replaceAll(str, find, replace) {
    return str.replace(new RegExp(this.escapeRegExp(find), 'g'), replace);
  }


  ngOnInit(): void {
    // Recupera i dati del carrello dal localStorage
    var data = localStorage.getItem('cart');
    //debugger;
    if (data !== null) {
      this.cartBooks = JSON.parse(data);
    } else {
      this.cartBooks = [];
    }


    if (localStorage["boughtCart"] === "true") {
      localStorage["boughtCart"] = false;
      this.authService.buy().then(res => {
        this.emptyCart();
        location.href = "/order";
      }, err => {

      });
    }

    this.refreshData();

  }


  //Modificato in quanto si necessita di un refresh per ricevere il next dei valori successivi della paginazione
  refreshData() {
    this.httpClientService.countDiscountBooks().subscribe({
      next: (num: number) => {
        console.log("Length array = " + num);
        this.allBooks = num;
        this.msg = "";

        this.httpClientService.getDiscountBooks(this.page, this.size).subscribe({
          next: (response) => {
            console.log(response);
            this.msg = "";
            this.handleSuccessfulResponse(response);
          },
          error: (err: HttpErrorResponse) => {
            console.log(err.message);
            this.msg = this.replaceAll(err.message, "#", "<br>");

            setTimeout(() => {
              this.msg = '';
            }, 1000);

          },
          complete: () => {
            console.log("Completed countBooks()");
          }
        });
      },
      error: (err: HttpErrorResponse) => {
        this.allBooks = 0;
        this.books = [];
        this.msg = this.replaceAll(err.message, "#", "<br>");
      },
      complete: () => {
        console.log("Complete getBooks()");
      }
    });

  }


  // evento per la paginazione
  renderPage(event: number) {
    this.page = (event);
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
      //bookwithRetrievedImageField.retrievedImage = 'data:image/jpeg;base64,' + book.picByte;
      bookwithRetrievedImageField.author = book.author;
      bookwithRetrievedImageField.price = book.price;
      bookwithRetrievedImageField.picByte = book.picByte;
      bookwithRetrievedImageField.isActive = book.isActive;
      this.books.push(bookwithRetrievedImageField);
    }


    //ciclo che inibisce il pulsante addCart per gli ogetti gia' nel carrello
    for (let ele of this.cartBooks) {
      let book = this.books.find(book => {
        return book.id === +ele.id;
      });

      if (book) book.isAdded = true;
    }

  }


  //Aggiunto quantity nel localStorage
  updateQuantity() {
    localStorage.setItem('cart', JSON.stringify(this.cartBooks));
  }


  addToCart(bookId) {

    let book = this.books.find(book => {
      return book.id === +bookId;
    });

    let cartData = [];
    let data = localStorage.getItem('cart');

    if (data !== null) {
      cartData = JSON.parse(data);
    }

    delete book.picByte;
    book["q"] = 1;

    cartData.push(book);
    this.cartBooks = cartData;
    localStorage.setItem('cart', JSON.stringify(cartData));
    //make the isAdded field of the book added to cart as true
    book.isAdded = true;

  }


  goToCart() {
    this.router.navigate(['/order']);
  }


  buyCart() {

    if (!this.authService.isLogged()) {
      localStorage["boughtCart"] = true;
      location.href = "/login";
      return;
    }

    this.authService.buy().then(res => {
      this.emptyCart();
      location.href = "/order";
    }, err => {

    });

  }

  emptyCart() {
    this.cartBooks = [];
    localStorage.setItem("cart", "[]");
    this.refreshData();
  }

  removeFromCart(bookId: number) {
    // Trova l'indice del libro da rimuovere nel carrello
    const index = this.cartBooks.findIndex(item => item.id === bookId);

    if (index !== -1) {
      // Rimuovi il libro dal carrello
      this.cartBooks.splice(index, 1);

      // Aggiorna il carrello nello storage locale
      localStorage.setItem('cart', JSON.stringify(this.cartBooks));

      // Trova il libro corrispondente nell'array dei libri e imposta isAdded su false
      const book = this.books.find(item => item.id === bookId);
      if (book) {
        book.isAdded = false;
      }
    }
  }

}
