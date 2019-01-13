CREATE OR REPLACE FUNCTION get_lspolek (
    prog IN INTEGER
) RETURN INTEGER IS
    liczba   INTEGER;
BEGIN
    SELECT
        COUNT(*)
    INTO liczba
    FROM
        spolka s
    WHERE
        prog <= (
            SELECT
                a.wartosc * a.ilosc
            FROM
                akcja a
            WHERE
                a.id_spolki = s.id_spolki
        );

    RETURN liczba;
END;
/
CREATE OR REPLACE PROCEDURE get_waluty (
    nazwa       IN VARCHAR2,
    recordset   OUT SYS_REFCURSOR
) AS
BEGIN
    OPEN recordset FOR SELECT
                           *
                       FROM
                           waluta
                       WHERE
                           nazwa_waluty LIKE nazwa;

END;
/
CREATE TABLE akcja (
    id_spolki   NUMBER NOT NULL,
    gielda      VARCHAR2(40 CHAR) NOT NULL,
    wartosc     NUMBER(12,2) NOT NULL,
    ilosc       INTEGER NOT NULL
);

ALTER TABLE akcja ADD CHECK ( wartosc >= 0 );

ALTER TABLE akcja ADD CHECK ( ilosc >= 0 );

ALTER TABLE akcja ADD CONSTRAINT akcja_pk PRIMARY KEY ( id_spolki );
CREATE TABLE czlowiek (
    pesel        NUMBER(11) NOT NULL,
    imie         VARCHAR2(40 CHAR) NOT NULL,
    nazwisko     VARCHAR2(40 CHAR) NOT NULL,
    narodowosc   VARCHAR2(40 CHAR) NOT NULL
);

ALTER TABLE czlowiek ADD CONSTRAINT czlowiek_pk PRIMARY KEY ( pesel );
CREATE TABLE gielda (
    nazwa_gieldy   VARCHAR2(40 CHAR) NOT NULL,
    waluta         VARCHAR2(40 CHAR) NOT NULL,
    kraj           VARCHAR2(40 CHAR) NOT NULL
);

ALTER TABLE gielda ADD CONSTRAINT gielda_pk PRIMARY KEY ( nazwa_gieldy );
CREATE TABLE inwestor (
    id_inwestora   NUMBER NOT NULL,
    budzet         NUMBER(12,2) NOT NULL,
    typ            VARCHAR2(40 CHAR) NOT NULL,
    osoba          NUMBER(11),
    zarzadca       NUMBER(11),
    spolka         NUMBER
);

ALTER TABLE inwestor ADD CONSTRAINT inwestor_pk PRIMARY KEY ( id_inwestora );

CREATE SEQUENCE inwestor_id_inwestora_seq START WITH 1000 INCREMENT BY 10 NOCACHE;

CREATE OR REPLACE TRIGGER inwestor_id_inwestora_trg BEFORE
    INSERT ON inwestor
    FOR EACH ROW
BEGIN
    :new.id_inwestora := inwestor_id_inwestora_seq.nextval;
END;
/
CREATE TABLE kurs (
    waluta1        VARCHAR2(40 CHAR) NOT NULL,
    waluta2        VARCHAR2(40 CHAR) NOT NULL,
    cena_wymiany   NUMBER(6,2) NOT NULL
);

ALTER TABLE kurs ADD CHECK ( cena_wymiany >= 0 );

ALTER TABLE kurs ADD CONSTRAINT kurs_pk PRIMARY KEY ( waluta1,
                                                      waluta2 );
CREATE TABLE panstwo (
    nazwa    VARCHAR2(40 CHAR) NOT NULL,
    waluta   VARCHAR2(40 CHAR) NOT NULL,
    skrot    CHAR(3 CHAR) NOT NULL
);

ALTER TABLE panstwo ADD CONSTRAINT panstwo_pk PRIMARY KEY ( nazwa );
CREATE TABLE spolka (
    id_spolki        NUMBER NOT NULL,
    nazwa_spolki     VARCHAR2(40 CHAR) NOT NULL,
    data_zalozenia   DATE DEFAULT SYSDATE NOT NULL,
    budzet           NUMBER(12,2) NOT NULL,
    ceo              NUMBER(11) NOT NULL
);

ALTER TABLE spolka ADD CHECK ( budzet >= 0 );

ALTER TABLE spolka ADD CONSTRAINT spolka_pk PRIMARY KEY ( id_spolki );

ALTER TABLE spolka ADD CONSTRAINT index_1 UNIQUE ( nazwa_spolki );

CREATE SEQUENCE spolka_id_spolki_seq START WITH 1000 INCREMENT BY 10 NOCACHE;

CREATE OR REPLACE TRIGGER spolka_id_spolki_trg BEFORE
    INSERT ON spolka
    FOR EACH ROW
BEGIN
    :new.id_spolki := spolka_id_spolki_seq.nextval;
END;
/
CREATE TABLE transakcja (
    id_transakcji   NUMBER NOT NULL,
    inwestor1       NUMBER NOT NULL,
    inwestor2       NUMBER NOT NULL,
    akcja           NUMBER NOT NULL,
    liczba          INTEGER NOT NULL,
    cena            NUMBER(6,2) NOT NULL
);

ALTER TABLE transakcja ADD CHECK ( cena >= 0 );

ALTER TABLE transakcja ADD CONSTRAINT transakcja_pk PRIMARY KEY ( id_transakcji );

CREATE SEQUENCE transakcja_id_transakcji_seq START WITH 1000 INCREMENT BY 10 NOCACHE;

CREATE OR REPLACE TRIGGER transakcja_id_transakcji_trg BEFORE
    INSERT ON transakcja
    FOR EACH ROW
BEGIN
    :new.id_transakcji := transakcja_id_transakcji_seq.nextval;
END;
/
CREATE TABLE waluta (
    nazwa_waluty   VARCHAR2(40 CHAR) NOT NULL,
    wartosc        NUMBER(6,2) NOT NULL
);

ALTER TABLE waluta ADD CHECK ( wartosc >= 0 );

ALTER TABLE waluta ADD CONSTRAINT waluta_pk PRIMARY KEY ( nazwa_waluty );
ALTER TABLE akcja
    ADD CONSTRAINT akcja_spolka_fk FOREIGN KEY ( id_spolki )
        REFERENCES spolka ( id_spolki )
    NOT DEFERRABLE;
ALTER TABLE akcja
    ADD CONSTRAINT akcja_gielda_fk FOREIGN KEY ( gielda )
        REFERENCES gielda ( nazwa_gieldy )
    NOT DEFERRABLE;
ALTER TABLE czlowiek
    ADD CONSTRAINT czlowiek_panstwo_fk FOREIGN KEY ( narodowosc )
        REFERENCES panstwo ( nazwa )
    NOT DEFERRABLE;
ALTER TABLE gielda
    ADD CONSTRAINT gielda_waluta_fk FOREIGN KEY ( waluta )
        REFERENCES waluta ( nazwa_waluty )
    NOT DEFERRABLE;
ALTER TABLE gielda
    ADD CONSTRAINT gielda_panstwo_fk FOREIGN KEY ( kraj )
        REFERENCES panstwo ( nazwa )
    NOT DEFERRABLE;
ALTER TABLE inwestor
    ADD CONSTRAINT inwestor_czlowiek_fk FOREIGN KEY ( osoba )
        REFERENCES czlowiek ( pesel )
    NOT DEFERRABLE;
ALTER TABLE inwestor
    ADD CONSTRAINT inwestor_czlowiek_fkv1 FOREIGN KEY ( zarzadca )
        REFERENCES czlowiek ( pesel )
    NOT DEFERRABLE;
ALTER TABLE inwestor
    ADD CONSTRAINT inwestor_spolka_fk FOREIGN KEY ( spolka )
        REFERENCES spolka ( id_spolki )
    NOT DEFERRABLE;
ALTER TABLE kurs
    ADD CONSTRAINT kurs_waluta_fk FOREIGN KEY ( waluta1 )
        REFERENCES waluta ( nazwa_waluty )
    NOT DEFERRABLE;
ALTER TABLE kurs
    ADD CONSTRAINT kurs_waluta_fkv1 FOREIGN KEY ( waluta2 )
        REFERENCES waluta ( nazwa_waluty )
    NOT DEFERRABLE;
ALTER TABLE panstwo
    ADD CONSTRAINT panstwo_waluta_fk FOREIGN KEY ( waluta )
        REFERENCES waluta ( nazwa_waluty )
    NOT DEFERRABLE;
ALTER TABLE spolka
    ADD CONSTRAINT spolka_czlowiek_fk FOREIGN KEY ( ceo )
        REFERENCES czlowiek ( pesel )
    NOT DEFERRABLE;
ALTER TABLE transakcja
    ADD CONSTRAINT transakcja_inwestor_fk FOREIGN KEY ( inwestor1 )
        REFERENCES inwestor ( id_inwestora )
    NOT DEFERRABLE;
ALTER TABLE transakcja
    ADD CONSTRAINT transakcja_inwestor_fkv1 FOREIGN KEY ( inwestor2 )
        REFERENCES inwestor ( id_inwestora )
    NOT DEFERRABLE;
ALTER TABLE transakcja
    ADD CONSTRAINT transakcja_akcja_fk FOREIGN KEY ( akcja )
        REFERENCES akcja ( id_spolki )
    NOT DEFERRABLE;