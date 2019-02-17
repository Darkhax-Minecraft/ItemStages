package net.darkhax.itemstages.commands;

import mezz.jei.Internal;
import mezz.jei.api.recipe.IRecipeCategory;
import net.darkhax.bookshelf.command.Command;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandDumpJeiStages extends Command {
    
    @Override
    public String getName () {
        
        return "jeicategories";
    }
    
    @Override
    public int getRequiredPermissionLevel () {
        
        return 0;
    }
    
    @Override
    public String getUsage (ICommandSender sender) {
        
        return "/gamestage jeicategories";
    }
    
    @Override
    public void execute (MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        
        for (final IRecipeCategory<?> cetegory : Internal.getRuntime().getRecipeRegistry().getRecipeCategories()) {
            
            sender.sendMessage(new TextComponentString("Id: " + cetegory.getUid() + " Mod: " + cetegory.getModName() + " Title: " + cetegory.getTitle()));
        }
    }
}