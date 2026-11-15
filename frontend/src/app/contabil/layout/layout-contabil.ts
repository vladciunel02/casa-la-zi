import { Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import {
  ActivatedRoute,
  NavigationEnd,
  Router,
  RouterLink,
  RouterLinkActive,
  RouterOutlet,
} from '@angular/router';
import { NgIcon, provideIcons } from '@ng-icons/core';
import {
  phosphorBuildings,
  phosphorCalendarCheck,
  phosphorCaretDown,
  phosphorFileXls,
  phosphorGear,
  phosphorMagnifyingGlass,
} from '@ng-icons/phosphor-icons/regular';
import { filter, map } from 'rxjs';
import { CautareFirma } from '../cautare-firma';

@Component({
  imports: [RouterOutlet, RouterLink, RouterLinkActive, NgIcon],
  viewProviders: [
    provideIcons({
      phosphorCalendarCheck,
      phosphorBuildings,
      phosphorFileXls,
      phosphorGear,
      phosphorMagnifyingGlass,
      phosphorCaretDown,
    }),
  ],
  selector: 'app-layout-contabil',
  styles: ``,
  templateUrl: './layout-contabil.html',
})
export class LayoutContabil {
  protected readonly cautare = inject(CautareFirma);

  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly areCautare = () =>
    this.route.snapshot.firstChild?.data['cautareFirma'] === true;

  /** True only when the active child route has data { cautareFirma: true }. */
  protected readonly arataCautarea = toSignal(
    this.router.events.pipe(
      filter((e) => e instanceof NavigationEnd),
      map(() => this.areCautare()),
    ),
    { initialValue: this.areCautare() },
  );

  protected readonly items = [
    { label: 'Zile trimise', path: '/contabil/zile-trimise', icon: 'phosphorCalendarCheck' },
    { label: 'Firme', path: '/contabil/firme', icon: 'phosphorBuildings' },
    { label: 'Export', path: '/contabil/export', icon: 'phosphorFileXls' },
    { label: 'Setări', path: '/contabil/setari', icon: 'phosphorGear' },
  ];
}
