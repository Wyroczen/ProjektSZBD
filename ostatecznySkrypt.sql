DROP TABLE WALUTA cascade constraints;
DROP TABLE GIELDA cascade constraints;
DROP TABLE SPOLKA cascade constraints;
DROP TABLE AKCJA cascade constraints;
DROP TABLE PANSTWO cascade constraints;
DROP TABLE CZLOWIEK cascade constraints;
DROP TABLE INWESTOR cascade constraints;
DROP TABLE TRANSAKCJA cascade constraints;
DROP TABLE KURS cascade constraints;

create table WALUTA
(nazwa_waluty VARCHAR2(40 CHAR) PRIMARY KEY NOT NULL,
wartosc NUMBER(6,2) NOT NULL CHECK(wartosc >= 0));

create table KURS
(waluta1 references WALUTA(nazwa_waluty) NOT NULL,
waluta2 references WALUTA(nazwa_waluty) NOT NULL,
cena_wymiany NUMBER(6,2) NOT NULL CHECK(cena_wymiany >= 0),
PRIMARY KEY(waluta1, waluta2));

create table PANSTWO
(nazwa VARCHAR2(40 CHAR) PRIMARY KEY NOT NULL,
waluta REFERENCES WALUTA(nazwa_waluty) NOT NULL,
skrot CHAR(3 CHAR) NOT NULL);

create table GIELDA
(nazwa_gieldy VARCHAR2(40 CHAR) PRIMARY KEY NOT NULL,
waluta REFERENCES WALUTA(nazwa_waluty) NOT NULL,
kraj REFERENCES PANSTWO(nazwa) NOT NULL);

create table CZLOWIEK
(pesel NUMBER(11) PRIMARY KEY NOT NULL,
imie VARCHAR2(40 CHAR) NOT NULL,
nazwisko VARCHAR2(40 CHAR) NOT NULL,
narodowosc REFERENCES PANSTWO(nazwa) NOT NULL);

create table SPOLKA
(id_spolki NUMBER GENERATED ALWAYS AS IDENTITY (START WITH 1000 INCREMENT BY 10) PRIMARY KEY NOT NULL,
nazwa_spolki VARCHAR2(40 CHAR) NOT NULL UNIQUE,
data_zalozenia DATE DEFAULT SYSDATE NOT NULL,
budzet NUMBER (12,2) NOT NULL CHECK(budzet >= 0),
ceo REFERENCES CZLOWIEK(PESEL) NOT NULL);

create table AKCJA
(id_spolki PRIMARY KEY REFERENCES SPOLKA(id_spolki)NOT NULL,
gielda REFERENCES GIELDA(nazwa_gieldy) NOT NULL,
wartosc NUMBER (12,2) CHECK (wartosc >= 0) NOT NULL,
ilosc INTEGER NOT NULL CHECK (ilosc >= 0));

create table INWESTOR
(id_inwestora NUMBER GENERATED ALWAYS AS IDENTITY (START WITH 1000 INCREMENT BY 10) PRIMARY KEY NOT NULL,
budzet NUMBER(12,2) NOT NULL,
typ VARCHAR2(40 CHAR) NOT NULL CHECK (typ IN('Inwestor indywidualny','Fundusz inwestycyjny','Spolka akcyjna')),
osoba references CZLOWIEK(pesel) NULL,
zarzadca references CZLOWIEK(pesel) NULL,
spolka references SPOLKA(id_spolki) NULL);

create table TRANSAKCJA
(id_transakcji NUMBER GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1) PRIMARY KEY NOT NULL,
inwestor1 REFERENCES INWESTOR(id_inwestora) NOT NULL,
inwestor2 REFERENCES INWESTOR(id_inwestora) NOT NULL,
akcja REFERENCES AKCJA(id_spolki) NOT NULL,
liczba INTEGER NOT NULL,
cena NUMBER(6,2) NOT NULL CHECK (cena >= 0));

--zlicz akcje inwestora
CREATE OR REPLACE FUNCTION liczba_akcji(n_inwestor IN INT, n_akcja IN INT)
return INT IS
liczba INT;
n INT;
m INT;
s INT;
checking INT;
type1 varchar2(200);
BEGIN    
    liczba := 0;
    n := 0;
    m := 0;
    s := 0;
    SELECT COUNT(*) INTO checking FROM TRANSAKCJA where inwestor1 = n_inwestor and akcja = n_akcja;
    
    if checking > 0 THEN
        SELECT NVL(SUM(liczba), 0) INTO n
        FROM TRANSAKCJA 
        where inwestor1 = n_inwestor and akcja = n_akcja
        GROUP BY inwestor1;
    
        return m;--
    
        SELECT NVL(SUM(liczba), 0) INTO m
        FROM TRANSAKCJA 
        where inwestor1 = n_inwestor and akcja = n_akcja
        GROUP BY inwestor1;   
    end if;
    
    n := n-m;
    liczba := n;
    
    SELECT typ INTO type1 FROM inwestor WHERE id_inwestora=n_inwestor;
    if (type1 = 'Spolka akcyjna') THEN
        SELECT ilosc INTO s
        FROM AKCJA 
        where id_spolki = n_akcja;
        
        liczba := s-m;
    END IF;
    return liczba;
END;
/

CREATE OR REPLACE FUNCTION kantor(n_waluty1 IN varchar2, n_waluty2 IN varchar2, n_ilosc IN number)
return number IS
kurs1 number;
kurs2 number;
wynik number;
BEGIN    
    SELECT wartosc INTO kurs1 FROM WALUTA WHERE nazwa_waluty=n_waluty1;
    SELECT wartosc INTO kurs2 FROM WALUTA WHERE nazwa_waluty=n_waluty2;
    wynik := kurs1*n_ilosc/kurs2;
    return wynik;
END;
/

--nowa waluta
CREATE OR REPLACE PROCEDURE insert_waluta (new_waluta IN varchar2, new_kurs IN number) as
BEGIN
    INSERT INTO WALUTA VALUES(new_waluta, new_kurs);

    INSERT INTO KURS (waluta1, waluta2, cena_wymiany)
    SELECT nazwa_waluty, new_waluta, wartosc/new_kurs
    FROM WALUTA
    WHERE nazwa_waluty != new_waluta;
    
    INSERT INTO KURS (waluta1, waluta2, cena_wymiany)
    SELECT  new_waluta, nazwa_waluty, new_kurs/wartosc
    FROM WALUTA
    WHERE nazwa_waluty != new_waluta;
END;
/

--update wartosci waluty
CREATE OR REPLACE PROCEDURE update_waluta (nazwa1 IN varchar2, new_wartosc IN number) as
BEGIN
    UPDATE WALUTA
    SET wartosc = new_wartosc
    WHERE nazwa_waluty = nazwa1;
    
    UPDATE (SELECT * FROM KURS WHERE waluta1=nazwa1) s
    SET cena_wymiany = new_wartosc/(SELECT wartosc FROM WALUTA w WHERE w.nazwa_waluty=s.waluta2);
    
    UPDATE (SELECT * FROM KURS WHERE waluta2=nazwa1) s
    SET cena_wymiany = (SELECT wartosc FROM WALUTA w WHERE w.nazwa_waluty=s.waluta1)/new_wartosc;
END;
/
--nowa transakcja
CREATE OR REPLACE PROCEDURE insert_transakcja (new_inwestor1 IN INT, new_inwestor2 IN INT, new_akcja IN INT, new_liczba IN INT) as
budzet_sprzedajacego NUMBER;
wartosc_transakcji NUMBER;
BEGIN
    if liczba_akcji(new_inwestor2, new_akcja) < new_liczba THEN 
        return;
    end if;

    select budzet into budzet_sprzedajacego from inwestor where id_inwestora=new_inwestor1;
    SELECT wartosc into wartosc_transakcji FROM akcja WHERE id_spolki=new_akcja;
    wartosc_transakcji := wartosc_transakcji*new_liczba;
    
    if budzet_sprzedajacego < wartosc_transakcji THEN
        return;
    end if;
    
    INSERT INTO TRANSAKCJA(inwestor1, inwestor2, akcja, liczba, cena)
    VALUES(new_inwestor1, new_inwestor2, new_akcja, new_liczba, (SELECT wartosc FROM akcja WHERE id_spolki=new_akcja));

    UPDATE INWESTOR
    SET budzet = budzet - new_liczba*(SELECT wartosc FROM akcja WHERE id_spolki=new_akcja)
    WHERE id_inwestora = new_inwestor1;
    
    UPDATE INWESTOR
    SET budzet = budzet + new_liczba*(SELECT wartosc FROM akcja WHERE id_spolki=new_akcja)
    WHERE id_inwestora = new_inwestor2;
END;
/

CREATE OR REPLACE PROCEDURE get_waluty (nazwa    IN  varchar2,
                      recordset OUT SYS_REFCURSOR) AS 
BEGIN
    OPEN recordset FOR
      SELECT * FROM WALUTA
      WHERE nazwa_waluty LIKE nazwa; 
END;
/

CREATE OR REPLACE FUNCTION get_lspolek( prog IN integer )
  RETURN integer
IS
liczba integer;
BEGIN
  SELECT COUNT(*)
  INTO liczba
  FROM SPOLKA s
  WHERE prog <= (SELECT a.wartosc * a.ilosc FROM AKCJA a WHERE a.id_spolki=s.id_spolki);
  RETURN liczba;
END;
/



--insert
CALL insert_waluta('PLN', 1.0);
CALL insert_waluta('EUR', 4.0);
CALL insert_waluta('USD', 3.8);
CALL insert_waluta('ILS', 2.0);
CALL insert_waluta('JPY', 0.034);
CALL insert_waluta('MXN', 0.19);
CALL insert_waluta('GBP', 5.0);
--
--
INSERT INTO PANSTWO VALUES('Polska', 'PLN', 'POL');
INSERT INTO PANSTWO VALUES('Niemcy', 'EUR', 'GER');
INSERT INTO PANSTWO VALUES('Izrael', 'ILS', 'ISR');
INSERT INTO PANSTWO VALUES('Japonia', 'JPY', 'JAP');
INSERT INTO PANSTWO VALUES('Meksyk', 'MXN', 'MEX');
INSERT INTO PANSTWO VALUES('Francja', 'EUR', 'FRA');
INSERT INTO PANSTWO VALUES('Wielka Brytania', 'GBP', 'UK');
INSERT INTO PANSTWO VALUES('Czechy', 'EUR', 'CZE');
INSERT INTO PANSTWO VALUES('Szwecja', 'EUR', 'SWE');
INSERT INTO PANSTWO VALUES('Watykan', 'EUR', 'VAT');
INSERT INTO PANSTWO VALUES('Stany Zjednoczone', 'USD', 'USA');
--
INSERT INTO CZLOWIEK VALUES(73100643035, 'Henryk', 'Adamski', 'Polska');
INSERT INTO CZLOWIEK VALUES(76062352014, 'Adam', 'Sobczak', 'Polska');
INSERT INTO CZLOWIEK VALUES(57112130279, 'Anna', 'Michalska', 'Polska');
INSERT INTO CZLOWIEK VALUES(44040444444, 'Jacek', 'Stachurski', 'Polska');
INSERT INTO CZLOWIEK VALUES(87111021371, 'Karol', 'Mojtala', 'Polska');
--
INSERT INTO CZLOWIEK VALUES(34092180324, 'Gabriele', 'Peters', 'Niemcy');
INSERT INTO CZLOWIEK VALUES(88010489881, 'Rudolf', 'Tesch', 'Niemcy');
INSERT INTO CZLOWIEK VALUES(73101542494, 'Felix', 'Möller', 'Niemcy');
INSERT INTO CZLOWIEK VALUES(56050123244, 'Laura', 'Rothstein', 'Niemcy');
INSERT INTO CZLOWIEK VALUES(74122548881, 'Jens', 'Pfeiffer', 'Niemcy');
--
INSERT INTO CZLOWIEK VALUES(92090281946, 'Mordechaj', 'Blumsztajn', 'Izrael');
INSERT INTO CZLOWIEK VALUES(87052797742, 'Benjamin', 'Netanyahu', 'Izrael');
INSERT INTO CZLOWIEK VALUES(59101731592, 'Akiba', 'Rubinstein', 'Izrael');
--
INSERT INTO SPOLKA(nazwa_spolki, data_zalozenia, budzet, ceo) VALUES('Orlen', '1991-11-23', 6134884.00, 57112130279);
INSERT INTO SPOLKA(nazwa_spolki, data_zalozenia, budzet, ceo) VALUES('Mosad', '1962-06-10', 9356123.00, 59101731592);
INSERT INTO SPOLKA(nazwa_spolki, data_zalozenia, budzet, ceo) VALUES('BMW', '1935-08-04', 7738927.01, 56050123244);
INSERT INTO SPOLKA(nazwa_spolki, data_zalozenia, budzet, ceo) VALUES('Microsoft', '1991-11-23', 9215784.01, 44040444444);
INSERT INTO SPOLKA(nazwa_spolki, data_zalozenia, budzet, ceo) VALUES('Santander', '1971-11-23', 354484979.00, 87052797742);
--
INSERT INTO GIELDA(nazwa_gieldy, waluta, kraj) VALUES('RYNEK PAPIEROW WARTOSCIOWYCH', 'PLN', 'Polska');
INSERT INTO GIELDA(nazwa_gieldy, waluta, kraj) VALUES('New York Stock Exchange', 'USD', 'Stany Zjednoczone');
INSERT INTO GIELDA(nazwa_gieldy, waluta, kraj) VALUES('Frankfurt Stock Exchange', 'EUR', 'Niemcy');
INSERT INTO GIELDA(nazwa_gieldy, waluta, kraj) VALUES('Tokyo Stock Exchange', 'JPY', 'Japonia');
--
INSERT INTO AKCJA VALUES(1000, 'RYNEK PAPIEROW WARTOSCIOWYCH', 34.29, 10000);
INSERT INTO AKCJA VALUES(1010, 'New York Stock Exchange', 13.58, 15000);
INSERT INTO AKCJA VALUES(1020, 'Frankfurt Stock Exchange', 73.12, 40000);
INSERT INTO AKCJA VALUES(1030, 'Tokyo Stock Exchange', 119.97, 9500);
INSERT INTO AKCJA VALUES(1040, 'RYNEK PAPIEROW WARTOSCIOWYCH', 9.82, 17000);
--
INSERT INTO INWESTOR(budzet, typ, osoba, zarzadca, spolka) VALUES(9231.13, 'Inwestor indywidualny', 56050123244, null, null);
INSERT INTO INWESTOR(budzet, typ, osoba, zarzadca, spolka) VALUES(48744.61, 'Inwestor indywidualny', 87052797742, null, null);
INSERT INTO INWESTOR(budzet, typ, osoba, zarzadca, spolka) VALUES(823.88, 'Inwestor indywidualny', 34092180324, null, null);
INSERT INTO INWESTOR(budzet, typ, osoba, zarzadca, spolka) VALUES(2137.85, 'Inwestor indywidualny', 76062352014, null, null);
INSERT INTO INWESTOR(budzet, typ, osoba, zarzadca, spolka) VALUES(1114.88, 'Inwestor indywidualny', 87111021371, null, null);
--
INSERT INTO INWESTOR(budzet, typ, osoba, zarzadca, spolka) VALUES(192968.23, 'Fundusz inwestycyjny', null, 59101731592, null);
INSERT INTO INWESTOR(budzet, typ, osoba, zarzadca, spolka) VALUES(581882.58, 'Fundusz inwestycyjny', null, 92090281946, null);
INSERT INTO INWESTOR(budzet, typ, osoba, zarzadca, spolka) VALUES(1197418.81, 'Fundusz inwestycyjny', null, 73101542494, null);
INSERT INTO INWESTOR(budzet, typ, osoba, zarzadca, spolka) VALUES(837891.94, 'Fundusz inwestycyjny', null, 34092180324, null);
INSERT INTO INWESTOR(budzet, typ, osoba, zarzadca, spolka) VALUES(238003.99, 'Fundusz inwestycyjny', null, 57112130279, null);
--
INSERT INTO INWESTOR(budzet, typ, osoba, zarzadca, spolka) VALUES(0.0, 'Spolka akcyjna', null, null, 1000);
INSERT INTO INWESTOR(budzet, typ, osoba, zarzadca, spolka) VALUES(0.0, 'Spolka akcyjna', null, null, 1010);
INSERT INTO INWESTOR(budzet, typ, osoba, zarzadca, spolka) VALUES(0.0, 'Spolka akcyjna', null, null, 1020);
INSERT INTO INWESTOR(budzet, typ, osoba, zarzadca, spolka) VALUES(0.0, 'Spolka akcyjna', null, null, 1030);
INSERT INTO INWESTOR(budzet, typ, osoba, zarzadca, spolka) VALUES(0.0, 'Spolka akcyjna', null, null, 1040);
--
call insert_transakcja(1000, 1100, 1000, 50);
call insert_transakcja(1010, 1110, 1010, 1000);
call insert_transakcja(1020, 1120, 1020, 67);
call insert_transakcja(1030, 1130, 1030, 38);
call insert_transakcja(1040, 1140, 1040, 19);
call insert_transakcja(1050, 1100, 1000, 80);
call insert_transakcja(1060, 1110, 1010, 10);
call insert_transakcja(1070, 1120, 1020, 240);
call insert_transakcja(1080, 1130, 1030, 900);
call insert_transakcja(1090, 1140, 1040, 100);
