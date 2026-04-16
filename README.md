### About
Rail Destinations is a mod port of the [RailSwitch](https://civwiki.org/wiki/RailSwitch) plugin used on the Civ servers.

The mod allows detector rails to emit a restone signal only if a player in a minecart chose the right destination. It works by using the mechanics of a [T-junction](https://minecraft.wiki/w/Rail#Placement) and the interaction with a [Detector Rail](https://minecraft.wiki/w/Detector_Rail).

***

### Command Usage
Type `/dest <destination>` where `<destination>` is the place where you want to go. You can also chain destinations, `/dest <destination1> <destination2>` etc. This can be useful for taking a route via specific junctions; players will be routed towards signs that contain either `<destination1>` or `<destination2>`.

***

### Junction Setup
1. Place a sign one block above a detector rail before the T-junction.
2. The first line must be `[destination]`. You may also use `[!destination]`, which will activate the rail if a player's destination is not on the signs.
3. The other three lines can be destination names. Setting any line to `*` will activate the detector rail if the player has any destination set (or the opposite when using `[!destination]`).

**Since T-junctions follow the [south-east rule](https://minecraft.wiki/w/Rail#South-east_rule) an inverter (shown below) may need to be built.**

![Intersection](https://cdn.modrinth.com/data/UYOyMXzL/images/b8e3a6eb7b2f42eaa66991da61c06f1cb7c9865e.png)

![Top down view](https://cdn.modrinth.com/data/UYOyMXzL/images/a3b0e51b1495d6152d1a22009199ef563be6131c.png)

***

### Inverters
An inverter can be used to change the default direction of the T-junction.


![Redstone inverter](https://cdn.modrinth.com/data/UYOyMXzL/images/045dc8e82754a7a20c9181ecb286c1ddcf013250.png)

***

[![Curseforge](https://badges.penpow.dev/badges/available/curseforge/cozy-minimal.svg)](https://www.curseforge.com/minecraft/mc-mods/rail-destinations)
[![Modrinth](https://badges.penpow.dev/badges/available/modrinth/cozy-minimal.svg)](https://modrinth.com/mod/rail-destinations)
[![GitHub](https://badges.penpow.dev/badges/available/github/cozy-minimal.svg)](https://github.com/Identity-Theft/rail-destinations)
[![ko-fi](https://badges.penpow.dev/badges/donate/kofi-singular/cozy-minimal.svg)](https://ko-fi.com/identitytheft)
