import { Component, OnInit, Input, EventEmitter, Output } from '@angular/core';
import { Book } from '../../../model/Book';
import { HttpClientService } from '../../../service/http-client.service';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { of } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { AuthService } from 'src/app/service/auth.service';


@Component({
  selector: 'app-addbook',
  templateUrl: './addbook.component.html',
  styleUrls: ['./addbook.component.scss']
})
export class AddbookComponent implements OnInit {

  @Input()
  book: Book;

  @Output()
  bookAddedEvent = new EventEmitter();
  private selectedFile;
  bColor: any = { r: 128, g: 128, b: 128 };

  msg: any;
  ok: any;


  constructor(private httpClientService: HttpClientService,
    private activedRoute: ActivatedRoute,
    private router: Router,
    private httpClient: HttpClient,
    private auth: AuthService) { }

    // ngOnInit(): Questo metodo viene chiamato quando il componente viene inizializzato. Controlla se è presente un parametro "action" nell'URL e, se è "edit", imposta il colore di un pulsante in base all'immagine del libro.
  ngOnInit() {

    let act = this.activedRoute.snapshot.queryParams["action"];

    if (act == "edit") {

      this.setButtonColor("data:image/jpeg;base64," + this.book.picByte);
    }

  }


// setButtonColor(url): Questo metodo ottiene i dati del colore dell'immagine e imposta un colore di sfondo per un pulsante in base a tali dati.
  private setButtonColor(url) {

    this.getImageColor(url, (r, g, b) => {

      let t = { r: r, g: g, b: b };

      let isBWG = (r - g + r - b) < 20;

      if (isBWG) {

        if (r < 50) {
          t.r = 200;
          t.b = 200;
        } else if (r > 150) {
          t.r = 0xcc;
          t.g = 0x00;
          t.b = 0xff;
        } else {
          t.r = 0x33;
          t.g = 0x99;
          t.b = 0xff;
        }

      } else {
        t.r = 255 - t.r;
        t.g = 255 - t.g;
        t.b = 255 - t.b;
      }

      this.bColor = { r: t.r, g: t.g, b: t.b };

    });

  }

  // onFileChanged(event): Questo metodo viene chiamato quando un file immagine viene selezionato. Legge il file immagine, calcola il colore del pulsante basato sull'immagine e converte l'immagine in una stringa di byte per il salvataggio.
  public onFileChanged(event) {
    console.log(event);
    this.selectedFile = event.target.files[0];

    let reader = new FileReader();
    reader.readAsDataURL(event.target.files[0]);
    reader.onload = (event2) => {
      this.setButtonColor(reader.result);
      this.book.picByte = reader.result.toString().substring(23);
    };

  }


  // Aggiunto regex errori
  escapeRegExp(string) {
    return string.replace(/[.*+?^${}()|[\]\\]/g, '\\$&'); // $& means the whole matched string
  }

  // Aggiunto regex errori
  replaceAll(str, find, replace) {
    return str.replace(new RegExp(this.escapeRegExp(find), 'g'), replace);
  }


  /* saveBook(): Questo metodo salva un libro. Gestisce l'invio dell'immagine del libro (se presente) e gestisce l'aggiunta o l'aggiornamento del libro chiamando i metodi del servizio HttpClientService.
   In base al ruolo dell'utente autenticato (auth.role), può eseguire operazioni diverse. Inoltre, gestisce le risposte e gli errori da parte del server.*/
  saveBook() {

    let isImgUpload = (this.selectedFile != undefined);
    let imgUploadObs = of({});

    //const uploadData = new FormData();
    if (isImgUpload) {
      //uploadData.append('imageFile', this.selectedFile, this.selectedFile.name);
      this.selectedFile.imageName = this.selectedFile.name;

      imgUploadObs = this.httpClientService.uploadImageBook(this.book, this.selectedFile);
    }

    let fn = null;

    if (this.book.id == null) {
      fn = this.httpClientService.addBook.bind(this.httpClientService);
    } else {

      if (this.auth.role == "Seller") {
        //faccio la chiamata che aggiorna solo il prezzo e inibisco il caricamento di immagini
        fn = this.httpClientService.updateBookJustPrice.bind(this.httpClientService);
        imgUploadObs = of({});
      } else {
        fn = this.httpClientService.updateBook.bind(this.httpClientService);
      }

    }

    imgUploadObs.pipe(
      switchMap(() => fn(this.book, this.auth.role.toLowerCase()))
    ).subscribe(
      (res) => {
        // this.ok = res.message;
        this.msg = "";

        /* setTimeout(() => {
           this.ok = '';
         }, 2000);*/

        this.bookAddedEvent.emit();
        this.router.navigate([this.auth.role.toLowerCase(), 'books']);
      },
      (err: HttpErrorResponse) => {

        this.msg = this.replaceAll(err.message, "#", "<br>");

        setTimeout(() => {
          this.msg = '';
        }, 2000);

      }
    );

  }


  // getImageColor(src: string, cb: any): Questo metodo ottiene il colore medio dell'immagine utilizzando il canvas HTML5.
  getImageColor(src: string, cb: any) {
    var image = new Image();
    image.src = src;

    image.onload = function () {
      var canvas = document.createElement('canvas');
      canvas.width = image.width;
      canvas.height = image.height;

      var context = canvas.getContext('2d');
      context.drawImage(image, 0, 0);

      var imageData = context.getImageData(0, 0, canvas.width, canvas.height);

      let x = canvas.width / 2;
      let y = canvas.height / 2;

      x -= 10;

      var index = (y * imageData.width + x) * 4;

      let data = { r: 0, g: 0, b: 0 };

      for (let c = 0; c < 20; c++) {
        data.r += imageData.data[index];
        data.g += imageData.data[index + 1];
        data.b += imageData.data[index + 2];
      }

      data.r = Math.round(data.r / 20);
      data.g = Math.round(data.g / 20);
      data.b = Math.round(data.b / 20);

      cb(data.r, data.g, data.b);
    };
  }

  // closeFunction(): Questo metodo chiude Book Details = x
  closeFunction() {
    this.router.navigate([this.auth.role.toLowerCase(), 'books']);
  }

}