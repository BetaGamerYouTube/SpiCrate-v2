# SpiCrate-v2
Minecraft Spigot Plugin: CaseOpening/Crates.
WARNING: This is an old project and officially discontinued.

## General Information & Features
- Requires MySQL Database (mysql.yml)
- 1.19 or newer (Could run on lower versions, not tested)
- Ingame Editor

## Command:
Permission: spicrate.admin
Aliases: spicrate, spi
- /crate addcrate <Player> <Crate> <Amount> - Add Crates to a player.
- /crate removecrate <Player> <Crate> <Amount> - Removes Crates from a player.
- /crate setcrate <Player> <Crate> <Amount> - Sets Crates for a player.
- /crate getcrates <Player> <Crate> - See how many Crates the player has.
- /crate edit <Crate> items - Edit the Items from the Crate.
- /crate edit <Crate> settings - Edit the settings from the items: Chance, Show Chance, Announce in Chat on Win
- /crate giveitem <Crate> <Player> <Config-ID> - Give the player a specific item from the Crate.
- /crate setlocation - Set the block location for the opening. (Player's location)
- /crate reload - Reload the plugin: Reload Configs, Reload Messages, Reconnect to database
- /crate plugin - Just some general Informations about the Plugin (/ver SpiCrates)
