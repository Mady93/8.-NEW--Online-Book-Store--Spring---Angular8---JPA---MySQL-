import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { AppRoutingModule } from './app-routing.module';
import { FormsModule } from '@angular/forms';
import { NgxPaginationModule } from 'ngx-pagination';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { MatTableModule } from '@angular/material';
//import { ToastrModule } from 'ngx-toastr'; 
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatExpansionModule } from '@angular/material/expansion';


import { AppComponent } from './app.component';
import { MenuComponent } from './menu/menu.component';
import { UsersComponent } from './admin/users/users.component';
import { HttpClientModule } from '@angular/common/http';
import { AdduserComponent } from './admin/users/adduser/adduser.component';
import { ViewuserComponent } from './admin/users/viewuser/viewuser.component';
import { BooksComponent } from './admin/books/books.component';
import { AddbookComponent } from './admin/books/addbook/addbook.component';
import { ViewbookComponent } from './admin/books/viewbook/viewbook.component';
import { ShopbookComponent } from './shopbook/shopbook.component';

import { HttpClientService } from './service/http-client.service';
import { AuthService } from './service/auth.service';

import { OrderComponent } from './components/order/order.component';
import { RegisterLoginComponent } from './components/register-login/register-login.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AuthInterceptorService } from './service/auth-interceptor.service';
import { ErrorComponent } from './components/error/error.component';
import { InboxComponent } from './components/inboxes/inbox/inbox.component';
import { CommunicationComponent } from './components/communication/communication.component';
import { InboxCancelledComponent } from './components/inboxes/inbox-cancelled/inbox-cancelled.component';
import { DiscountBooksComponent } from './components/discount/marketing-association/discount-books/discount-books.component';
import { AddDiscountComponent } from './components/discount/marketing/add-discount/add-discount.component';
import { ViewDiscountComponent } from './components/discount/marketing/view-discount/view-discount.component';
import { DiscountComponent } from './components/discount/marketing/discount/discount.component';
import { DiscountAssociationComponent } from './components/discount//marketing-association/discount-association/discount-association.component';
import { AutocompleteLibModule } from 'angular-ng-autocomplete';
import { NgSelectModule } from '@ng-select/ng-select';

@NgModule({
  declarations: [
    AppComponent,
    MenuComponent,
    UsersComponent,
    AdduserComponent,
    ViewuserComponent,
    BooksComponent,
    AddbookComponent,
    ViewbookComponent,
    ShopbookComponent,
    OrderComponent,
    RegisterLoginComponent,
    ErrorComponent,
    InboxComponent,
    CommunicationComponent,
    InboxCancelledComponent,
    DiscountBooksComponent,
    AddDiscountComponent,
    ViewDiscountComponent,
    DiscountComponent,
    DiscountAssociationComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    NgxPaginationModule,
    NgbModule,
    ReactiveFormsModule,
    BrowserAnimationsModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    FontAwesomeModule,
    //ToastrModule.forRoot(),
    MatTableModule,
    MatPaginatorModule,
    MatExpansionModule,

    AutocompleteLibModule,
    NgSelectModule
  ],
  providers: [
    HttpClientService,
    AuthService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptorService,
      multi: true
    }
    ],
  bootstrap: [AppComponent]
})
export class AppModule { }
