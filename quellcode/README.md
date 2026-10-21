# Quellcode — Feuerwehr Nebelthal

Fabric-Mod für **Minecraft Java 1.21.1**, **JDK 21** und **Fabric API**.
Ein Mod enthält sechs Fahrzeuge und einen Bergungsschlüssel. Java-Quellcode wird zu einer **.jar-Datei** kompiliert.

| Fahrzeug | Funkkenner | Gestaltung |
| --- | --- | --- |
| MZF / ELW | FL NT 10/18-01 | Einsatzleitwagen mit Kastenwagenaufbau, Antenne und Dachgerät |
| DLK 18 | FL NT 10/32-01 | Drehleiterfahrzeug mit vierteiliger ausfahrbarer Leiter, Korb und Stützen |
| LF 10 | FL NT 10/42-01 | Löschgruppenfahrzeug mit Geräteräumen, Rollläden, Dachleiter und Saugschläuchen |
| TSF-W | FL NT 10/48-01 | Kompakterer Aufbau mit gleichem Flottendesign |
| MZB | FL NT 10/79-01 | Motorboot mit Außenbordmotor, Steuerstand und seitlichem Schriftzug **Nebelthal** |
| RW 2 | FL NT 10/52-01 | Rüstwagen mit kurzem Fahrerhaus, vier seitlichen Geräteräumen und Dachgerät |

Alle Fahrzeuge verwenden dieselbe Farbpalette: feuerwehrrote Karosserie, helle Stoßfänger, gelbe Konturstreifen, Aluminium-Rollläden und blaue Leuchten. Für die Schrift gibt es eigene hochauflösende Texturen. Die Fahrzeuge unterscheiden sich in Kabine, Motorhaube, Aufbau, Radstand und Dachausrüstung. Die beiliegenden Bilder dienten als Referenz für die Silhouetten. Es wurden weder deren Schriftzüge noch Herstellerlogos oder fremde Modelle übernommen. Die Modelle sind Minecraft-Geometrie und keine maßstabsgetreuen Fahrzeugnachbauten.

## Unter Windows kompilieren

1. Das komplette ZIP entpacken. `build.gradle`, `settings.gradle` und `src` müssen zusammenbleiben.
2. **JDK 21** installieren. `java -version` und `javac -version` müssen Java 21 anzeigen. `JAVA_HOME` auf den JDK-Ordner setzen, nicht auf dessen `bin`-Unterordner.
3. CMD im entpackten Projektordner öffnen, in dem `build.gradle` liegt.
4. Den gewünschten Befehl ausführen:

```cmd
"C:\Gradle\gradle-8.14.6\bin\gradle.bat" clean build --no-daemon --stacktrace
```

Beim ersten Build werden Minecraft-, Fabric- und Gradle-Abhängigkeiten aus dem Internet geladen. Python, ffmpeg, Blockbench und zusätzliche Modellbibliotheken werden zum Kompilieren **nicht** benötigt.

Ergebnis: **`build\libs\quellcode-1.1.0.jar`**.
Die Datei `quellcode-1.1.0-sources.jar` enthält Quellcode und wird nicht als Mod installiert.

## Installieren und verwenden

- Minecraft **1.21.1** mit Fabric Loader **0.16.14 oder neuer** starten.
- Die alte `quellcode-1.0.0.jar` aus dem `mods`-Ordner entfernen. Dann `quellcode-1.1.0.jar` und **Fabric API für 1.21.1** hineinlegen. Entwickelt mit Fabric API `0.116.6+1.21.1`. Beide Quellcode-JARs zugleich zu installieren erzeugt einen Mod-ID-Konflikt.
- Für Multiplayer müssen Server und alle Mitspieler den Mod und Fabric API installieren.
- Kreativ-Inventar → **Feuerwehr Nebelthal**. Ein Fahrzeugitem auf einen freien, ebenen Platz rechtsklicken. Für die langen Fahrzeuge mindestens 4×8 freie Blöcke einplanen. Mit dem MZB-Item direkt auf die Wasseroberfläche rechtsklicken; das Boot setzt dort auf dem Wasser auf.
- Fahrzeug rechtsklicken: einsteigen. **F5** für bessere Übersicht verwenden.
- **W/S:** vorwärts/rückwärts; **A/D:** lenken; **Umschalt:** aussteigen.
- **B:** Blaulicht; **N:** Zweitonhorn; **L:** Leiter aus-/einfahren, ausschließlich bei stillstehender DLK auf dem Boden. Die Fahrt bleibt während ausgefahrener Leiter gesperrt.
- Die drei Sondertasten sind in den Minecraft-Steuerungsoptionen frei belegbar. WASD folgt ebenfalls den normalen Minecraft-Bewegungstasten.
- **Bergungsschlüssel** in die Hand nehmen und auf ein unbesetztes Fahrzeug rechtsklicken. Das Fahrzeug wird als Item zurückgegeben. Im Kreativmodus funktioniert bei unbesetzten Fahrzeugen auch Schleichen + Rechtsklick. Der Schlüssel ist im Kreativ-Inventar und per Rezept aus einem Eisenbarren und einem Stock erhältlich.
- Hornlautstärke: Minecraft-Lautstärkeregler **Freundliche Kreaturen**. Das Horn benutzt eine ins OGG-Format übertragene und am Anfang/Ende weich ausgeblendete Fassung der vom Nutzer bereitgestellten MP3. Beim Ausschalten läuft die bereits gestartete, höchstens etwa sieben Sekunden lange Tonfolge noch zu Ende.

Alternativ mit Cheats:

```mcfunction
/give @s quellcode:mzf_elw
/give @s quellcode:dlk_18
/give @s quellcode:lf_10
/give @s quellcode:tsf_w
/give @s quellcode:mzb
/give @s quellcode:rw_2
/give @s quellcode:recovery_tool
/summon quellcode:mzb ~ ~ ~
```

## Funktionsumfang und Grenzen

Enthalten: sechs platzierbare und fahrbare Fahrzeugentitäten, ein Fahrerplatz je Fahrzeug, einfaches Beschleunigen/Bremsen/Lenken, Platzierung auf Wasser und Bootsauftrieb, hell gerenderte wechselnde Blaulichter, das bereitgestellte Martinshorn, eine animierte Leiter mit nach hinten versetztem Drehkranz, ein Bergungsschlüssel, Serverprüfung der Fahrereingaben und Speicherung von Licht/Horn/Leiter im Spielstand. Fahrbetrieb und Leiterzustand laufen auf dem Server; Modelle, Tasten und Darstellung sind separater Client-Code.

Die Modelle sind detaillierte Minecraft-Geometrie, keine fotorealistischen CAD-Modelle. Es gibt noch keine funktionierenden Pumpen, Schläuche, Löschwasser, Inventare, öffnenden Türen/Geräteräume, begehbaren Leitern, benutzbaren Rettungskörbe, Mehrpersonenbesatzung, Schaltgetriebe oder Schadenssimulation. Blaulicht wird hell dargestellt und beleuchtet keine benachbarten Blöcke. Die Kollision ist eine vereinfachte zentrale Minecraft-Hitbox; die langen Überhänge und ausgefahrene Leiter besitzen keine eigene Kollision. Die DLK kann ihre Leiter neigen und ausfahren, aber nicht separat um die Hochachse drehen. Das Boot hat vereinfachten Auftrieb ohne Wellenmodell. Fahrzeuge sind für kreative Ortsgestaltung und Rollenspiel vorgesehen; es gibt keine Herstellungsrezepte.

## Quellcode bearbeiten

- `src/main/java/de/nebelthal/quellcode/VehicleKind.java`: Funkkenner, Abmessungen, Geschwindigkeit.
- `VehicleEntity.java`: Serverphysik, Sitzplatz, Interaktion, gespeicherter Zustand.
- `VehicleItem.java`: Platzierung.
- `ControlPayload.java` / `Quellcode.java`: Netzwerkpaket und Registrierungen.
- `src/client/java/de/nebelthal/quellcode/client/VehicleRendererV2.java`: Fahrzeugformen, Beschriftungsflächen, Materialien und Leiteranimation.
- `QuellcodeClient.java`: Tasten und Client-Registrierung.
- `src/main/resources/assets/quellcode/textures/entity` und `textures/markings`: Materialtexturen und separate Beschriftungen.
- `tools/generate_assets.py`: optionaler Generator der Originaltexturen und des Horns. Nur zum Neugenerieren werden Python mit Pillow, ffmpeg und der im Skript konfigurierte TrueType-Font benötigt.

Die Fahrzeugtexturen sind 4×4-Atlanten aus 256×256-Feldern; die Beschriftung liegt in separaten 512- bzw. 1024 Pixel breiten Bildern. Code und selbst erstellte Grafiken stehen unter MIT-Lizenz. Der Hornmitschnitt wurde vom Nutzer als Vorlage bereitgestellt; seine Nutzungsrechte werden dadurch nicht geändert.

Siehe `VALIDATION.md` für tatsächlich ausgeführte Prüfungen und offene Spieltests.
