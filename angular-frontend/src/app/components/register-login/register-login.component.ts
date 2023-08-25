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
  
form  : FormGroup;

  msg: string[];

  nameErr: String[];
  emailErr: string[];
  passwordErr: string[];

  isLogin: boolean = false;

  showPassword: boolean[] = [false, false];

   constructor(private formBuilder: FormBuilder, private router: Router, private auth: AuthService) { }

  ngOnInit() {

    this.isLogin = (this.router.url.indexOf("/login")>=0);

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


  toggleShowPassword(n) {
    this.showPassword[n] = !this.showPassword[n];
  }


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



  validatePwd(): ValidatorFn {

    return (control: AbstractControl): ValidationErrors | null => {
      let a = this.form.get("password");
      let b = this.form.get("passwordRepeat");
      let valid = (a.value == b.value);
      return valid ? null : { valid: false };
    };
  }


  escapeRegExp(string) {
    return string.replace(/[.*+?^${}()|[\]\\]/g, '\\$&'); // $& means the whole matched string
  }


  replaceAll(str, find, replace) {
    return str.replace(new RegExp(this.escapeRegExp(find), 'g'), replace);
  }



  action() {
    //debugger;

      let name = this.form.controls.name.value;
      let email = this.form.controls.email.value;
      let password = this.form.controls.password.value;

      //email = "pippo@gmail.com";
      //password = "11111111";


      if(this.isLogin) {

       

        this.auth.login(email, password).subscribe({
          next: (res) => {
            //this.router.navigate(["/shop"]);
            location.href="/shop";
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
            }, 3000); //  scompare dopo 3 secondi
  
  
          },
          complete: () => {
            this.form.reset();
          }
        })

      } else {

        //debugger;
     
        this.auth.register(name, email , password).subscribe({
          next: (res) => {
            this.router.navigate(["/login"]);
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
            }, 3000); //  scompare dopo 3 secondi
  
  
          },
          complete: () => {
            this.form.reset();
          }
        });

      }


    
    }




}