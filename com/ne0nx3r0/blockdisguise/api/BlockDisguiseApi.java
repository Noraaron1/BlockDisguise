package com.ne0nx3r0.blockdisguise.api;

import org.bukkit.Material;
import org.bukkit.entity.Player;

public interface BlockDisguiseApi
{
    public boolean isDisguised(Player p);
    
    public void disguisePlayer(Player p, Material material, byte blockData);
    
    public void undisguisePlayer(Player p);
}
