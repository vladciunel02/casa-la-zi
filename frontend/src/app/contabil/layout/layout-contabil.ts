import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { NgIcon, provideIcons } from '@ng-icons/core';
import {
  phosphorBuildings,
  phosphorCalendarCheck,
  phosphorFileXls,
} from '@ng-icons/phosphor-icons/regular';

@Component({
  imports: [RouterOutlet, RouterLink, RouterLinkActive, NgIcon],
  viewProviders: [provideIcons({ phosphorCalendarCheck, phosphorBuildings, phosphorFileXls })],
  selector: 'app-layout-contabil',
  styles: ``,
  templateUrl: './layout-contabil.html',
})
export class LayoutContabil {
  protected readonly items = [
    { label: 'Zile trimise', path: '/contabil/zile-trimise', icon: 'phosphorCalendarCheck' },
    { label: 'Firme', path: '/contabil/firme', icon: 'phosphorBuildings' },
  ];
}
