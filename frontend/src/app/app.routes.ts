import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'contabil' },
  {
    path: 'contabil',
    loadComponent: () =>
      import('./contabil/layout/layout-contabil').then((m) => m.LayoutContabil),
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'zile-trimise' },
      {
        path: 'zile-trimise',
        data: { cautareFirma: true },
        loadComponent: () =>
          import('./contabil/zile-trimise/zile-trimise').then((m) => m.ZileTrimise),
      },
      {
        path: 'firme',
        loadComponent: () => import('./contabil/firme/firme').then((m) => m.Firme),
      },
      {
        path: 'export',
        loadComponent: () => import('./contabil/export/export').then((m) => m.Export),
      },
      {
        path: 'setari',
        loadComponent: () => import('./contabil/setari/setari').then((m) => m.Setari),
      },
    ],
  },
  {
    path: 'casier',
    loadComponent: () => import('./casier/layout/layout-casier').then((m) => m.LayoutCasier),
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'acasa' },
      {
        path: 'acasa',
        loadComponent: () => import('./casier/acasa/acasa').then((m) => m.Acasa),
      },
      {
        path: 'poze-si-verificare',
        loadComponent: () =>
          import('./casier/poze-si-verificare/poze-si-verificare').then(
            (m) => m.PozeSiVerificare,
          ),
      },
      {
        path: 'confirmare',
        loadComponent: () =>
          import('./casier/confirmare/confirmare').then((m) => m.Confirmare),
      },
    ],
  },
  {
    path: '**',
    loadComponent: () =>
      import('./pagina-negasita/pagina-negasita').then((m) => m.PaginaNegasita),
  },
];
