import { Component, OnInit } from '@angular/core';
import { Order } from 'src/app/model/Order';
import { Book } from 'src/app/model/Book';
@Component({
  selector: 'app-inbox',
  templateUrl: './inbox.component.html',
  styleUrls: ['./inbox.component.scss']
})
export class InboxComponent implements OnInit {

  //page
  page: number = 1;
  size: number = 3;
  orders: Order[] = [];
  allOrders: number = 0;


  displayedColumns: string[] = ['Title', 'Author', 'Quantity', 'Price'];
  /*
  dataSource: Book[] = [
    { Title: 'Book 1', Author: 'Author 1', Quantity: 1, Price: 20 },
    { Title: 'Book 2', Author: 'Author 2', Quantity: 2, Price: 15 },
    { Title: 'Book 3', Author: 'Author 3', Quantity: 3, Price: 49 },
  ];
  */
  constructor() { }

  ngOnInit() {
 
  }


}
