# Perks #

## NOTE ##
**This Mod is currently under development and may undergo significant changes and is not recommended for deployment on important save data.**


## Outline ##
Add various Perks to Minecraft. Perk is unlocked by various actions and can be learned by consuming Perk Experience (PXP). Once you learn Perk, you can use it by consuming Perk Energy (PE).


## Perk ##
**To learn Perk, operate from the dedicated GUI displayed with the "P" key. If you want to see the GUI tooltip, you can press the Shift key.**

|Perk|Description|Unlock Condition|
|----|-----------|----------------|
|Breeder|When you feed an adult animal, it automatically feeds the surrounding animals as well.|<details>Breeding 512 grow-up animals.</details>|
|Cultivator|When you use the Hoe to plow the ground, it automatically plows the ground around you as well.|<details>Cultivate 512 blocks.</details>|
|Feeder|When you feed a child's animal, it automatically feeds the child's animals around it.|<details>Feeding 512 child animals.</details>|
|Furnaceman|The refining speed of the nearby Furnace will be increased.|<details>Smelting 1024 items.</details>|
|Harvester|When the crop is harvested, it automatically harvests the surrounding crop.|<details>Harvest 512 crops.</details>|
|Intelligence|The cost of using Perk is reduced.|<details>Crafting 128 bookshelves.</details>|
|Iron Fist|When destroying blocks with your bare hands, you will be able to destroy them at the same speed as a steel tool.|<details>Destroy 256 blocks without equipping tools.</details>|
|Obsidian Breaker|The speed of obsidian destruction increases.|<details>Destroy 128 obsidian.</details>|
|Polisher|When using Grindstone to repair equipment, you will be able to do so without losing Enchantment.|<details>Polish 512 times with Grindstone.</details>|
|Resident of End|You can nullify the negative effects on items in The End.|<details>Stay 72,000 ticks at The End.</details>|
|Seeder|When you plant a seed or crop, it automatically plants around it.|<details>Plant 512 seeds or crops.</details>|
|Spreader|When bone meal is used, it automatically uses bone meal on surrounding crops.|<details>Use 512 bone meals.</details>|


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
Increases the maximum value of Perk Energy by 5 points. This effect will disappear after 30 minutes or when the player dies.

<img src="https://app.box.com/shared/static/lzeyjf997l6h0jp4zwtp49x5fvnobs2a.png">


## Command ##
In Perks Mod, some commands are added to verify operation in Creative mode.

|Name|Description|Command|
|----|-----------|-------|
|Perk|Comprehensive Perk operations.|`/perk <query> <target> <value>`|

#### Examples ####

- `/perk experience add EideeHi 100`
- `/perk experience set EideeHi 0`
- `/perk unlock EideeHi perks:breeder`
- `/perk remove EideeHi perks:cultivaor`

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
