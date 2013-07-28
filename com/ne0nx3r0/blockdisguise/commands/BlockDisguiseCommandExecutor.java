package com.ne0nx3r0.blockdisguise.commands;

import com.ne0nx3r0.blockdisguise.BlockDisguise;
import org.bukkit.Bukkit;
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
        if(cs instanceof Player && !cs.hasPermission("BlockDisguise.disguise") && !cs.isOp())
        {
            msg(cs,ChatColor.RED+"You do not have permission to use /bd !");
            
            return true;
        }    
        
        Player playerToDisguise = null;
        
        if(cs instanceof Player)
        {
            playerToDisguise = (Player) cs;
        }
        
        if(args.length == 0)
        {
            if(plugin.disguiseManager.isDisguised(playerToDisguise))
            {
                plugin.disguiseManager.undisguise(playerToDisguise);

                msg(cs,"Undisguised!");
            }
            else
            {
                msg(cs,"Usage:");
                msg(cs,"- /bd <blockId|blockName> [blockData]");
                
                if(cs.hasPermission("BlockDisguise.disguiseOthers"))
                {
                    msg(cs,"- /bd <playerName> <blockId|blockName> [blockData]");
                }
            }

            return true;
        }  
        
        Material m = null;
        Byte mData = 0;
        
        if(args.length == 1)
        {            
            if(!(cs instanceof Player))
            {
                msg(cs,ChatColor.RED+"How would the console look as a block?");
                msg(cs,"");
                msg(cs,"Console usage:");
                msg(cs,"- /bd <playerName> <blockId|blockName> [blockData]");
                msg(cs,"");
                
                return true;
            }
            
            
            if((args[0].equalsIgnoreCase("undisguise") || args[0].equalsIgnoreCase("u")))
            {
                if(plugin.isDisguised(playerToDisguise))
                {
                    msg(cs,"Undisguised!");
                }
                else
                {
                    msg(cs,ChatColor.RED+"You are not disguised!");
                }

                return true;
            }
            
            m = this.getMaterial(args[0]);
        }
        else if(args.length == 2)
        {
            m = this.getMaterial(args[0]);
            
            if(m == null)
            {
                playerToDisguise = this.getPlayerByName(args[0]);
            
                if(playerToDisguise == null)
                {
                    msg(cs,ChatColor.RED+"Invalid block or player!");

                    return true;
                }
                
                if(args[1].equalsIgnoreCase("u") || args[1].equalsIgnoreCase("undisguise"))
                {
                    if(cs.hasPermission("BlockDisguise.disguiseOthers"))
                    {
                        if(plugin.disguiseManager.isDisguised(playerToDisguise))
                        {
                            plugin.disguiseManager.undisguise(playerToDisguise);

                            msg(cs,"Undisguised "+playerToDisguise.getName());
                            msg(playerToDisguise,"You have been undisguised!");
                        }
                        else
                        {
                            msg(cs,ChatColor.RED+playerToDisguise.getName()+" is not disguised!");
                        }
                    }
                    else
                    {
                        msg(cs,ChatColor.RED+"You cannot undisguise others!");
                    }
                    
                    return true;
                }
                else
                {
                    m = this.getMaterial(args[1]);
                }
            }
            else
            {
                try
                {
                    mData = Byte.parseByte(args[1]);
                }
                catch(Exception e)
                {
                    mData = null;
                }
            }
        }
        else if(args.length == 3)
        {
            playerToDisguise = this.getPlayerByName(args[0]);
            
            m = this.getMaterial(args[1]);
            
            try
            {
                mData = Byte.parseByte(args[2]);
            }
            catch(Exception e)
            {
                mData = null;
            }
        }
        
        // Verify valid blocks
        if(m == null || playerToDisguise == null)
        {
            msg(cs,ChatColor.RED+"Invalid command! (\"/bd\" for usage)");
            
            return true;
        }
        
        // Verify can disguise others if necessary
        if(cs != playerToDisguise && !cs.hasPermission("BlockDisguise.disguiseOthers"))
        {
            msg(cs,ChatColor.RED+"You do not have permission to disguise others!");
            
            return true;
        }
        
        // Verify material not blacklisted
        if(plugin.BLACKLISTED_BLOCKS.contains(m.getId())
        && !cs.hasPermission("BlockDisguise.allowAllBlocks"))
        {
            msg(cs,ChatColor.RED+"You are not allowed to disguise as '"+args[0]+"'!");

            return true;
        }

        plugin.disguiseManager.disguise(playerToDisguise,m,mData);

        msg(playerToDisguise,"You are now disguised as a "+m.name()+"!");
        
        if(!playerToDisguise.equals(cs))
        {
            msg(cs,"Disguised "+playerToDisguise.getName()+" as a "+m.name()+"!");
        }
        
        return true;
    }
    
    public Material getMaterial(String sMaterial)
    {
        Material material = null;

        try
        {
            material = Material.getMaterial(Integer.parseInt(sMaterial));
        }
        catch(NumberFormatException e)
        {
            material = Material.matchMaterial(sMaterial);
        }
        
        return material;
    }
    
    public Player getPlayerByName(String sPlayer)
    {
        sPlayer = sPlayer.toLowerCase();
        
        for(Player p : Bukkit.getOnlinePlayers())
        {
            if(p.getName().toLowerCase().contains(sPlayer))
            {
                return p;
            }
        }

        return null;
    }
    
    public void msg(CommandSender cs, String sMessage)
    {
        cs.sendMessage(ChatColor.DARK_RED + "[BD] " + ChatColor.WHITE + sMessage);
    }    
}
