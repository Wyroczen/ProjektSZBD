DROP TABLE WALUTY cascade constraints;
DROP TABLE RYNKI cascade constraints;
DROP TABLE SPOLKA cascade constraints;
DROP TABLE AKCJA cascade constraints;
DROP TABLE PANSTWA cascade constraints;
DROP TABLE LUDZIE cascade constraints;
DROP TABLE INWESTOR cascade constraints;
DROP TABLE INWESTOR_INDYWIDUALNY cascade constraints;
DROP TABLE FUNDUSZ_INWESTYCYJNY cascade constraints;
DROP SEQUENCE rynki_seq;
DROP SEQUENCE inwestor_seq;
DROP SEQUENCE spolka_seq;

create table WALUTY
(nazwa_waluty VARCHAR2(15 CHAR) UNIQUE,
wartosc NUMBER(6,2));

create table PANSTWA
(nazwa VARCHAR2(30 CHAR) PRIMARY KEY,
waluta REFERENCES WALUTY(nazwa_waluty),
skrot CHAR(3) NOT NULL);

create table RYNKI
(id_rynku INTEGER,
nazwa_rynku VARCHAR2(40 CHAR) UNIQUE,
waluta REFERENCES WALUTY(nazwa_waluty),
panstwo REFERENCES PANSTWA(nazwa));

create table LUDZIE
(pesel NUMBER(11) PRIMARY KEY,
imie VARCHAR(20),
nazwisko VARCHAR(20),
narodowosc REFERENCES PANSTWA(nazwa));

create table INWESTOR
(id_inwestora INTEGER,
budzet NUMBER(12,2)
);

create table SPOLKA
(id_spolki INTEGER,
nazwa_spolki VARCHAR2(40 CHAR) NOT NULL UNIQUE,
data_zalozenia DATE DEFAULT SYSDATE,
budzet NUMBER (12,2) NOT NULL CHECK(budzet >= 0),
ceo REFERENCES LUDZIE(PESEL));

--sekwencje rynki
ALTER TABLE RYNKI ADD (
  CONSTRAINT rynki_pk PRIMARY KEY (id_rynku));

CREATE SEQUENCE rynki_seq START WITH 1;

CREATE OR REPLACE TRIGGER rynki_bir
BEFORE INSERT ON RYNKI
FOR EACH ROW

BEGIN
  SELECT rynki_seq.NEXTVAL
  INTO   :new.id_rynku
  FROM   dual;
END;
/
--sekwencje inwestor
ALTER TABLE INWESTOR ADD (
  CONSTRAINT inwestor_pk PRIMARY KEY (id_inwestora));

CREATE SEQUENCE inwestor_seq START WITH 1;

CREATE OR REPLACE TRIGGER inwestor_bir
BEFORE INSERT ON INWESTOR
FOR EACH ROW

BEGIN
  SELECT inwestor_seq.NEXTVAL
  INTO   :new.id_inwestora
  FROM   dual;
END;
/
--sekwencje spolka
ALTER TABLE SPOLKA ADD (
  CONSTRAINT spolka_pk PRIMARY KEY (id_spolki));

CREATE SEQUENCE spolka_seq START WITH 1;

CREATE OR REPLACE TRIGGER spolka_bir
BEFORE INSERT ON SPOLKA
FOR EACH ROW

BEGIN
  SELECT spolka_seq.NEXTVAL
  INTO   :new.id_spolki
  FROM   dual;
END;
/
--inne creaty
create table AKCJA
(id_akcji PRIMARY KEY REFERENCES SPOLKA(id_spolki),
id_gieldy REFERENCES RYNKI(id_rynku),
wartosc NUMBER (8,2) CHECK (wartosc >= 0));

create table INWESTOR_INDYWIDUALNY
(id_ii references INWESTOR(id_inwestora) PRIMARY KEY,
pesel REFERENCES LUDZIE(pesel));

create table FUNDUSZ_INWESTYCYJNY
(id_fi references INWESTOR(id_inwestora) PRIMARY KEY,
nazwa VARCHAR2(20) UNIQUE,
ceo references LUDZIE(pesel));



--insert
INSERT INTO WALUTY VALUES('PLN', 1.0);
INSERT INTO WALUTY VALUES('EUR', 4.0);
INSERT INTO WALUTY VALUES('USD', 3.8);
INSERT INTO WALUTY VALUES('ILS', 2.0);
INSERT INTO WALUTY VALUES('JPY', 0.034);
INSERT INTO WALUTY VALUES('MXN', 0.19);
INSERT INTO WALUTY VALUES('GBP', 5.0);


INSERT INTO PANSTWA VALUES('Polska', 'PLN', 'POL');
INSERT INTO PANSTWA VALUES('Niemcy', 'EUR', 'GER');
INSERT INTO PANSTWA VALUES('Izrael', 'ILS', 'ISR');
INSERT INTO PANSTWA VALUES('Japonia', 'JPY', 'JAP');
INSERT INTO PANSTWA VALUES('Meksyk', 'MXN', 'MEX');
INSERT INTO PANSTWA VALUES('Francja', 'EUR', 'FRA');
INSERT INTO PANSTWA VALUES('Wielka Brytania', 'GBP', 'UK');
INSERT INTO PANSTWA VALUES('Czechy', 'EUR', 'CZE');
INSERT INTO PANSTWA VALUES('Szwecja', 'EUR', 'SWE');
INSERT INTO PANSTWA VALUES('Watykan', 'EUR', 'VAT');
INSERT INTO PANSTWA VALUES('Stany Zjednoczone', 'USD', 'USA');

INSERT INTO LUDZIE VALUES(73100643035, 'Henryk', 'Adamski', 'Polska');
INSERT INTO LUDZIE VALUES(76062352014, 'Adam', 'Sobczak', 'Polska');
INSERT INTO LUDZIE VALUES(57112130279, 'Anna', 'Michalska', 'Polska');
INSERT INTO LUDZIE VALUES(44040444444, 'Jacek', 'Stachurski', 'Polska');
INSERT INTO LUDZIE VALUES(87111021371, 'Karol', 'Mojtala', 'Polska');

INSERT INTO LUDZIE VALUES(34092180324, 'Gabriele', 'Peters', 'Niemcy');
INSERT INTO LUDZIE VALUES(88010489881, 'Rudolf', 'Tesch', 'Niemcy');
INSERT INTO LUDZIE VALUES(73101542494, 'Felix', 'Möller', 'Niemcy');
INSERT INTO LUDZIE VALUES(56050123244, 'Laura', 'Rothstein', 'Niemcy');
INSERT INTO LUDZIE VALUES(74122548881, 'Jens', 'Pfeiffer', 'Niemcy');

INSERT INTO LUDZIE VALUES(92090281946, '?????', '????', 'Izrael');
INSERT INTO LUDZIE VALUES(87052797742, '????', '????', 'Izrael');
INSERT INTO LUDZIE VALUES(59101731592, '?????', '??????', 'Izrael');

INSERT INTO SPOLKA(nazwa_spolki, data_zalozenia, budzet, ceo) VALUES('Orlen', '1991-11-23', 6134884.00, 57112130279);
INSERT INTO SPOLKA(nazwa_spolki, data_zalozenia, budzet, ceo) VALUES('Mosad', '1962-06-10', 9356123.00, 59101731592);
INSERT INTO SPOLKA(nazwa_spolki, data_zalozenia, budzet, ceo) VALUES('BMW', '1935-08-04', 7738927.01, 56050123244);
INSERT INTO SPOLKA(nazwa_spolki, data_zalozenia, budzet, ceo) VALUES('Microsoft', '1991-11-23', 9215784.01, 44040444444);
INSERT INTO SPOLKA(nazwa_spolki, data_zalozenia, budzet, ceo) VALUES('Santander', '1971-11-23', 354484979.00, 87052797742);

INSERT INTO RYNKI(nazwa_rynku, waluta, panstwo) VALUES('RYNEK PAPIEROW WARTOSCIOWYCH', 'PLN', 'Polska');
INSERT INTO RYNKI(nazwa_rynku, waluta, panstwo) VALUES('New York Stock Exchange', 'USD', 'Stany Zjednoczone');
INSERT INTO RYNKI(nazwa_rynku, waluta, panstwo) VALUES('Frankfurt Stock Exchange', 'EUR', 'Niemcy');
INSERT INTO RYNKI(nazwa_rynku, waluta, panstwo) VALUES('???????', 'JPY', 'Japonia');

INSERT INTO AKCJA VALUES(1, 1, 34.29);
INSERT INTO AKCJA VALUES(2, 2, 13.58);
INSERT INTO AKCJA VALUES(3, 3, 73.12);
INSERT INTO AKCJA VALUES(4, 4, 119.97);
INSERT INTO AKCJA VALUES(5, 1, 9.82);