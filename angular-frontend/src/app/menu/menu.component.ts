import { Component, HostListener, OnInit } from '@angular/core';
import { faAdd, faAdjust, faBalanceScaleRight, faBook, faDatabase, faDeleteLeft, faDiagnoses, faDirections, faDollar, faDriversLicense, faHand, faHandBackFist, faHandDots, faHandFist, faHandHolding, faHandHoldingHand, faHandHoldingWater, faHandMiddleFinger, faHome, faInbox, faList, faMagicWandSparkles, faMailBulk, faMarker, faPercentage, faRemove, faStarOfLife, faUnlockKeyhole, faUser, faUserAlt, faUserGroup, faVoicemail, faWandMagic } from '@fortawesome/free-solid-svg-icons';
import { AuthService } from '../service/auth.service';
import { NavigationEnd, Router } from '@angular/router';
import { filter } from 'rxjs/operators';
import { HttpClientService } from '../service/http-client.service';

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.scss']
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
  iconaInbox = faInbox;
  iconaStateWorking = faHandHoldingHand;
  iconaStateDeleted = faRemove;
  iconaDiscount = faPercentage;
  iconaMarketing = faBalanceScaleRight;
  iconaAssociate = faHandHoldingWater;

  // isLogin e showViewOrders vengono impostate per controllare la visibilità delle voci del menu in base alla situazione (accesso o registrazione e posizione dell'URL).
  isLogin: boolean = true;
  showViewOrders: boolean = false;

  currentUrl: string;

  constructor(public authService: AuthService, private router: Router, private httpClienteService: HttpClientService) { }

  // ngOnInit(): Questo metodo viene chiamato durante l'inizializzazione del componente. Si abbona agli eventi di navigazione (NavigationEnd) per monitorare la modifica dell'URL e determinare se l'utente è in fase di registrazione o accesso, nonché se si trova sulla pagina /shop.
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

    // update(): Questo metodo è chiamato quando l'utente accede o effettua il logout. Aggiorna i dati dell'utente attualmente loggato utilizzando l'ID utente dal servizio AuthService.
    /*faccio un subscribe alla variabile uid, che si accorge di un cambio a caldo dovuto ad un login*/
    this.authService.update().subscribe({
      next: () => {
        this.updateUserData(this.authService.uid);
      }
    });

    /*prendo le info dell'utente loggato basandomi sull'id dell'utente che ho in questo momento*/
    this.updateUserData(this.authService.uid);
  }

// updateUserData(uid: number): Questo metodo richiede i dati dell'utente tramite il servizio HttpClientService utilizzando l'ID utente fornito. Aggiorna il nome utente nel menu in base ai dati recuperati.
  private updateUserData(uid: number) {

    if (!uid) return;

    this.httpClienteService.getUser(uid).subscribe(
      (userData) => {
        
        this.userName = userData.name;
      },
      (error) => {
        console.error('Errore nel recuperare i dati utente:', error);
      }
    );
  }


}



