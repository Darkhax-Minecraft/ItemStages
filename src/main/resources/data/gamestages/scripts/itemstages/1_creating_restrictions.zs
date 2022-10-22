// Item Stages allows various aspects of items to be locked behind Game Stage's
// progression flags. For example you can prevent a player from picking up an 
// item if they do not have the required stages.

// To use Item Stages in your script you must import the helper class. This 
// class contains many helper methods for creating restrictions. 
import mods.itemstages.ItemStages;

// The following helper creates a simple restriction for sticks that is lifted
// if the player has stage one. Restricted players will not be able to pick up
// sticks, hold them in the inventory, use them as weapons, etc.
// ItemStages.restrict(IIngredient toRestrict, String... requiredStages);
ItemStages.restrict(<item:minecraft:stick>, "ex_one");

// Restricting items one by one can be very time consuming and can negatively
// impact game performance. Because of this it is recommended to restrict as 
// many items with one restriction as possible. One way of doing this is by
// restricting an entire tag.
ItemStages.restrict(<tag:items:forge:cobblestone>, "ex_two");

// You can also create restrictions using custom item matching predicates. For
// example the following will stage any item with a furnace fuel burn time of 
// of 150 ticks, like most wooden slabs. 
ItemStages.restrict(stack => stack.burnTime == 150, "ex_three");

// There are also several prebuilt advanced conditions that are built directly
// into Item Stages for you.

// Restricts any item with a specific enchantment on it. In this case any item
// with the vanilla Sharpness enchantment will be restricted.
// ItemStages.restrict(Enchantment enchantment, String... requiredStages);
ItemStages.restrict(<enchantment:minecraft:sharpness>, "ex_four");

// Restricts any item with a specific item rarity. In this case any item
// with the epic rarity will be restricted.
// ItemStages.restrict(Rarity rarity, String... requiredStages);
import crafttweaker.api.item.property.Rarity;
ItemStages.restrict(Rarity.EPIC, "ex_four");

// You can also restrict items based on their owning mod ID. Keep in mind that 
// all items from that mod will be staged and you can not unstage or remove
// specific ones using this method.
// ItemStages.createModRestriction("minecraft", "ex_five");