import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthService } from '../service/auth.service';

@Injectable({
  providedIn: 'root'
})
export class RoleGuardService implements CanActivate {

  constructor(private router: Router, private authService: AuthService) { }

  resources: any = {
    "/admin/users": ["Admin"],
    "/admin/books": ["Admin"],
    "/seller/books": ["Seller"],
    "/order": ["Admin", "User", "Seller", "Order", "Marketing"],
    "/communication": ["Admin", "User", "Seller", "Order", "Marketing"],
    "/inbox": ["Order"],
    "/marketing/discount":["Marketing"]
  }

  canActivate(next: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {

    let uRole = this.authService.role;
    let path = next.url.join('/');

    let a = path.indexOf("/");

    if (a >= 0) {
      path = path.substring(0, a);
    }

    path = "/" + path;

    let reqRole = this.resources[path];

    return (reqRole == null || reqRole.includes(uRole));

  }

}
