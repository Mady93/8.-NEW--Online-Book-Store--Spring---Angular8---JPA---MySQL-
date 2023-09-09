import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Book } from 'src/app/model/Book';
import { Discount } from 'src/app/model/Discount';
import { HttpClientService } from 'src/app/service/http-client.service';

@Component({
  selector: 'app-discount-association',
  templateUrl: './discount-association.component.html',
  styleUrls: ['./discount-association.component.scss']
})
export class DiscountAssociationComponent implements OnInit {

  books: Book[] = [];
  discounts: Discount[] = [];
  selectedBook: Book = null;
  selectedOpt: Number;
  //selectedDiscount: Discount = null;
  action: string = "Add";

  discontOptions: any[] = [];


  constructor(private http: HttpClientService) { }



  ngOnInit() {


    this.http.getDiscounts(1, 100).subscribe({
      next: (discounts: Discount[]) => {
        this.discounts = discounts;
        this.discontOptions = discounts;
      }
    });

  }



  selectEvent(book: Book) {
    this.selectedBook = book;

    if (this.selectedBook.discount != null) {
      this.action = "Update";
      //debugger;
      this.selectedOpt = this.selectedBook.discount.id;/*this.discounts.find()*/
      //this.selectedDiscount = this.selectedBook.discount;
    } else {
      this.action = "Add";
      this.selectedOpt = null;
    }

  }

  search(name: string) {

    if (name == "") return;

    this.http.findBookByName(name).subscribe({
      next: (books: Book[]) => {
        this.books = books;
      },
      error: (err: HttpErrorResponse) => {

      },
      complete: () => {}
    })
  }

  searchCleared() {
    this.selectedBook = null;
    this.action = "Add";
  }


  setDiscount(searchBox) {

    

    let dis: Discount = this.discounts.find(x=>x.id == this.selectedOpt);


    if (this.selectedBook.discount == null) {
      this.http.applyDiscountByBookId(dis, this.selectedBook.id).subscribe({
        next: (book: Book) => {
          
          searchBox.query = "";
          this.books = [];
          this.selectedBook = null;
          this.selectedOpt = null;

        },
        error: (err: HttpErrorResponse) => {

        },
        complete: () => {}
      });

    } else {
      this.http.updateDiscountByBookId(dis, this.selectedBook.id).subscribe({
        next: (book: Book) => {
  
          searchBox.query = "";
          this.books = [];
          this.selectedBook = null;
          this.selectedOpt = null;

        },
        error: (err: HttpErrorResponse) => {

        },
        complete: () => {}
      });
    }



  }

  delDiscount(searchBox) {

    this.http.deleteDiscountByBookId(this.selectedBook.id).subscribe({
      next: (res:any) => {
        searchBox.query = "";
        this.books = [];
        this.selectedBook = null;
        this.selectedOpt = null;
      },
      error: (err: HttpErrorResponse) => {

      },
      complete: () => {}
    })

  }


}
