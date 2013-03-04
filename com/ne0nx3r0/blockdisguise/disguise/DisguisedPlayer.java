package com.ne0nx3r0.blockdisguise.disguise;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class DisguisedPlayer
{
    final Material material;
    final byte blockData;
    Block lastBlock;
    final String playerName;

    DisguisedPlayer(String sPlayerName,Material material, byte blockData)
    {
        this.playerName = sPlayerName;
        this.material = material;
        this.blockData = blockData;
    }
}
