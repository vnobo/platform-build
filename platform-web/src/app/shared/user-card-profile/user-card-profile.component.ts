import {Component, OnInit} from '@angular/core';
import {Authenticate} from '../../core/authenticate.service';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';

@Component({
  selector: 'app-user-card-profile',
  templateUrl: './user-card-profile.component.html',
  styleUrls: ['./user-card-profile.component.scss']
})
export class UserCardProfileComponent implements OnInit {

  account$: Observable<any>;

  constructor(private http: HttpClient) {

  }

  ngOnInit() {
    this.account$ = this.http.get(`/account/user/${Authenticate.getAuthName()}`);
  }

}
