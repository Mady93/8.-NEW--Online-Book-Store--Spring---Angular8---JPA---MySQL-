import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthService } from '../service/auth.service';

@Injectable({
  providedIn: 'root'
})
export class RoleGuardService implements CanActivate {

  constructor(private router: Router,private authService: AuthService) { }


  /*
   { path: 'admin/users', canActivate: [RoleGuardService], component: UsersComponent },
  { path: 'admin/books', canActivate: [RoleGuardService], component: BooksComponent },
  { path: 'seller/books', canActivate: [RoleGuardService], component: BooksComponent },
  { path: 'shop', component: ShopbookComponent },
  { path: 'order', canActivate: [RoleGuardService], component: OrderComponent },
  { path: 'register', component: RegisterLoginComponent },
  { path: 'login', component: RegisterLoginComponent },
  { path: 'communication', component: CommunicationComponent },
  { path: 'inbox', component: InboxComponent },
 
  */


  resources: any = {
    "/admin/users": ["Admin"],
    "/admin/books": ["Admin"],
    "/seller/books": ["Seller"],
    "/order": ["Admin", "User", "Seller","Order"],
    "/communication": ["Admin", "User", "Seller","Order"],
    "/inbox": ["Order"],
  }

  canActivate(next: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {

    //debugger;
    let uRole = this.authService.role;
    let path = next.url.join('/');

    //path = "/"+((path.indexOf("/") >= 0)?path.substring(0,path.indexOf("/")):path);
    
    let a = path.indexOf("/");
    if (a>=0)
    {
      path = path.substring(0,a);
    }
    path = "/"+path;

    let reqRole = this.resources[path];




    return (reqRole == null || reqRole.includes(uRole));

  }




}
