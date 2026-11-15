import { Service, signal } from '@angular/core';

@Service()
export class CautareFirma {
  readonly termen = signal('');
}
