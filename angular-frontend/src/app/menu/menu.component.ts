import { Component, HostListener, OnInit } from '@angular/core';
import { faAdd, faBook, faHome, faList, faMailBulk, faUnlockKeyhole, faUser, faUserAlt, faUserGroup, faVoicemail } from '@fortawesome/free-solid-svg-icons';
import { AuthService } from '../service/auth.service';
import { NavigationEnd, Router } from '@angular/router';
import { filter } from 'rxjs/operators';
import { HttpClientService } from '../service/http-client.service';

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.css']
})
export class MenuComponent implements OnInit {

  userName: string = '';

  iconaHome = faHome;
  iconaUsers = faUserGroup;
  iconaInsert = faAdd;
  iconaProfiles = faUser;
  iconaSecurity = faUnlockKeyhole;
  iconaBooks = faBook;
  iconaView = faList;
  iconaProfile = faUserAlt;
  iconaCommunication = faMailBulk;

  isLogin: boolean = true;
  showViewOrders: boolean = false;

  currentUrl: string;

  constructor(public authService: AuthService, private router: Router, private httpClienteService: HttpClientService) { }

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
    

    this.updateUserData();
    
  }


  private updateUserData() {
    const userId = this.authService.uid;

    this.httpClienteService.getUser(userId).subscribe(
      (userData) => {
        //debugger;
        this.userName = userData.name;
      },
      (error) => {
        console.error('Errore nel recuperare i dati utente:', error);
      }
    );
    
  }

  



}



