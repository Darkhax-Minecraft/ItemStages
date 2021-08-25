import mods.itemstages.ItemStages;

// When a restriction is created an object is returned that allows you to 
// independently modify how that restriction should behave. This can be stored 
// for later use in your script 

// When a restriction is created a handle for that restriction is returned. 
// This handle allows you to independently configure how the restriction acts
// or even test items against the restriction in other parts of your script.
val one = ItemStages.restrict(<item:minecraft:gold_ingot>, "adv_one");

// By default restricted items will be named "Unfamiliar Item" however you can
// create a custom name to dispaly instead. You can even define a function that
// change the name based on the item being restricted. In this case we simply
// rename gold ingots to lemon bars temporarily.
// Restriction#hiddenName(MCTextComponent hiddenName);
// Restriction#hiddenName(Function<IItemStack, MCTextComponent> hiddenNameGenerator);
one.hiddenName("Lemon Bar");

// By default items are restricted to the greatest degree possible however you
// may want to ease certain parts of the restriction. For example you may want
// to prevent a player from using an item but allow them to keep it in their 
// inventory. This can be done by setting preventInventory to false.
val two = ItemStages.restrict(<item:minecraft:diamond>, "adv_two");
two.preventInventory(false);   // Allows item to be kept in inventories.
two.preventAttacking(false);   // Allows item to be used as a weapon.
two.preventPickup(false);      // Allows item to be picked up.
two.preventUsing(false);       // Allows item to be used (left/right click).
two.setHiddenInJEI(false);     // Allows item to be visible in JEI.

