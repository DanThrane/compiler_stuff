# DanskDiego

DanskDiego er et imperativt, proceduremæssig sprog, der endelig giver en mulighed for at programmere på dansk.

## Overblik
 
DanskDiego består at et småt simpelt sprog der skrives på HC Andersens modersmål. Compileren er udviklet i selvsamme
persons fødeby.  
 
## Data typer

De primitive primitive datatyper der er tilgængelige i DanskDiego består af `heltal`, `bool` og `tegn`. 

For at definere en variabel, bruges `variable` nøgleordet:

```
variable a: heltal;
```

Som det ses, sluttes en sætning med `;`.

Flere variabler kan instantieres på samme linje, adskildt med komma på følgende måde:

```
variable a: heltal, b: bool, c: tegn;
```

## Type

Nøgleordet `type` kan bruges til at definere en type. F.eks. `type id = heltal` gør `id` lig med `heltal` data typen.

## Samlinger

DanskDiego understøtter opstilling, og datastrukturer. For at definere en opstilling bruges `opstilling af`, med f.eks.:
 

```
variable a: opstilling af heltal, b: opstilling af bool, c: opstilling af opstilling af tegn;
```

Hvor `c` er en to-dimensionel opstilling.

Som det kan læses i koden, er der ikke defineret en længde for opstillingerne. Det gøres med `allok` nøgleorder, hvor
man tildeler en størrelse vha. et heltalt:

```
allok b af længde 5;
```

Hvilket i samarbejde med foregående kode allokerer `b` som en opstilling af booler af størrelse 5.

For at tilgå en værdi i en opstilling bruges vinkelparenteser. For at tilgå det første index i en opstilling skrives det
`arr[0]`.
 
En datastruktur er tilsvarende til en C-datastruktur og bruger nøgleordene `datastruktur af`. F.eks.:

```
variable rec: datastruktur af { a: heltal, b: bool };
```

Hvilket laver en struktur med børnene der kan tilgås med `.`, f.eks. `rec.a` eller `rec.b`. 

Like arrays, records will need to be allocated, which is done similarly to arrays, but without the size:

```
variable rec: datastruktur af { a: heltal, b: bool };
allok rec;
```

Datastrukturer kan defineres med `type` nøgleordet, f.eks. for binære træer:

```
type Knude = datastruktur af { venstreBarn: Knude, højreBarn: Knude, nøgle: heltal };
```

Som bruges til at definere en knude af et binært træ.

## Funktioner

For at definere en funktion bruges `funktion`. En funktion skal have et navn og en type der returneres og skal termineres
med `slut` efterfulgt af samme funktionsnavn. 

En funktion der vender tilbage med et heltal defineres på følgende måde:

```
funktion vendTilbageMedHeltal(a: heltal): heltal
    return a;
end vendTilbageMedHeltal
```

som definerer en funktion `vendTilbageMedHeltal` som tager et enkelt argument, et heltal.

For at kalde funktionen bruges samme standard som fleste C-lignende sprog:

```
vendTilbageMedHeltal(5);
```

## Opstillingslængde og numerisk værdi

For at hente længden af en opstilling kan rør-operatoren bruges:

```
variable længde: heltal, opst: opstilling af heltal;
allok opst af længde 5;
længde = |opst|;
skriv længde; // 5
```

Derudover, kan rør-operatoren bruges til at få den numeriske værdi, f.eks.:

```
variable a: heltal;
a = 0-5;
skriv |a|; // 5
```

## Sammenligninger

DanskDiego har `sandt` og `falsk` som boolske kosntanter, og `nul` til brug med opstillinger og datastrukturer.

Ækvivalens operatorer i Dansk Diego er tilsvarende til de fleste andre imperative sprog, med `==` for lighed, `!=` for ulighed.

Derudover har sproget `<`, `>`, `<=`, `>=` for lighedstjek.

## Kontrol strukturer

### Hvis-sætninger

Hvis-sætninger i DanskDiego kan være efterfulgt af en ellers-sætning, hvilket også kan undlades. Et prædikat skal omsluttes af
parenteser. Prædikater kan kædes sammen med `&&` eller `||` operatorerne.

```
hvis (i == 0) så skriv 1;
ellsers skriv 0; // Kan undlades
```

Hvis-sætninger kan indeholder en enkel sætning, eller en liste af sætninger, hvilket er omgivet af tuborgklammer.

### Så længe-løkker

Så længe-løkker syntaksen kræver et prædikat, tilsvarende til hvis-sætninger, men prædikatet efterfølges af `gør` nøgleordet, f.eks.:

```
variable i: heltal;
i = 5;
så længe (i < 5) gør skriv i;
```

Kroppen af en så længe-løkke kan enten være en enkelt sætning eller en liste af sætninger.

## Goddag, Verden!

Goddag, Verden-programmet i DanskDiego løses med `skriv`-primitiven, og kan gøres ved øverste niveau.

```
srkiv "Goddag, Verden!";
```

## Binært træ gennemgang

```
type Knude = datastruktur af { venstreBarn: Knud, højreBarn: Knude, nøgle: heltal };
funktion initialiserKnude(nøgle: heltal, venstreBarn: Knude, højreBarn: Knude): Knude
    variable k: Knude;
    k.nøgle = nøgle;
    k.venstreBarn = venstreBarn;
    k.højreBarn = højreBarn;
    return k;
slut initialiserKnude

variable venstreBarn: Knude, højreBarn: Knude, rod: Knude;
venstreBarn = initialiserKnude(1, nul, nul);
højreBarn = initialiserKnude(2, nul, nul);
rod = initialiserKnude(0, venstreBarn, højreBarn);

funktion summerNøgler(knude: Knude): heltal
    var nøgleVærdi: heltal;
    nøgleVærdi = 0;
    hvis (knude.venstreBarn != nul) så nøgleVærdi = nøgleVærdi + summerNøgler(knude.venstreBarn);
    hvis (knude.højreBarn != nul) så nøgleVærdi = nøgleVærdi + summerNøgler(knude.højreBarn);
    return nøgleVærdi + knude.nøgle;
slut summerNøgler

skriv summerNøgler(root); // 3
```

