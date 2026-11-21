import { Component, signal } from '@angular/core';
import { CautareFirmaInput } from '../cautare-firma-input/cautare-firma-input';

@Component({
  imports: [CautareFirmaInput],
  selector: 'app-firme',
  styles: ``,
  templateUrl: './firme.html',
})
export class Firme {
  protected readonly termen = signal('');
}
