import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClientService } from '../service/http-client.service';
import { Book } from '../model/Book';
import { HttpErrorResponse } from '@angular/common/http';
import { User } from '../model/User ';
import { faCartPlus, faCartShopping, faDollarSign, faQuestion, faUserEdit } from '@fortawesome/free-solid-svg-icons';
import { AuthService } from '../service/auth.service';

@Component({
  selector: 'app-shopbook',
  templateUrl: './shopbook.component.html',
  styleUrls: ['./shopbook.component.css']
})

export class ShopbookComponent implements OnInit {

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

  // Aggiunto errori
  msg: any;
  status: any;

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



    if (localStorage["boughtCart"] === "true")
    {
      localStorage["boughtCart"] = false;
      this.authService.buy().then(res => {
        this.emptyCart();
        location.href = "/order";
      }, err => {

      });
    }

    this.refreshData();
  }


  //Aggiunto quantity nel localStorage
  updateQuantity() {
    localStorage.setItem('cart', JSON.stringify(this.cartBooks));
  }

  //Modificato in quanto si necessita di un refresh per ricevere il next dei valori successivi della paginazione
  refreshData() {
    this.httpClientService.countBooks().subscribe({
      next: (num: number) => {
        console.log("Length array = " + num);
        this.allBooks = num;
        this.msg = "";

        this.httpClientService.getBooks(this.page, this.size).subscribe({
          next: (response) => {
            console.log(response);
            this.msg = "";
            this.handleSuccessfulResponse(response);
          },
          error: (err: HttpErrorResponse) => {
            console.log(err.message);
            this.msg = this.replaceAll(err.message, "#", "<br>");
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
      bookwithRetrievedImageField.retrievedImage = 'data:image/jpeg;base64,' + book.picByte;
      bookwithRetrievedImageField.author = book.author;
      bookwithRetrievedImageField.price = book.price;
      bookwithRetrievedImageField.picByte = book.picByte;
      this.books.push(bookwithRetrievedImageField);
    }
  }

  addToCart(bookId) {

    //retrieve book from books array using the book id
    let book = this.books.find(book => {
      return book.id === +bookId;
    });
    let cartData = [];
    //retrieve cart data from localstorage
    let data = localStorage.getItem('cart');
    console.log(data);
    //parse it to json 
    if (data !== null) {
      cartData = JSON.parse(data);
    }
    console.log('hii');
    // add the selected book to cart data


    let t = {
      id: book.id,
      name: book.name,
      author: book.author,
      price: book.price,
      //picByte: book.picByte,
      q: 1
    };

    cartData.push(t);
    //updated the cartBooks
    this.updateCartData(cartData);
    //save the updated cart data in localstorage

    localStorage.setItem('cart', JSON.stringify(cartData));
    //make the isAdded field of the book added to cart as true
    book.isAdded = true;
  }

  updateCartData(cartData) {
    this.cartBooks = cartData;
  }

  goToCart() {
    this.router.navigate(['/order']);
  }

  buyCart() {

    if (!this.authService.isLogged()){
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
