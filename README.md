# Netlv - A Minecraft AntiCheat Plugin (Abandoned)

This is an abandoned project that I made to refresh my Java skills, and as a break from python and aspx. 

It's not meant for actual use and has a lot of false positives, especially in PvP combat because velocity from taking damage isn't accounted for.

## Features

* **Reach:** Detects players hitting from unusually long distances.
* **Hitbox:**  Analyzes hit locations for hitbox manipulation.
* **Movement:**  Monitors for flight, speed hacks, and other movement exploits.
* **Scaffold:** Detects suspicious block placement common in scaffold hacks.
* **Inventory Move:** Flags movement when interacting with inventories.
* **Phase:** Prevents phasing through blocks.
* **Invalid Pitch:** Flags impossible pitch values.
* **Jesus:** Detects walking on water or lava.
* **More:**  There are many more checks, some combined into single toggles. 

## Installation

You'll need to compile the plugin from source.

## Usage

* `/printreach <player>`: Prints a player's reach.
* `/identifiers`: Toggles identifier alerts.
* `/netlv`: Main command for settings and help.
* `/netlv toggledevmode`: Toggles development mode.
* `/netlv setcommand <command>`: Sets a command to run on alert triggers.
* `/netlv setbroadcastmessage <message>`: Sets a broadcast message.
* **Additional Commands:** More commands exist for testing; refer to the code.

## Disclaimer

This plugin is in early stages and will not be updated. It was made for experimentation with various formulas and calculations in Minecraft and is not suitable for production use. 
