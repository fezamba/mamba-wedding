import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RsvpService } from '../../services/rsvp.service';
import { RsvpResponse } from '../../models/rsvp.models';

import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { NgxMaskDirective } from 'ngx-mask';

@Component({
  selector: 'app-rsvp',
  standalone: true,
  imports: [
    CommonModule, 
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressBarModule,
    MatSnackBarModule,
    NgxMaskDirective
  ],
  templateUrl: './rsvp.html',
  styleUrl: './rsvp.css',
})
export class Rsvp {
  private fb = inject(FormBuilder);
  private rsvpService = inject(RsvpService);
  private snackBar = inject(MatSnackBar);

  guestData = signal<RsvpResponse | null>(null);
  isLoading = signal(false);
  
  step = signal<'search' | 'form' | 'success'>('search');

  lookupForm = this.fb.group({
    codigoConvite: ['', [Validators.required, Validators.minLength(3)]]
  });

  actionForm = this.fb.group({
    email: ['', [Validators.email, Validators.required, Validators.maxLength(120)]],
    telefone: ['', [Validators.required, Validators.pattern("^\\d{11}$")]],
    observacoes: ['']
  });

  resetSearch() {
    this.guestData.set(null);
    this.lookupForm.reset();
    this.actionForm.reset();
    this.step.set('search');
  }

  onLookup() {
    if (this.lookupForm.invalid) return;

    this.isLoading.set(true);
    const codigo = this.lookupForm.value.codigoConvite!;

    this.rsvpService.lookup(codigo).subscribe({
      next: (data) => {
        this.guestData.set(data);
        this.actionForm.patchValue({
          email: data.email,
          telefone: data.telefone,
          observacoes: data.observacoes
        });
        this.step.set('form');
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error(err);
        this.showMessage('Convite não encontrado. Verifique o código.', 'Erro');
        this.isLoading.set(false);
        this.guestData.set(null);
      }
    });
  }

  submitRsvp(isConfirm: boolean) {
    this.isLoading.set(true);
    const codigo = this.guestData()?.codigoConvite!;

    if (this.actionForm.invalid) {
      this.actionForm.markAllAsTouched();
      return;
    }

    const rawTelefone = this.actionForm.get('telefone')?.value;
    const digits = String(rawTelefone ?? '').replace(/\D/g, '');

    if (digits.length !== 11) {
      this.actionForm.get('telefone')?.setErrors({ length: true });
      return;
    }

  this.actionForm.patchValue({ telefone: digits }, { emitEvent: false });
    
    const payload = {
      codigoConvite: codigo,
      ...this.actionForm.getRawValue()
    };

    const onSuccess = () => {
        this.showMessage(
          isConfirm ? 'Presença confirmada com sucesso!' : 'Sua resposta foi registrada.', 
          'Sucesso'
        );
        this.isLoading.set(false);
        this.step.set('success');
    };

    const onError = () => {
      this.showMessage('Ocorreu um erro ao enviar. Tente novamente.', 'Erro');
      this.isLoading.set(false);
    }

    if (isConfirm) {
      this.rsvpService.confirm(payload as any).subscribe({ next: onSuccess, error: onError });
    } else {
      this.rsvpService.decline(payload as any).subscribe({ next: onSuccess, error: onError });
    }
  }

  private showMessage(message: string, action: string) {
    this.snackBar.open(message, action, {
      duration: 3000,
      horizontalPosition: 'center',
      verticalPosition: 'bottom'
    });
  }
}