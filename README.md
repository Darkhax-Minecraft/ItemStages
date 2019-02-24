# Item Stages [![](http://cf.way2muchnoise.eu/280316.svg)](https://minecraft.curseforge.com/projects/item-stages) [![](http://cf.way2muchnoise.eu/versions/280316.svg)](https://minecraft.curseforge.com/projects/item-stages)

This mod is an addon for the [GameStages API](https://minecraft.curseforge.com/projects/game-stages). It allows for items and blocks to be placed into custom progression systems.  You should check out the GameStage API mod's description for more info. To give a brief run down, stages are parts of the progression system set up by the modpack or server. Stages are given to players through a command, which is typically ran by a questing mod, advancement, or even a Command Block.

[![Nodecraft](https://nodecraft.com/assets/images/logo-dark.png)](https://nodecraft.com/r/darkhax)    
This project is sponsored by Nodecraft. Use code [Darkhax](https://nodecraft.com/r/darkhax) for 30% off your first month of service!

## Setup

This mod uses [CraftTweaker](https://minecraft.curseforge.com/projects/crafttweaker) for configuration.

This mod adds one new ZenScript method for adding item stage restrictions. You can use a specific item/block id, or an ore dictionary entry. If an ore dictionary is used, all entries for that oredict will be restricted. `mods.ItemStages.addItemStage(String stage, Item/Block/OreDict);`

## Effects

When something is restricted by this mod, several things will happen to prevent the player from using the item. 

- Holding a restricted item will cause it to be dropped immediately.
- The tooltip will be replaced with a restricted message.
- Items will be hidden in JEI if JEI is installed. 
- More to come!

## Example Script

```
// Example Script

// Restricts access to stone.
mods.ItemStages.addItemStage("one", <minecraft:stone>);

// Restricts access to iron chestplate..
// Note: With JEI auto hiding, you will need to manually add another entry for the version of the item in JEI.
mods.ItemStages.addItemStage("one", <minecraft:iron_chestplate>);

// Restricts access to all level 5 enchantments.
// Note: With JEI auto hiding, you will need to manually add another entry for the version of the item in JEI.
mods.ItemStages.addItemStage("one", <minecraft:enchanted_book>.withTag({StoredEnchantments: [{lvl: 5 as short}]}));

// Restricts access to Potion of Harming II
mods.ItemStages.addItemStage("one", <minecraft:potion>.withTag({Potion: "minecraft:strong_harming"}));

// Restricts access to all dyes in the ore dictionary.
mods.ItemStages.addItemStage("one", <ore:dye>);

// Gives an item a different name if it is restricted
mods.ItemStages.setUnfamiliarName("Clump of Fur", <minecraft:wool:*>);

// Stages a liquid from JEI
mods.ItemStages.stageLiquid("one", <liquid:water>);

// Stages a tooltip line. All lines that start with target text.
mods.ItemStages.stageTooltip("one", "When");

// Stages a recipe category in JEI.
mods.ItemStages.stageRecipeCategory("eight", "minecraft.anvil");

// Stages an enchantment (in JEI)
mods.ItemStages.stageEnchant("one", <enchantment:minecraft:protection>);

// Stages an enchantment by level (in JEI)
mods.ItemStages.stageEnchantByLevel("one", <enchantment:minecraft:projectile_protection>.makeEnchantment(1));
```
