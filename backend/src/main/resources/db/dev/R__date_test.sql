-- Date de test pentru dezvoltare. Ruleaza doar cu profilul "dev"


INSERT INTO cabinet (id, denumire)
VALUES ('11111111-1111-1111-1111-111111111111', 'Cabinet Contabil Popescu'),
       ('22222222-2222-2222-2222-222222222222', 'Expert Conta Oltenia')
ON CONFLICT (id) DO NOTHING;

-- Firmele ----------------------------------------------------------------

INSERT INTO firma (id, cabinet_id, denumire, cui, nr_reg_com, adresa, platitor_tva, activ)
VALUES
    -- Cabinetul A: cele 8 firme din design, toate active și plătitoare de TVA
    ('f0000000-0000-0000-0000-000000000001', '11111111-1111-1111-1111-111111111111',
     'Alimentara Florea SRL', '18452300', 'J16/412/2016', 'Str. Gării 7, Filiași, Dolj', true, true),
    ('f0000000-0000-0000-0000-000000000002', '11111111-1111-1111-1111-111111111111',
     'Brutăria Stan SRL', '30117720', 'J16/842/2018', 'Str. Morii 4, Leu, Dolj', true, true),
    ('f0000000-0000-0000-0000-000000000003', '11111111-1111-1111-1111-111111111111',
     'Bufet La Răscruce SRL', '29004418', 'J16/619/2017', 'DN6 km 212, Segarcea, Dolj', true, true),
    ('f0000000-0000-0000-0000-000000000004', '11111111-1111-1111-1111-111111111111',
     'Magazin Mixt Ionescu SRL', '15632285', 'J16/205/2014', 'Str. Bisericii 2, Bechet, Dolj', true, true),
    ('f0000000-0000-0000-0000-000000000005', '11111111-1111-1111-1111-111111111111',
     'Agro Dealu Mare SRL', '34772153', 'J16/1108/2020', 'Sat Dealu Mare 31, Dolj', true, true),
    ('f0000000-0000-0000-0000-000000000006', '11111111-1111-1111-1111-111111111111',
     'Moara Veche SRL', '22198643', 'J16/530/2015', 'Str. Jiului 18, Podari, Dolj', true, true),
    ('f0000000-0000-0000-0000-000000000007', '11111111-1111-1111-1111-111111111111',
     'Croitoria Matei SRL', '40125569', 'J16/914/2021', 'Str. Libertății 9, Calafat, Dolj', true, true),
    ('f0000000-0000-0000-0000-000000000008', '11111111-1111-1111-1111-111111111111',
     'Farmacia Veterinară Lunca SRL', '36981008', 'J16/773/2019', 'Str. Lunca 3, Sadova, Dolj', true, true),

    -- Cabinetul A: o firmă arhivată (inventată), pentru testele cu firme inactive
    ('f0000000-0000-0000-0000-000000000009', '11111111-1111-1111-1111-111111111111',
     'Atelier Auto Cârcea SRL', '27115304', 'J16/377/2010', 'Str. Fabricii 5, Cârcea, Dolj', false, false),

    -- Cabinetul B: aceeași Alimentara Florea, cu același CUI (cazul firmei care schimbă cabinetul)
    ('f0000000-0000-0000-0000-000000000101', '22222222-2222-2222-2222-222222222222',
     'Alimentara Florea SRL', '18452300', 'J16/412/2016', 'Str. Gării 7, Filiași, Dolj', true, true)
ON CONFLICT (id) DO NOTHING;

-- Contabilii -------------------------------------------------------------

INSERT INTO utilizator (id, email, parola_hash, nume, rol, cabinet_id, firma_id, activ)
VALUES
    -- contabilul "curent" până avem login (casalazi.dev.utilizator-curent-id)
    ('a0000000-0000-0000-0000-000000000001', 'ana.popescu@example.com',
     '$2b$10$LbcJD23UgPc9/rvTR142e.RV4JP3oyXtLibwR8gTSg/z82w2n601O', 'Ana Popescu', 'CONTABIL',
     '11111111-1111-1111-1111-111111111111', NULL, true),
    -- colegul ei din același cabinet: vede aceleași firme
    ('a0000000-0000-0000-0000-000000000002', 'mihai.dumitrescu@example.com',
     '$2b$10$LbcJD23UgPc9/rvTR142e.RV4JP3oyXtLibwR8gTSg/z82w2n601O', 'Mihai Dumitrescu', 'CONTABIL',
     '11111111-1111-1111-1111-111111111111', NULL, true),
    -- contabilul din cabinetul B: nu trebuie să vadă firmele din A
    ('a0000000-0000-0000-0000-000000000003', 'ioana.stoica@example.com',
     '$2b$10$LbcJD23UgPc9/rvTR142e.RV4JP3oyXtLibwR8gTSg/z82w2n601O', 'Ioana Stoica', 'CONTABIL',
     '22222222-2222-2222-2222-222222222222', NULL, true)
ON CONFLICT (id) DO NOTHING;

-- Casierii ---------------------------------------------------------------

INSERT INTO utilizator (id, email, parola_hash, nume, rol, cabinet_id, firma_id, activ)
VALUES
    -- câte un casier activ la fiecare firmă din design
    ('c0000000-0000-0000-0000-000000000001', 'elena.florea@example.com',
     '$2b$10$LbcJD23UgPc9/rvTR142e.RV4JP3oyXtLibwR8gTSg/z82w2n601O', 'Elena Florea', 'CASIER',
     NULL, 'f0000000-0000-0000-0000-000000000001', true),
    ('c0000000-0000-0000-0000-000000000002', 'ion.stan@example.com',
     '$2b$10$LbcJD23UgPc9/rvTR142e.RV4JP3oyXtLibwR8gTSg/z82w2n601O', 'Ion Stan', 'CASIER',
     NULL, 'f0000000-0000-0000-0000-000000000002', true),
    ('c0000000-0000-0000-0000-000000000003', 'ana.marin@example.com',
     '$2b$10$LbcJD23UgPc9/rvTR142e.RV4JP3oyXtLibwR8gTSg/z82w2n601O', 'Ana Marin', 'CASIER',
     NULL, 'f0000000-0000-0000-0000-000000000003', true),
    ('c0000000-0000-0000-0000-000000000004', 'maria.ionescu@example.com',
     '$2b$10$LbcJD23UgPc9/rvTR142e.RV4JP3oyXtLibwR8gTSg/z82w2n601O', 'Maria Ionescu', 'CASIER',
     NULL, 'f0000000-0000-0000-0000-000000000004', true),
    ('c0000000-0000-0000-0000-000000000005', 'gheorghe.radu@example.com',
     '$2b$10$LbcJD23UgPc9/rvTR142e.RV4JP3oyXtLibwR8gTSg/z82w2n601O', 'Gheorghe Radu', 'CASIER',
     NULL, 'f0000000-0000-0000-0000-000000000005', true),
    ('c0000000-0000-0000-0000-000000000006', 'vasile.dinu@example.com',
     '$2b$10$LbcJD23UgPc9/rvTR142e.RV4JP3oyXtLibwR8gTSg/z82w2n601O', 'Vasile Dinu', 'CASIER',
     NULL, 'f0000000-0000-0000-0000-000000000006', true),
    ('c0000000-0000-0000-0000-000000000007', 'irina.matei@example.com',
     '$2b$10$LbcJD23UgPc9/rvTR142e.RV4JP3oyXtLibwR8gTSg/z82w2n601O', 'Irina Matei', 'CASIER',
     NULL, 'f0000000-0000-0000-0000-000000000007', true),
    ('c0000000-0000-0000-0000-000000000008', 'florin.lungu@example.com',
     '$2b$10$LbcJD23UgPc9/rvTR142e.RV4JP3oyXtLibwR8gTSg/z82w2n601O', 'Florin Lungu', 'CASIER',
     NULL, 'f0000000-0000-0000-0000-000000000008', true),

    -- un casier dezactivat la Alimentara Florea, pentru testul "casierul inactiv nu se poate loga"
    ('c0000000-0000-0000-0000-000000000009', 'mihai.florea@example.com',
     '$2b$10$LbcJD23UgPc9/rvTR142e.RV4JP3oyXtLibwR8gTSg/z82w2n601O', 'Mihai Florea', 'CASIER',
     NULL, 'f0000000-0000-0000-0000-000000000001', false)
ON CONFLICT (id) DO NOTHING;