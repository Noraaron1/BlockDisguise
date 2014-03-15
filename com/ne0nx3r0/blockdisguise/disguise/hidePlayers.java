package com.ne0nx3r0.blockdisguise.disguise;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

class hidePlayers implements Runnable
{
    private final DisguiseManager dm;
    
    public hidePlayers(DisguiseManager dm)
    {
        this.dm = dm;
    }

    @Override
    public void run()
    {
        for(DisguisedPlayer disguisedPlayer : dm.players.values())
        {
            Player pDisguisedPlayer = dm.plugin.getServer().getPlayer(disguisedPlayer.playerName);

            if(pDisguisedPlayer == null)
            {
                continue;
            }

            if(disguisedPlayer.lastBlock == null)
            {
                for(Player pHideFrom : pDisguisedPlayer.getWorld().getPlayers())
                {
                    if(!pHideFrom.equals(pDisguisedPlayer))
                    {
                        pHideFrom.sendBlockChange(pDisguisedPlayer.getLocation(), disguisedPlayer.material, disguisedPlayer.blockData);
                    }
                }
                
                disguisedPlayer.lastBlock = pDisguisedPlayer.getLocation().getBlock();
                
                continue;
            }
                
            Block currentBlock = pDisguisedPlayer.getLocation().getBlock();
            
            if(currentBlock != null 
            && !currentBlock.equals(disguisedPlayer.lastBlock))
            {
                for(Player pHideFrom : pDisguisedPlayer.getWorld().getPlayers())
                {
                   if(!pDisguisedPlayer.equals(pHideFrom))
                   {    
                        if(pDisguisedPlayer.getLocation().distanceSquared(pHideFrom.getLocation()) < dm.plugin.MAX_UPDATE_DISTANCE)
                        {
                            pHideFrom.sendBlockChange(pDisguisedPlayer.getLocation(), disguisedPlayer.material, disguisedPlayer.blockData);
                        }

                        pHideFrom.sendBlockChange(
                                disguisedPlayer.lastBlock.getLocation(), 
                                disguisedPlayer.lastBlock.getType(), 
                                disguisedPlayer.lastBlock.getData());
                    }
                }

                disguisedPlayer.lastBlock = currentBlock;
            }
        }
    }
}