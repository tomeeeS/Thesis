Itt találhatóak a diplomamunkám keretében készített kódbázisaim.

### Ride-sharing application
A ride-sharing szoftver általam készített implementációja az 'src' mappában található. Az útvonalhálózattal, útvonaltervezéssel és a résztvevők egészértékű programozással való párosításával kapcsolatos munkámat tartalmazza. Nem futtatható, mivel nem tartalmazza azt a részt, amit a két BME hallgató készített, csak az én kódomat; az egyetem követelményeinek megfelelően.  

Viszont hogy futtatni is lehessen, a '\_\_NOT_ONLY_MINE_src' mappában található a ride-sharing alkalmazáshoz tartozó összes kód. Futtatásához szükségeltetik legalább Java 11 SDK. Telepíteni kell továbbá a gépünkre a MySql-t, PostgreSql, és az egészértékű programozás (IP) könyvtárakat. Az előbbiekhez szükséges root joggal rendelkező felhasználó, és létrehozni a táblákat a 'create_databse.sql' script lefuttatásával. Az IP könyvtárakat azért kell telepíteni és nemcsak függőségként használni, mert nem Java alapúak, és a hozzájuk tartozó Java wrapper könyvtárak (amik az 'src' mappák 'lib' mappájában már megvannak) tudják felhasználni shared library-ként őket (a gépünkön ezek .a ill. .so fájlok), miután a library path-ba (LD_LIBRARY_PATH) beraktuk az elérési útvonalaikat, és ezt a környezeti változót megadtuk a futtató környezetünknek.

### Forgalom-előrejelzés
A forgalom-előrejelzési kódom a „Maryland_traffic” mappában található.


# EN
This is the code for my Master's thesis.

### Ride-sharing application
My implementation for the ride-sharing application is located in the 'src' folder. It contains work related to the road network, routing, and participant matching optimization with IP. It cannot be run, as it does not include the portion that the two students of BME have made, only my code, as prescribed by the requirements of ELTE.  

### Traffic forecasting
My traffic forecasting code is in the 'Maryland_traffic' folder.
