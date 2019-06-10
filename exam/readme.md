# Lift

- Az adatok (osztály neve, port stb.) a megadott alakban szerepeljenek a programban.
- A megoldáshoz célszerű osztályokat, segédfüggvényeket, adatszerkezeteket stb. létrehozni.
- Használhatók az alábbi segédanyagok:
  - A JDK dokumentációja [itt érhető el](https://bead.inf.elte.hu/files/java/api/).
  - A gyakorlatok anyagai (és az adatbáziskezeléshez szükséges jar fájlok) [itt érhetők el](http://exam.inf.elte.hu/osztott-anyagok.zip).
- A megoldást teljesen önállóan kell elkészíteni. Együttműködés/másolás esetén minden érintett fél elégtelent kap.

A további feladatok eggyel növelik a megszerzett jegyet, és tetszőleges sorrendben adhatók hozzá a programhoz.

Beadás: az elkészült megoldás fájljait `zip`-pel kell tömöríteni a feltöltéshez. IDE-ben készült megoldásnál a teljes projekt becsomagolható.

## Alapfeladat

Készíts két szervert az `Elevator` osztályba: a `13578` és `13579` porton indulnak el, és egy liftet valósítanak meg az alábbiak szerint.

A szerver két klienst vár be. A kliensek szövegesen kommunikálnak a szerverrel; a `13578` porton érkező kliens lakókat ad a rendszerhez, a `13579` porton érkező pedig a liftet vezérli. Az utóbbi kliens kezdetben elküldi egy sorban a lift kapacitását (hány fő fér bele). Ezután a szerver felváltva vár egy-egy sort a két klienstől.

A lakók kliensének sora számokat tartalmaz pl. így: `3 0 0 2 -4`.

- az első szám jelöli, melyik emelethez adunk lakókat
  - az emeletek száma nem korlátozott
- a többi szám egy-egy lakót jelöl, akik ezen a szinten tartózkodnak
  - a számaik megadják, hogy melyik emeletre szeretnének menni
  - a sorrendjük számít
  - üres sor jelöli, ha ebben a lépésben nem jön új lakó a lifthez

A lift kliense az `up` vagy a `down` parancsokat kaphatja.

- a lift a `0` szintről indul
- minden lépésben egy szinttel feljebb/lejjebb megy a lift
- szintváltás után a liftből kiszáll minden olyan utas, aki az adott emeletre szeretne menni, ezután beszállnak az emeleten várakozó utasok (legfeljebb a lift kapacitásáig)
  - amikor a lift a `0` szintre ér úgy, hogy nem marad benne utas, a program zárja a kapcsolatot a kliensekkel, és lépjen ki
  - a szerver minden lépésben elküldi a kliensnek, mi történt
    - ha egy 3 kapacitású liftből ketten szállnak ki és hárman be, a kiírás legyen `1\. lift, 3\. szint, 2 ki, [0, 0, 2] be`
    - (az alapfeladatban csak egy lift van, a későbbiekben lehet több is)

Az alapfeladatban a szervernek nem kell párhuzamosan működnie.

A feladathoz nem kell klienst készíteni, szokványos hálózati kommunikációs eszközökkel (putty, telnet) próbálható ki.

## +1

Készíts `elevator-pitch` névre bejegyzett RMI objektumot, amely üzembe állásakor véletlenszerűen kisorsol két különböző emeletet. Ezeket `startFloor` és `endFloor` hívásokkal lehet tőle lekérni.

- a sorsolt értékek legyenek kicsik, pl. essenek a `-2..+2` intervallumba

Amikor egy lift eléri a `startFloor` szintet, elkezdődik egy hirdetés, amely akkor teljesül sikeresen, ha addig, amíg a lift el nem éri az `endFloor` szintet, sosem ürül ki.

- ha a lift az `endFloor` szint elérése előtt kiürül, akkor hívódjon meg az RMI objektum `pitchDone` metódusa `false` paraméterrel; ha a hirdetés sikeres volt, akkor `true` paraméterrel
- a `pitchDone` meghívásakor sorsolódjon új `startFloor` és `endFloor`
- a főprogram kilépéskor írja ki, hány hirdetés lett sikeres és hány sikertelen
  - ehhez legyen két lekérdező művelet is az RMI objektumhoz

## +1

A `Skyscraper` osztályba készítsd el az alapfeladat módosítását az alábbiak szerint.

A `13578` porton még mindig csak egyetlen klienst várunk, ez most működjön a `13579` porton érkező kliensektől függetlenül. Amikor a kliens üres sort küld, a szerver bontja vele a kapcsolatot.

A `13579` porton több klienst is várunk egymás után. A liftek a beérkezés sorrendjében kapnak sorszámot. A kliens liftjének jellegét az első sorban küldött üzenete adja meg.

- `done`: a porton nem fogadunk több klienst, az eddig elindított liftek működnek
- `manual`: a továbbiakban az alapfeladatban leírt módon működik a kliens
- `auto N`: a lift mindig önállóan dönt arról, merre kell mennie
  - `N` egy egész szám, ennyi másodpercenként vált szintet a lift
  - ha nem üres a lift, akkor az elsőként beszállt személy emelete felé halad
  - ha üres, és van várakozó valamelyik szinten, arra megy
  - ha üres, és a `0` szinten van, akkor nem tesz lépést
  - ha üres, és nincsen várakozó, a `0` szint felé megy
  - ez a lift is befejezi a működését, ha a `0` szintre lépett éppen, és üres lett
- ha már nincsen működő lift, akkor a szerver befejezi a futását

## +1

A program tárolja az időt is: kezdetben `17:00` van, és mindig, amikor egy lift szintet vált, eltelik egy perc.

Néhány lakó előre megadott időben érkezik meg a liftekhez. Egy adatbázisban legyen eltárolva az érkezések adatai: az időpont, a szint (ahol a lakó megjelenik) és a célszint (ahová menni szeretne).

Készíts `ElevatorScheduler` osztályt, amellyel hozzá lehet adni új időzítetten megjelenő lakót az adatbázishoz.
