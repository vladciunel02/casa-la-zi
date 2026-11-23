CREATE TABLE cabinet (
    id UUID PRIMARY KEY,
    denumire VARCHAR(255) NOT NULL,
    creat_la TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE firma (
    id UUID PRIMARY KEY,
    cabinet_id UUID NOT NULL REFERENCES cabinet(id),
    denumire VARCHAR(255) NOT NULL,
    cui VARCHAR(20) NOT NULL,
    nr_reg_com VARCHAR(20) NOT NULL ,
    adresa VARCHAR(255) NOT NULL,
    platitor_tva BOOLEAN NOT NULL,
    activ BOOLEAN NOT NULL DEFAULT true,
    creat_la TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT firma_cabinet_id_cui_unique UNIQUE (cabinet_id, cui),
    CONSTRAINT firma_cui_check CHECK (cui ~ '^[0-9]{2,10}$')
);

CREATE TABLE utilizator
(
    id          UUID PRIMARY KEY,
    email       VARCHAR(255) NOT NULL UNIQUE,
    parola_hash VARCHAR(255) NOT NULL,
    nume        VARCHAR(255) NOT NULL,
    rol         VARCHAR(50)  NOT NULL,
    cabinet_id  UUID REFERENCES cabinet (id),
    firma_id    UUID REFERENCES firma (id),
    activ       BOOLEAN      NOT NULL DEFAULT true,
    creat_la    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT utilizator_rol_check CHECK (
        ( rol ='CONTABIL' AND  cabinet_id IS NOT NULL AND firma_id IS NULL) OR
        (rol = 'CASIER' AND cabinet_id IS NULL AND firma_id IS NOT NULL)
    ),
    CONSTRAINT utilizator_email_check CHECK(email = lower(email))
);
