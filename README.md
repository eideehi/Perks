# Perks #

## NOTE ##
**This Mod is currently under development and may undergo significant changes and is not recommended for deployment on important save data.**


## Outline ##
Add various Perks to Minecraft. Perk is unlocked by various actions and can be learned by consuming Perk Experience (PXP). Once you learn Perk, you can use it by consuming Perk Energy (PE).


## Perk ##
**To learn Perk, operate from the dedicated GUI displayed with the "P" key.**

|Perk|Description|Unlock Condition|
|----|-----------|----------------|
|Breeder|If you have animal feed in your hand, it will automatically breeding grow-up animals within range.|<details>Breeding 512 grow-up animals.</details>|
|Cultivator|If you have a hoe in your hand, you will cultivate the land automatically.|<details>Cultivate 512 blocks.</details>|
|Feeder|If you have animal feed in your hand, it will automatically feeding child animals within range.|<details>Feeding 512 child animals.</details>|
|Furnaceman|Refining speed increases when around Furnace.|<details>Smelting 1024 items.</details>|
|Harvester|If harvestable crops except pumpkin and watermelon are with, they are automatically harvested.|<details>Harvest 512 crops.</details>|
|Intelligence|Reduces Perk usage costs|<details>Crafting 128 bookshelves.</details>|
|Iron Fist|Blocks can be destroyed efficiently with bare hands.|<details>Destroy 256 blocks without equipping tools.</details>|
|Obsidian Breaker|Obsidian destruction speed is increased.|<details>Destroy 128 obsidian.</details>|
|Polisher|When polished equipment with Grindstone, the finished product will retain its enchantment.|<details>Polish 512 times with Grindstone.</details>|
|Resident of End|Disables disadvantages related to End items.|<details>Stay 72,000 ticks at The End.</details>|
|Seeder|If you have seeds or crops in your hands that you can plant, except pumpkins and watermelons, you can plant them automatically on the farmland.|<details>Plant 512 seeds or crops.</details>|
|Spreader|If you have bone meal in your hand, it will be automatically used for crop.|<details>Use 512 bone meals.</details>|


## Perk Energy (PE) ##
Perk Energy is the energy required to use Perk, and the UI appears in the upper left corner of the screen. Energy recovers over time.
Also, the energy will increase or decrease depending on the unlocking of the perk and the level of the player.


## Perk Experience (PXP) ##
The Perk Experience is required when learning Perk. Currently, Perk Experience rises by getting the normal experience orb.


## Item ##

### Energy Drink ###
Recover 200 Perk Energy.

<img src="https://app.box.com/shared/static/rontsnhtei115vkg5j2fnotpnszxu0qi.png">

### Energy Drink Blue ###
Increases the base point of Perk Energy by 5, the increased value is lost when the player dies.

<img src="https://app.box.com/shared/static/lzeyjf997l6h0jp4zwtp49x5fvnobs2a.png">


## Command ##
In Perks Mod, some commands are added to verify operation in Creative mode.

|Name|Description|Command|
|----|-----------|-------|
|Unlock Perk|Instantly unlock Perk.|`/unlockPerk <target> <perk>`|
|Perk Experience|It is possible to add and set park experience points.|`/perkExperience <query> <target> <value>`|
|Remove Perk|You can reset the state of Perk.|`/removePerk <target> <perk>`|


## Config ##

### Client ###
Path: **.minecraft/config/perks-client.toml**
|Item|Description|
|----|-----------|
|perkEnergyUiX|Perk Energy UI display position X|
|perkEnergyUiY|Perk Energy UI display position Y|



## Languages ##

|Language|Translators|Status|
|--------|-----------|------|
|en_us|Translation Tools|Complete|
|ja_jp|EideeHi|Complete|
