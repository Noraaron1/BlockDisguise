package com.ne0nx3r0.blockdisguise.commands;

import com.ne0nx3r0.blockdisguise.BlockDisguise;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BlockDisguiseCommandExecutor implements CommandExecutor {
    private final BlockDisguise plugin;

    public BlockDisguiseCommandExecutor(BlockDisguise plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String alias, String[] args)
    {
        if (!(cs instanceof Player))
        {
            msg(cs,ChatColor.RED+"BlockDiguise cannot be run from the console.");
            
            return true;
        }
        
        if(cs.hasPermission("BlockDisguise.disguise"))
        {
            Player player = (Player) cs;
            
            if(args.length < 1)
            {
                if(plugin.disguiseManager.isDisguised(player))
                {
                    plugin.disguiseManager.undisguise(player);

                    msg(cs,"Undisguised");
                }
                else
                {
                    msg(cs,"Usage: /bd <blockId|blockName> [blockData]");
                }
                
                return true;
            }
            
            if(!plugin.disguiseManager.isDisguised(player))
            {
                Material material = null;
                
                try
                {
                    material = Material.getMaterial(Integer.parseInt(args[0]));
                }
                catch(Exception e)
                {
                    material = Material.matchMaterial(args[0]);
                }

                if(material == null || !material.isBlock())
                {
                    msg(cs,ChatColor.RED+"'"+args[0]+"' is not a valid block!");

                    msg(cs,"(hint: diamond_block vs diamond)");

                    return true;
                } 
                
                byte blockData = 0;
                
                if(args.length > 1)
                {
                    try
                    {
                        blockData = Byte.parseByte(args[1]);
                    }
                    catch(Exception e)
                    {              
                        msg(cs, "Usage: /bd <blockId|blockName> [blockData]");
                        
                        return true;
                    }
                }

                plugin.disguiseManager.disguise(player,material,blockData);
                
                msg(cs,"You are disguised as a "+material.name()+"!");

            }
        }
        else
        {
            msg(cs,ChatColor.RED+"You do not have permission to use /bd !");
        }
        
        return true;
    }
    
    public void msg(CommandSender cs, String sMessage)
    {
        cs.sendMessage(ChatColor.DARK_RED + "[BD] " + ChatColor.WHITE + sMessage);
    }    
}
