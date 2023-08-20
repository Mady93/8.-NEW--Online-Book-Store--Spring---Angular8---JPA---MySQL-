import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthService } from '../service/auth.service';

@Injectable({
  providedIn: 'root'
})
export class RoleGuardService implements CanActivate {

  constructor(private router: Router,private authService: AuthService) { }

  resources: any = {
    "/admin/users": ["Admin"],
    "/admin/books": ["Admin"],
    "/order": ["Admin", "User", "Seller"]
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
