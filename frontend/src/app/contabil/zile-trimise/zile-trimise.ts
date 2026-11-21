import { Component, signal } from '@angular/core';
import { CautareFirmaInput } from '../cautare-firma-input/cautare-firma-input';

@Component({
  imports: [CautareFirmaInput],
  selector: 'app-zile-trimise',
  styles: ``,
  templateUrl: './zile-trimise.html',
})
export class ZileTrimise {
  protected readonly termen = signal('');
}
