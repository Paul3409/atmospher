# Validation — 2026-10-21

## Build

Successful clean production build with **Gradle 8.14.6**, **JDK 21**, Minecraft 1.21.1, Yarn 1.21.1+build.3, Fabric Loader 0.16.14, Fabric API 0.116.6+1.21.1 and Fabric Loom 1.10.5.

The Linux equivalent of the requested Windows command was executed:

```text
gradle clean build --no-daemon --stacktrace
BUILD SUCCESSFUL in 26s
9 actionable tasks: 9 executed
```

Environment-specific proxy and system certificate-store JVM flags were necessary in the build environment. They are not embedded in the project or needed for an ordinary Windows connection. Windows CMD itself was not available for testing.

Both `compileJava` and `compileClientJava`, resource processing, and production JAR remapping completed. Gradle reports deprecated features in the Fabric build toolchain; use the specified Gradle 8.14.6, not Gradle 9. There are no automated Java unit tests (`test NO-SOURCE`).

## Dedicated-server smoke test

Started Minecraft 1.21.1 / Fabric 0.16.14 with the mod in a local, loopback-only test world. All five entity identifiers successfully spawned:

```text
Summoned new MZF / ELW — FL NT 10/18-01
Summoned new DLK 18 — FL NT 10/32-01
Summoned new LF 10 — FL NT 10/42-01
Summoned new TSF-W — FL NT 10/48-01
Summoned new MZB — FL NT 10/79-01
```

After ticking, `/data get entity` verified:

- DLK instantiated with `LadderDeployed:1b` reached `Ladder:1.0f`.
- MZB in the test pool remained afloat at Y ≈ 2.537, with water surface Y ≈ 2.889.
- MZF instantiated with `BlueLights:1b` retained `BlueLights:1b` in entity serialization.
- `save-all` and clean server shutdown completed.

The smoke test preceded the final cosmetic TSF shortening and fully qualified entity data-fixer identifier change; the final source was then clean-built with Gradle 8.14.6. Server logs included vanilla missing-data-fixer notices for custom entity types and an unavailable Mojang public-key request in the offline local test. Neither prevented world startup, spawning, ticking or saving. No claim is made that a world reload was tested.

## Resources

Parsed all nine JSON resources, decoded every PNG, and resolved every item-model texture reference. All five vehicle texture atlases are included, along with the original OGG siren, English/German translations and inventory icons. No external runtime model library is required.

## Still needs a graphical client playtest

Actual rendered appearance, camera position, keyboard driving, player mounting/dismounting, audible horn quality, perceived light timing, and multiple connected players were not exercised in a graphical Minecraft client. This environment had no graphical client session. Successful compilation and server tests are not a substitute for visual/gameplay acceptance. See README.md for explicit simulation/model limitations.
