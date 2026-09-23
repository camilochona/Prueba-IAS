import { Component, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { TransferService } from './service/transfer.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [FormsModule, CommonModule],
  template: `
    <div class="container mt-5">
      <h2 class="mb-4">BancolAS - Transferencias</h2>
      
      <div class="row">
        <div class="col-md-5">
          <div class="card p-4 shadow-sm">
            <h4>Nueva Transferencia</h4>
            <form (ngSubmit)="onSubmit()">
              <div class="mb-3">
                <label>Referencia</label>
                <input type="text" class="form-control" name="ref" [(ngModel)]="request.requestReference" required>
              </div>
              <div class="mb-3">
                <label>Cuenta Origen</label>
                <input type="text" class="form-control" name="src" [(ngModel)]="request.sourceAccountId" required>
              </div>
              <div class="mb-3">
                <label>Cuenta Destino</label>
                <input type="text" class="form-control" name="dest" [(ngModel)]="request.destinationAccountId" required>
              </div>
              <div class="mb-3">
                <label>Monto (COP)</label>
                <input type="number" class="form-control" name="amount" [(ngModel)]="request.amount" required>
              </div>
              <button type="submit" class="btn btn-primary w-100">Procesar Transferencia</button>
            </form>

            <div *ngIf="lastResult" class="alert mt-3" 
                 [ngClass]="lastResult.status === 'AUTORIZADA' ? 'alert-success' : 'alert-danger'">
              <strong>{{ lastResult.status }}</strong>: {{ lastResult.reason }}
            </div>
          </div>
        </div>

        <div class="col-md-7">
          <h4>Transferencias Recientes</h4>
          <table class="table table-striped border">
            <thead>
              <tr>
                <th>Referencia</th>
                <th>Origen</th>
                <th>Destino</th>
                <th>Monto</th>
                <th>Estado</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let t of transfers">
                <td>{{ t.requestReference }}</td>
                <td>{{ t.sourceAccountId }}</td>
                <td>{{ t.destinationAccountId }}</td>
                <td>{{ t.amount | currency:'COP':'symbol':'1.0-0' }}</td>
                <td>
                  <span class="badge" [ngClass]="t.status === 'AUTORIZADA' ? 'bg-success' : 'bg-danger'">
                    {{ t.status }}
                  </span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  `
})
export class AppComponent implements OnInit {
  request = { requestReference: '', sourceAccountId: 'CTA-1001', destinationAccountId: 'CTA-2001', amount: 0 };
  lastResult: any = null;
  transfers: any[] = [];

  constructor(private transferService: TransferService) {}

  ngOnInit() {
    this.loadTransfers();
  }

  loadTransfers() {
    this.transferService.getRecentTransfers().subscribe(data => this.transfers = data);
  }

  onSubmit() {
    this.transferService.createTransfer(this.request).subscribe(res => {
      this.lastResult = res;
      this.loadTransfers();
      // Limpiar monto y cambiar referencia para la siguiente prueba
      this.request.amount = 0;
      //this.request.requestReference = 'REF-' + Math.floor(Math.random() * 1000);
    });
  }
}