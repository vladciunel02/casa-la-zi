import { Component, model } from '@angular/core';
import { NgIcon, provideIcons } from '@ng-icons/core';
import { phosphorMagnifyingGlass } from '@ng-icons/phosphor-icons/regular';

@Component({
  imports: [NgIcon],
  viewProviders: [provideIcons({ phosphorMagnifyingGlass })],
  selector: 'app-cautare-firma-input',
  styles: ``,
  templateUrl: './cautare-firma-input.html',
})
export class CautareFirmaInput {
  /** The typed text, bound two-way by the page that owns it. */
  readonly termen = model('');
}
