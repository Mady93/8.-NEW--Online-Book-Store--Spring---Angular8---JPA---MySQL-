import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClientService } from '../service/http-client.service';
import { Book } from '../model/Book';
import { HttpErrorResponse } from '@angular/common/http';
import { Order } from '../model/Order';
import { User } from '../model/User ';
import { JoinTable } from '../model/JoinTable';
import { faAdd, faCartFlatbed, faCartPlus, faCartShopping, faDollarSign, faQuestion, faUserEdit } from '@fortawesome/free-solid-svg-icons';

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

  constructor(private router: Router, private httpClientService: HttpClientService) { }


  /*ngOnInit() {
    this.httpClientService.getBooks(1, 6).subscribe(
      response => this.handleSuccessfulResponse(response),
    );
    //from localstorage retrieve the cart item
    let data = localStorage.getItem('cart');
    //if this is not null convert it to JSON else initialize it as empty
    if (data !== null) {
      this.cartBooks = JSON.parse(data);
    } else {
      this.cartBooks = [];
    }
  }*/

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
      picByte: book.picByte,
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






  buy(uid: number) {
    let cart = JSON.parse(localStorage["cart"]);

    let order: Order;



    order = new Order();
    order.user = new User();
    order.user.id = uid;

    this.httpClientService.addOrder(uid).subscribe({
      next: (ret: any) => {

        let oid = ret.orderId;

        cart.forEach(ele => {

          let jt: JoinTable = new JoinTable();
          //let jt = {book: {id:ele.id}, order: {id: oid}, quantity: ele.q};

          //debugger;
          jt.book.id = ele.id;
          jt.order.id = oid;
          jt.quantity = ele.q;
          //jt.price = ele.price;


          this.httpClientService.addJoinTable(jt).subscribe({
            next: (jt: JoinTable) => {
              this.emptyCart();
            }
          })

        });

      }
    });

  }


  updateCartData(cartData) {
    this.cartBooks = cartData;
  }

  goToCart() {
    this.router.navigate(['/order']);
  }

  emptyCart() {
    this.cartBooks = [];
    localStorage.clear();
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
