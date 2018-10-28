DROP TABLE WALUTY cascade constraints;
DROP TABLE RYNKI cascade constraints;
DROP TABLE SPOLKA cascade constraints;
DROP TABLE AKCJA cascade constraints;
DROP TABLE PANSTWA cascade constraints;
DROP TABLE LUDZIE cascade constraints;
DROP TABLE INWESTORZY cascade constraints;

create table WALUTY
(nazwa_waluty VARCHAR2(15 CHAR) PRIMARY KEY,
wartosc NUMBER(6,2));

create table PANSTWA
(nazwa VARCHAR2(20) PRIMARY KEY,
waluta REFERENCES WALUTY(nazwa_waluty),
skrot CHAR(3) NOT NULL);

create table RYNKI
(nazwa_rynku VARCHAR(40) PRIMARY KEY,
waluta REFERENCES WALUTY(nazwa_waluty),
panstwo REFERENCES PANSTWA(nazwa));

create table LUDZIE
(pesel NUMBER(9) PRIMARY KEY,
imie VARCHAR(20),
nazwisko VARCHAR(20),
narodowosc REFERENCES PANSTWA(nazwa));

create table INWESTORZY
(pesel NUMBER(9) PRIMARY KEY,
imie VARCHAR(20),
nazwisko VARCHAR(20),
narodowosc REFERENCES PANSTWA(nazwa),
budzet NUMBER (12,2));

create table SPOLKA
(id_spolki INT PRIMARY KEY,
nazwa_spolki VARCHAR2(40) NOT NULL UNIQUE,
data_zalozenia DATE DEFAULT SYSDATE,
budzet NUMBER (12,2) NOT NULL CHECK(budzet >= 0),
ceo REFERENCES LUDZIE(PESEL));

create table AKCJA
(id_akcji PRIMARY KEY REFERENCES SPOLKA(id_spolki),
id_gieldy REFERENCES RYNKI(nazwa_rynku),
wartosc NUMBER (8,2) CHECK (wartosc >= 0));

INSERT INTO WALUTY VALUES('PLN', 1.0);
INSERT INTO WALUTY VALUES('EUR', 4.0);

INSERT INTO PANSTWA VALUES('Polska', 'PLN', 'POL');
INSERT INTO PANSTWA VALUES('Niemcy', 'EUR', 'GER');

INSERT INTO LUDZIE VALUES(666666666, 'SZATAN KURWA', 'BRAK', 'Polska');
INSERT INTO LUDZIE VALUES(213721371, 'Karol', 'Wojtyla', 'Niemcy');
INSERT INTO LUDZIE VALUES(731273127, 'Jan Pawel', 'II', 'Polska');
INSERT INTO LUDZIE VALUES(444444444, 'Jacek', 'Stachurski', 'Polska');
INSERT INTO LUDZIE VALUES(148814881, 'Rudolf', 'Hess', 'Niemcy');
