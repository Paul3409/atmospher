# Quellcode — Feuerwehr Nebelthal

Fabric-Mod für **Minecraft Java 1.21.1**, **JDK 21** und **Fabric API**.
Ein Mod enthält alle fünf Fahrzeuge. Java-Quellcode wird zu einer **.jar-Datei** kompiliert.

| Fahrzeug | Funkkenner | Gestaltung |
| --- | --- | --- |
| MZF / ELW | FL NT 10/18-01 | Einsatzleitwagen mit Kastenwagenaufbau, Antenne und Dachgerät |
| DLK 18 | FL NT 10/32-01 | Drehleiterfahrzeug mit vierteiliger ausfahrbarer Leiter, Korb und Stützen |
| LF 10 | FL NT 10/42-01 | Löschgruppenfahrzeug mit Geräteräumen, Rollläden, Dachleiter und Saugschläuchen |
| TSF-W | FL NT 10/48-01 | Kompakterer Aufbau mit gleichem Flottendesign |
| MZB | FL NT 10/79-01 | Motorboot mit Außenbordmotor, Steuerstand und seitlichem Schriftzug **Nebelthal** |

Alle Fahrzeuge verwenden dieselbe Farbpalette: feuerwehrrote Karosserie, helle Dächer/Stoßfänger, gelbe Konturstreifen, Aluminium-Rollläden und blaue Leuchten. Die 1024×1024-PNG-Atlanten unterscheiden sich in den Funkkennern. Eigene geometrische Modelle, keine Herstellerlogos oder übernommenen Fahrzeugmodelle. Die Gestaltung orientiert sich an deutschen Feuerwehrfahrzeugen; sie ist keine maßstabsgetreue Nachbildung eines konkreten Fahrgestells.

## Unter Windows kompilieren

1. Das komplette ZIP entpacken. `build.gradle`, `settings.gradle` und `src` müssen zusammenbleiben.
2. **JDK 21** installieren. `java -version` und `javac -version` müssen Java 21 anzeigen. `JAVA_HOME` auf den JDK-Ordner setzen, nicht auf dessen `bin`-Unterordner.
3. CMD im entpackten Projektordner öffnen, in dem `build.gradle` liegt.
4. Den gewünschten Befehl ausführen:

```cmd
"C:\Gradle\gradle-8.14.6\bin\gradle.bat" clean build --no-daemon --stacktrace
```

Beim ersten Build werden Minecraft-, Fabric- und Gradle-Abhängigkeiten aus dem Internet geladen. Python, ffmpeg, Blockbench und zusätzliche Modellbibliotheken werden zum Kompilieren **nicht** benötigt.

Ergebnis: **`build\libs\quellcode-1.0.0.jar`**.
Die Datei `quellcode-1.0.0-sources.jar` enthält Quellcode und wird nicht als Mod installiert.

## Installieren und verwenden

- Minecraft **1.21.1** mit Fabric Loader **0.16.14 oder neuer** starten.
- `quellcode-1.0.0.jar` und **Fabric API für 1.21.1** in den `mods`-Ordner legen. Entwickelt mit Fabric API `0.116.6+1.21.1`.
- Für Multiplayer müssen Server und alle Mitspieler den Mod und Fabric API installieren.
- Kreativ-Inventar → **Feuerwehr Nebelthal**. Ein Fahrzeugitem auf einen freien, ebenen Platz rechtsklicken. Für die langen Fahrzeuge mindestens 4×8 freie Blöcke einplanen. Das MZB an einem Ufer/Becken platzieren oder im Wasser beschwören.
- Fahrzeug rechtsklicken: einsteigen. **F5** für bessere Übersicht verwenden.
- **W/S:** vorwärts/rückwärts; **A/D:** lenken; **Umschalt:** aussteigen.
- **B:** Blaulicht; **N:** Zweitonhorn; **L:** Leiter aus-/einfahren, ausschließlich bei stillstehender DLK auf dem Boden. Die Fahrt bleibt während ausgefahrener Leiter gesperrt.
- Die drei Sondertasten sind in den Minecraft-Steuerungsoptionen frei belegbar. WASD folgt ebenfalls den normalen Minecraft-Bewegungstasten.
- Im Kreativmodus: **Schleichen + Rechtsklick** auf ein leeres Fahrzeug entfernt es und lässt das Fahrzeugitem fallen.
- Hornlautstärke: Minecraft-Lautstärkeregler **Freundliche Kreaturen**. Abschalten beendet die laufende Tonsequenz spätestens nach zwei Sekunden.

Alternativ mit Cheats:

```mcfunction
/give @s quellcode:mzf_elw
/give @s quellcode:dlk_18
/give @s quellcode:lf_10
/give @s quellcode:tsf_w
/give @s quellcode:mzb
/summon quellcode:mzb ~ ~ ~
```

## Funktionsumfang und Grenzen

Enthalten: fünf platzierbare und fahrbare Fahrzeugentitäten, ein Fahrerplatz je Fahrzeug, einfaches Beschleunigen/Bremsen/Lenken, Bootsauftrieb, wechselnde Blaulichter, ein eigenes synthetisches Zweitonhorn, animierte Leiter und Stützen, Serverprüfung der Fahrereingaben und Speicherung von Licht/Horn/Leiter im Spielstand. Fahrbetrieb und Leiterzustand laufen auf dem Server; Modelle, Tasten und Darstellung sind separater Client-Code.

Die Modelle sind detaillierte Minecraft-Geometrie, keine fotorealistischen CAD-Modelle. Es gibt noch keine funktionierenden Pumpen, Schläuche, Löschwasser, Inventare, öffnenden Türen/Geräteräume, begehbaren Leitern, benutzbaren Rettungskörbe, Mehrpersonenbesatzung, Schaltgetriebe oder Schadenssimulation. Blaulicht wird hell dargestellt und beleuchtet keine benachbarten Blöcke. Die Kollision ist eine vereinfachte zentrale Minecraft-Hitbox; die langen Überhänge und ausgefahrene Leiter besitzen keine eigene Kollision. Die DLK kann ihre Leiter neigen und ausfahren, aber nicht separat um die Hochachse drehen. Das Boot hat vereinfachten Auftrieb ohne Wellenmodell. Fahrzeuge sind für kreative Ortsgestaltung und Rollenspiel vorgesehen; es gibt keine Herstellungsrezepte.

## Quellcode bearbeiten

- `src/main/java/de/nebelthal/quellcode/VehicleKind.java`: Funkkenner, Abmessungen, Geschwindigkeit.
- `VehicleEntity.java`: Serverphysik, Sitzplatz, Interaktion, gespeicherter Zustand.
- `VehicleItem.java`: Platzierung.
- `ControlPayload.java` / `Quellcode.java`: Netzwerkpaket und Registrierungen.
- `src/client/java/de/nebelthal/quellcode/client/VehicleRenderer.java`: Modellteile in Block-/Meter-Einheiten, Materialien und Leiteranimation.
- `QuellcodeClient.java`: Tasten und Client-Registrierung.
- `src/main/resources/assets/quellcode/textures/entity`: editierbare PNG-Texturen.
- `tools/generate_assets.py`: optionaler Generator der Originaltexturen und des Horns. Nur zum Neugenerieren werden Python mit Pillow, ffmpeg und der im Skript konfigurierte TrueType-Font benötigt.

Die Texturen sind 4×4-Atlanten aus 256×256-Feldern: Lack, Weiß, Reifen, Aluminium, Glas, Rollladen, Gelb, Blaulicht, Scheinwerfer, Rücklicht, Kühlergrill, Seitenbeschriftung, Heckbeschriftung, Riffelblech, Dunkelgrau und Kennzeichen. Alle Quellen und selbst erzeugten Assets stehen unter MIT-Lizenz.

Siehe `VALIDATION.md` für tatsächlich ausgeführte Prüfungen und offene Spieltests.
