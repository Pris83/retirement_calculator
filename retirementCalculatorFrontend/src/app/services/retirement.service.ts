import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { catchError } from 'rxjs/operators';
import { throwError, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RetirementService {

private apiUrl = 'http://localhost:8080/retirement-plans';

  constructor(private http: HttpClient) {}

  calculateRetirement(data: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/calculate`, data).pipe(
      catchError(this.handleError));
  }

 private handleError(error: HttpErrorResponse) {
    let errorMsg = 'An unknown error occurred!';
    if (error.error && error.error.message) {
      errorMsg = `${error.error.message}`;
    }
    return throwError(() => new Error(errorMsg));
  }
}
