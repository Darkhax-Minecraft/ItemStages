/*
--=[INFO]=--

Script Author: kd8lvt
Written: 11/5/2021
Last Tested Versions:
  CraftTweaker: 7.1.2.445
  GameStages: 7.3.12
  ItemStages: 3.0.9
  Minecraft: 1.16.5

This script automatically generates ItemStages from vanilla's Datapack Tags.
You can use something like OpenLoader to make a global datapack.
You can find documentation on Vanilla datapacks here: https://minecraft.fandom.com/wiki/Data_pack

Given the syntax for CraftTweaker / ItemStages doesn't change, this should work for any version 1.13 and above
Try making a tag of tags to keep your tag files clean and legible!

--=[BEGIN SCRIPT]=--
*/

//Import ItemStages' package
import mods.itemstages.ItemStages;

//Get the Item Tag manager
var manager = <tag:items:forge:logs>.getManager();

//Get all item tags
var tagList = manager.getAllTags();

//Look through all the Item tags
for tag in tagList {
    //Get the tag's resource location
    var resourceLoc = tag.getId();
    //If the resource location is in the "pack_tags" namespace and the path starts with "stages/main"
    if (resourceLoc.namespace == "pack_tags" && resourceLoc.path.startsWith("stages/main")) {
        //Set up the stage's name
        var stageString = "";
        //Split the path of the Tag's resource location into an array of the folders' names
        var arr = resourceLoc.path.split("/");
        //For every folder name in said array...
        for chr in arr {
            //Copy the folder's name
            var char1 = chr;
            //If the stage's name string is not empty...
            if (!stageString.isEmpty) {
                //Add an underscore to the beginning of the copied folder name
                char1 = "_"+char1; 
            }
            //Add the copied folder name to the stage's name
            stageString += char1;
        }
        //Restrict the tag to the stage's name that was built above
        ItemStages.restrict(tag,stageString);
        //Notify the log that the stage has been created, and how many items are locked behind it.
        print("Created stage "+stageString+" -- it has "+tag.elements.length as string+" items locked behind it!");
    }
}