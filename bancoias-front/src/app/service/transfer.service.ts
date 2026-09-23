import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class TransferService {
  private apiUrl = 'http://localhost:8080/api/transfers';

  constructor(private http: HttpClient) {}

  createTransfer(transfer: any): Observable<any> {
    return this.http.post(this.apiUrl, transfer);
  }

  getRecentTransfers(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }
}