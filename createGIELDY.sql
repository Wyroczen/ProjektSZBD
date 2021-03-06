DROP TABLE WALUTY cascade constraints;
DROP TABLE RYNKI cascade constraints;
DROP TABLE SPOLKA cascade constraints;
DROP TABLE AKCJA cascade constraints;
DROP TABLE PANSTWA cascade constraints;
DROP TABLE LUDZIE cascade constraints;
DROP TABLE INWESTORZY cascade constraints;

create table WALUTY
(id_waluty INTEGER PRIMARY KEY,
nazwa_waluty VARCHAR2(15 CHAR) UNIQUE,
wartosc NUMBER(6,2));

create table PANSTWA
(nazwa VARCHAR2(20) PRIMARY KEY,
waluta REFERENCES WALUTY(nazwa_waluty),
skrot CHAR(3) NOT NULL);

create table RYNKI
(id_rynku INTEGER PRIMARY KEY,
nazwa_rynku VARCHAR2(40 CHAR),
waluta REFERENCES WALUTY(nazwa_waluty),
panstwo REFERENCES PANSTWA(nazwa));

create table LUDZIE
(id_ludzie INTEGER PRIMARY KEY,
pesel NUMBER(11) UNIQUE,
imie VARCHAR(20),
nazwisko VARCHAR(20),
narodowosc REFERENCES PANSTWA(nazwa));

create table INWESTORZY
(pesel NUMBER(11) PRIMARY KEY,
imie VARCHAR(20),
nazwisko VARCHAR(20),
narodowosc REFERENCES PANSTWA(nazwa),
budzet NUMBER (12,2));

create table SPOLKA
(id_spolki INT PRIMARY KEY,
nazwa_spolki VARCHAR2(40 CHAR) NOT NULL UNIQUE,
data_zalozenia DATE DEFAULT SYSDATE,
budzet NUMBER (12,2) NOT NULL CHECK(budzet >= 0),
ceo REFERENCES LUDZIE(PESEL));

create table AKCJA
(id_akcji PRIMARY KEY REFERENCES SPOLKA(id_spolki),
id_gieldy REFERENCES RYNKI(nazwa_rynku),
wartosc NUMBER (8,2) CHECK (wartosc >= 0));

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
INSERT INTO LUDZIE VALUES(73101542494, 'Felix', 'M�ller', 'Niemcy');
INSERT INTO LUDZIE VALUES(56050123244, 'Laura', 'Rothstein', 'Niemcy');
INSERT INTO LUDZIE VALUES(74122548881, 'Jens', 'Pfeiffer', 'Niemcy');

INSERT INTO LUDZIE VALUES(92090281946, '?????', '????', 'Izrael');
INSERT INTO LUDZIE VALUES(87052797742, '????', '????', 'Izrael');
INSERT INTO LUDZIE VALUES(59101731592, '?????', '??????', 'Izrael');

INSERT INTO SPOLKA VALUES(1, 'Orlen', '1991-11-23', 6134884.00, 57112130279);
INSERT INTO SPOLKA VALUES(2, 'Mosad', '1962-06-10', 9356123.00, 59101731592);
INSERT INTO SPOLKA VALUES(3, 'BMW', '1935-08-04', 7738927.00, 56050123244);
INSERT INTO SPOLKA VALUES(4, 'Microsoft', '1991-11-23', 9215784.00, 44040444444);
INSERT INTO SPOLKA VALUES(5, 'Santander', '1971-11-23', 354484979.00, 87052797742);

INSERT INTO RYNKI VALUES('RYNEK PAPIEROW WARTOSCIOWYCH', 'PLN', 'Polska');
INSERT INTO RYNKI VALUES('New York Stock Exchange', 'USD', 'Stany Zjednoczone');
INSERT INTO RYNKI VALUES('Frankfurt Stock Exchange', 'EUR', 'Niemcy');
INSERT INTO RYNKI VALUES('???????', 'JPY', 'Japonia');

INSERT INTO AKCJA VALUES(1, 'RYNEK PAPIEROW WARTOSCIOWYCH', 34.29);
INSERT INTO AKCJA VALUES(2, 'New York Stock Exchange', 13.58);
INSERT INTO AKCJA VALUES(3, 'Frankfurt Stock Exchange', 73.12);
INSERT INTO AKCJA VALUES(4, '???????', 119.97);
INSERT INTO AKCJA VALUES(5, 'RYNEK PAPIEROW WARTOSCIOWYCH', 9.82);