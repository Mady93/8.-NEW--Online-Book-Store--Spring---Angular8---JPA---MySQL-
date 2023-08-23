import { Component, OnInit, Input, EventEmitter, Output } from '@angular/core';
import { Book } from '../../../model/Book';
import { HttpClientService } from '../../../service/http-client.service';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { concatMap, switchMap } from 'rxjs/operators';
import { AuthService } from 'src/app/service/auth.service';


@Component({
  selector: 'app-addbook',
  templateUrl: './addbook.component.html',
  styleUrls: ['./addbook.component.css']
})
export class AddbookComponent implements OnInit {

  @Input()
  book: Book;

  @Output()
  bookAddedEvent = new EventEmitter();
  private selectedFile;
  imgURL: any;

  msg: any;


  constructor(private httpClientService: HttpClientService,
    private activedRoute: ActivatedRoute,
    private router: Router,
    private httpClient: HttpClient,
    private auth: AuthService) { }

  ngOnInit() {

    let act = this.activedRoute.snapshot.queryParams["action"];

    if (act == "edit") {

      this.setImage(this.book.retrievedImage);

    }

  }

  private setImage(url) {

    document.querySelector("form").style.backgroundImage = "url('" + url + "')";
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

      (<HTMLElement>document.querySelector(".btn-success")).style.backgroundColor = "rgba(" + t.r + "," + t.g + "," + t.b + "," + 255 + ")";

    });

  }

  public onFileChanged(event) {
    console.log(event);
    this.selectedFile = event.target.files[0];

    let reader = new FileReader();
    reader.readAsDataURL(event.target.files[0]);
    reader.onload = (event2) => {
      this.imgURL = reader.result;
      this.setImage(this.imgURL);
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



  saveBook() {

    if (this.book.id == null) {
      const uploadData = new FormData();

      if (this.selectedFile){
        uploadData.append('imageFile', this.selectedFile, this.selectedFile.name);
        this.selectedFile.imageName = this.selectedFile.name;
      }
      

      //debugger;

      this.httpClient.post('http://localhost:8080/books/upload', uploadData, { observe: 'response' }).subscribe({
        next: () => {
          this.httpClientService.addBook(this.book).subscribe({
            next: () => {
              this.msg = "";
              this.bookAddedEvent.emit();
              this.router.navigate(['admin', 'books']);
            },
            error: (err: HttpErrorResponse) => {
              this.msg = this.replaceAll(err.message, "#", "<br>");
            },
            complete: () => {

            }
          });
          console.log('Image uploaded successfully');
        },
        error: (err: HttpErrorResponse) => {
          console.log('Image not uploaded successfully');
          this.msg = this.replaceAll(err.message, "#", "<br>");
        }
      })

    } else {



      let isImgUpload = (this.selectedFile != undefined);

      //debugger;

      const uploadData = new FormData();
      if (isImgUpload) {
        uploadData.append('imageFile', this.selectedFile, this.selectedFile.name);
        this.selectedFile.imageName = this.selectedFile.name;
      }

      
      let imgUploadObs = isImgUpload ? this.httpClient.post('http://localhost:8080/books/upload', uploadData, { observe: 'response' }) : of({});


      imgUploadObs.pipe(
        switchMap(() => this.httpClientService.updateBook(this.book, this.auth.role.toLowerCase()))
      ).subscribe(
        (res) => {
          this.msg = "";
          this.bookAddedEvent.emit();
          this.router.navigate([this.auth.role.toLowerCase(), 'books']);
        },
        (err) => {
          this.msg = this.replaceAll(err.message, "#", "<br>");
        }
      );
    }

  }

  getImageColor(src: string, cb: any) {
    var image = new Image();
    image.src = src;

    //debugger;
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

  closeFunction() {
    this.router.navigate(['admin', 'books']);
  }


}