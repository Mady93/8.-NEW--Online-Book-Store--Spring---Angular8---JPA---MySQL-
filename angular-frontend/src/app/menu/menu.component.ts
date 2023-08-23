import { Component, OnInit } from '@angular/core';
import { faAdd, faBook, faHome, faList, faUnlockKeyhole, faUser, faUserAlt} from '@fortawesome/free-solid-svg-icons';
import { AuthService } from '../service/auth.service';
import { NavigationEnd, Router } from '@angular/router';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.css']
})
export class MenuComponent implements OnInit {

  iconaHome = faHome;
  iconaUsers = faUserAlt;
  iconaInsert = faAdd;
  iconaProfiles = faUser;
  iconaSecurity = faUnlockKeyhole;
  iconaBooks = faBook;
  iconaView = faList;

  isLogin: boolean = true;
  showViewOrders: boolean = false;

  constructor(public authService: AuthService, private router: Router) { }

  ngOnInit() {
  
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: NavigationEnd) => {
      if (event.urlAfterRedirects.includes('/register')) {
        this.isLogin = false;
        this.showViewOrders = false; // Nascondi il pulsante "View Orders" quando si è su /register
      } else {
        this.isLogin = true;
        if (event.urlAfterRedirects !== '/shop') {
          this.showViewOrders = true; // Mostra il pulsante "View Orders" solo quando non si è su /shop
        } else {
          this.showViewOrders = false;
        }
      }
    });
}

}
  


