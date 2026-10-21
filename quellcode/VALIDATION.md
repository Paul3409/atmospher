# Validation — 2026-10-21

## Build and server

The updated project compiled with JDK 21 and the requested Gradle 8.14.6:

```text
gradle clean build --no-daemon --stacktrace --offline
BUILD SUCCESSFUL in 14s
9 actionable tasks: 9 executed
```

The build compiled both common/server and client code, processed resources and remapped `quellcode-1.1.0.jar`. Gradle's existing deprecation warning remains. The source has no automated Java unit tests (`test NO-SOURCE`).

A Fabric 1.21.1 dedicated server started successfully with version 1.1.0. Its recipe count increased from 1290 to 1291 after adding the recovery tool recipe. `/summon quellcode:rw_2` returned `Summoned new RW 2 — FL NT 10/52-01`. Previously saved LF 10 and MZB entities were still present in the same world. The old boat remained at Y ≈ 2.537 on the test water surface. `save-all` and clean shutdown completed.

Minecraft printed its usual `No data fixer registered` notices for the six custom entity types. An offline public-key request also failed in this isolated test environment. Neither prevented world loading or spawning.

## Resources

All six vehicle atlases, high-resolution label textures, inventory icons and the converted OGG are included in the build. The OGG is 7.262 seconds, stereo Vorbis at 44.1 kHz. It derives from the user-supplied MP3 with a high-pass/low-pass filter and soft fades at the start and end. The recovered MP3 printed one decoder warning about a malformed frame; ffmpeg still produced a valid OGG.

## Outstanding client playtest

A graphical Minecraft client was unavailable for this build. The following have not been verified in-game after the update: direct water-surface item placement, driving with W/A/S/D, use of the recovery tool, daylight brightness of the beacons, label readability at normal game distance, exact appearance relative to the supplied example images, horn quality, and multi-player synchronization. Code compilation and a server smoke test do not establish those behaviors. If any of them fails, capture a screenshot and `logs/latest.log` from that play session.
