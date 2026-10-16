# Visual Boost (Fabric, Minecraft 1.21.11)

Klient-only mod z menu najprzydatniejszych opcji obrazu. Wszystko dziala od razu.

## Klawisze (zmienialne w Opcje > Sterowanie > Visual Boost)
- **Prawy Shift** - menu
- **C** - zoom (przytrzymaj)
- **H** - edycja ukladu HUD (przeciaganie i usuwanie paneli)

## Opcje
- Obraz: fullbright (z regulacja jasnosci), FOV, sila zoomu, cienie postaci
- Kamera: bujanie, przechylanie przy ranie, efekt FOV przy sprincie, znieksztalcenia ekranu, sprint/kucanie jako przelacznik
- Wydajnosc: zasieg renderowania, max FPS, mieszanie biomow, czasteczki, presety Jakosc / Wydajnosc

- HUD (zakladki HUD i Teksty): Info (napis, FPS, ping, czas), Pozycja (XYZ, BPS), Efekty, Cooldowny, Pancerz,
  Ekwipunek, Klawisze (WASD, LMB/RMB z CPS), Gracz, Znajomi (lista lokalna), Zasoby (liczniki przedmiotow).
  W edytorze (klawisz H) panele przeciagasz myszka, klikniecie panelu otwiera jego ustawienia
  (skala, przezroczystosc, kolor tla i akcentu, usuniecie). Snap do krawedzi i siatka.

Config moda: `config/visualboost.json`. Pozostale opcje zapisuje sama gra (options.txt).

## Budowanie
`gradle build` (Java 21, Gradle 9.2+) -> `build/libs/visualboost-1.1.0.jar` (nie *-sources.jar).
