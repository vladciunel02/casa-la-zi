import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { NgIcon, provideIcons } from '@ng-icons/core';
import { phosphorCaretLeft } from '@ng-icons/phosphor-icons/regular';

@Component({
  imports: [RouterLink, NgIcon],
  viewProviders: [provideIcons({ phosphorCaretLeft })],
  selector: 'app-poze-si-verificare',
  styles: ``,
  templateUrl: './poze-si-verificare.html',
})
export class PozeSiVerificare {}
