import { Component, OnInit } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { AbstractControl, FormBuilder, FormControl, FormGroup, ValidationErrors, Validators, ValidatorFn } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from 'src/app/service/auth.service';

@Component({
  selector: 'app-register-login',
  templateUrl: './register-login.component.html',
  styleUrls: ['./register-login.component.scss']
})
export class RegisterLoginComponent implements OnInit {

  form: FormGroup;

  msg: string[];
  ok: any;

  nameErr: String[];
  emailErr: string[];
  passwordErr: string[];

  isLogin: boolean = false;

  showPassword: boolean[] = [false, false];

  constructor(private formBuilder: FormBuilder, private router: Router, private auth: AuthService) { }

  // ngOnInit(): Questo metodo viene chiamato durante l'inizializzazione del componente. Si occupa di inizializzare il formulario e le sue validazioni in base al tipo di operazione (registrazione o accesso).
  ngOnInit() {

    this.isLogin = (this.router.url.indexOf("/login") >= 0);

    this.form = this.formBuilder.group({
      name: [
        '',
        [

        ],
      ],
      email: [
        '',
        [
          Validators.email

        ],
      ],
      password: [
        '',
        [

        ]
      ],
      repeatPassword: ['']
    });




    if (!this.isLogin) {
      this.form.get('repeatPassword').setValidators([
        Validators.required,
        this.validatePasswordConfirmation.bind(this)
      ]);
    }

  }

// toggleShowPassword(n): Questo metodo permette di mostrare o nascondere la password nell'input corrispondente, a seconda dell'indice n passato come parametro.
  toggleShowPassword(n) {
    this.showPassword[n] = !this.showPassword[n];
  }


  // validatePasswordConfirmation(control: FormControl): Questo metodo valida se la password inserita e la sua conferma coincidono.
  private validatePasswordConfirmation(control: FormControl): ValidationErrors | null {
    const password = this.form.get('password').value;
    const repeatPassword = control.value;

    if (password !== repeatPassword) {
      return { passwordMismatch: true };
    }

    return null;
  }


  get name(): FormControl {
    return <FormControl>this.form.controls['name'];
  }

  get email(): FormControl {
    return <FormControl>this.form.controls['email'];
  }

  get password(): FormControl {
    return <FormControl>this.form.controls['password'];
  }

  get repeatPassword(): FormControl {
    return <FormControl>this.form.controls['repeatPassword'];
  }

  // validatePwd(): Questo metodo è un validatore personalizzato che verifica che due campi password abbiano lo stesso valore.
  validatePwd(): ValidatorFn {

    return (control: AbstractControl): ValidationErrors | null => {
      let a = this.form.get("password");
      let b = this.form.get("passwordRepeat");
      let valid = (a.value == b.value);
      return valid ? null : { valid: false };
    };
  }

// escapeRegExp(string): Questo metodo esegue l'escape dei caratteri speciali in una stringa, utilizzando una regex.
  escapeRegExp(string) {
    return string.replace(/[.*+?^${}()|[\]\\]/g, '\\$&'); // $& means the whole matched string
  }

// replaceAll(str, find, replace): Questo metodo sostituisce tutte le occorrenze di una sottostringa con un'altra nella stringa di input.
  replaceAll(str, find, replace) {
    return str.replace(new RegExp(this.escapeRegExp(find), 'g'), replace);
  }


// action(): Questo metodo è chiamato quando l'utente esegue un'azione (registrazione o accesso). Richiama i metodi appropriati del servizio AuthService per l'accesso o la registrazione e gestisce le risposte e gli errori restituiti dal servizio.
// Nel caso dell'accesso, gestisce la visualizzazione dei messaggi di errore per l'email e la password, se presenti.
// Nel caso della registrazione, gestisce la visualizzazione dei messaggi di errore per il nome, l'email e la password, se presenti.
  action() {

    let name = this.form.controls.name.value;
    let email = this.form.controls.email.value;
    let password = this.form.controls.password.value;

    if (this.isLogin) {

      this.auth.login(email, password).subscribe({
        next: (res: any) => {

          this.ok = res.body.message;

          setTimeout(() => {
            this.ok = '';
            this.router.navigate(["/shop"]);
          }, 2000);

        },
        error: (err: HttpErrorResponse) => {

          this.msg = this.replaceAll(err.message, "#", "<br>");

          let t = err.message.split("#");
          this.emailErr = t.filter(x => { return x.startsWith("Email") });
          this.passwordErr = t.filter(x => { return x.startsWith("Password") });


          this.msg = t.filter(x => !(x.startsWith("Email") || x.startsWith("Password"))).map(x => {
            const [timestamp, status, message] = x.split(",");
            return `Timestamp: ${timestamp}, Status: ${status}, Message: ${message.trim()}`;
          });


          setTimeout(() => {
            this.emailErr = [];
            this.passwordErr = [];
          }, 2000);

        },
        complete: () => {
          this.form.reset();
        }
      })

    } else {

      if (!this.form.valid) return;
      this.auth.register(name, email, password).subscribe({

        next: (res: any) => {

          this.ok = res.message;

          setTimeout(() => {
            this.ok = '';
            this.router.navigate(["/login"]);
          }, 2000);

        },
        error: (err: HttpErrorResponse) => {

          this.msg = this.replaceAll(err.message, "#", "<br>");

          let t = err.message.split("#");
          this.nameErr = t.filter(x => { return x.startsWith("Name") });
          this.emailErr = t.filter(x => { return x.startsWith("Email") });
          this.passwordErr = t.filter(x => { return x.startsWith("Password") });


          this.msg = t.filter(x => !(x.startsWith("Name") || x.startsWith("Email") || x.startsWith("Password"))).map(x => {
            const [timestamp, status, message] = x.split(",");
            return `Timestamp: ${timestamp}, Status: ${status}, Message: ${message.trim()}`;
          });

          setTimeout(() => {
            this.nameErr = [];
            this.emailErr = [];
            this.passwordErr = [];
          }, 2000);

        },
        complete: () => {
          this.form.reset();
        }
      });
    }
  }

}





