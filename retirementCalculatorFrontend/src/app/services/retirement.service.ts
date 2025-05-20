import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RetirementService {

private apiUrl = 'http://localhost:8080/retirement-plans';

  constructor(private http: HttpClient) {}

  calculateRetirement(data: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/calculate`, data);
  }

}
