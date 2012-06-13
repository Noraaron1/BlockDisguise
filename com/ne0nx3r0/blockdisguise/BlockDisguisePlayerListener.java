package com.ne0nx3r0.blockdisguise;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BlockDisguisePlayerListener implements Listener{
    private BlockDisguise p;
    
    public BlockDisguisePlayerListener(BlockDisguise p){
        this.p = p;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent e){
        if(!BlockDisguise.enabledFor.isEmpty()
        && BlockDisguise.enabledFor.containsKey(e.getPlayer().getName())
        && !BlockDisguise.pending.contains(e.getPlayer().getName())){
            BlockDisguise.pending.add(e.getPlayer().getName());
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent e){
        for(String sPlayer : p.enabledFor.keySet()){
            e.getPlayer().hidePlayer(p.getServer().getPlayer(sPlayer));
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent e){
        BlockDisguise.lastUpdate.remove(e.getPlayer());
        BlockDisguise.enabledFor.remove(e.getPlayer().getName());
    }
    
}
