import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { UsersComponent } from './admin/users/users.component';
import { BooksComponent } from './admin/books/books.component';
import { ShopbookComponent } from './shopbook/shopbook.component';
import { OrderComponent } from './components/order/order.component';
import { RegisterLoginComponent } from './components/register-login/register-login.component';
import { ErrorComponent } from './components/error/error.component';
import { RoleGuardService } from './service/role-guard.service';
import { InboxComponent } from './components/inboxes/inbox/inbox.component';
import { CommunicationComponent } from './components/communication/communication.component';
import { InboxCancelledComponent } from './components/inboxes/inbox-cancelled/inbox-cancelled.component';
import { DiscountBooksComponent } from './components/discount/discount-books/discount-books.component';
import { DiscountComponent } from './components/discount/marketing/discount/discount.component';
import { DiscountAssociationComponent } from './components/discount/discount-association/discount-association.component';

const routes: Routes = [
  { path: '', redirectTo: 'shop', pathMatch: 'full' },
  { path: 'admin/users', canActivate: [RoleGuardService], component: UsersComponent },
  { path: 'admin/books', canActivate: [RoleGuardService], component: BooksComponent },
  { path: 'seller/books', canActivate: [RoleGuardService], component: BooksComponent },
  { path: 'shop', component: ShopbookComponent },
  { path: 'order', canActivate: [RoleGuardService], component: OrderComponent },
  { path: 'register', component: RegisterLoginComponent },
  { path: 'login', component: RegisterLoginComponent },
  { path: 'communication', canActivate: [RoleGuardService], component: CommunicationComponent },
  { path: 'inbox', canActivate: [RoleGuardService], component: InboxComponent },
  { path: 'inboxCancelled', canActivate: [RoleGuardService], component: InboxCancelledComponent },
  { path: 'discountBooks', canActivate: [RoleGuardService], component: DiscountBooksComponent },
  { path: 'marketing/discount', canActivate: [RoleGuardService], component: DiscountComponent },
  { path: 'marketing/discountRelative', canActivate: [RoleGuardService], component: DiscountAssociationComponent },
  { path: '**', component: ErrorComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
