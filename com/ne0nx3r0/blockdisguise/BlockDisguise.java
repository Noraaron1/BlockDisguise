package com.ne0nx3r0.blockdisguise;

import com.ne0nx3r0.blockdisguise.api.BlockDisguiseApi;
import com.ne0nx3r0.blockdisguise.commands.BlockDisguiseCommandExecutor;
import com.ne0nx3r0.blockdisguise.disguise.DisguiseManager;
import com.ne0nx3r0.blockdisguise.listeners.BlockDisguisePlayerListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.h31ix.updater.Updater;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class BlockDisguise extends JavaPlugin implements BlockDisguiseApi
{
    public int MAX_UPDATE_DISTANCE;
    public boolean MAKE_PLAYERS_INVISIBLE;
    public boolean UNDISGUISE_ON_PVP;
    public DisguiseManager disguiseManager;
    public boolean UPDATE_AVAILABLE = false;
    public String UPDATE_NAME;
    public List<Integer> ALLOWED_BLOCKS;
    
    @Override
    public void onEnable()
    {
// Load config
        File configFile = new File(this.getDataFolder(), "config.yml");   
        
        if(!configFile.exists())
        {
            configFile.getParentFile().mkdirs();
            copy(this.getResource(configFile.getName()), configFile);
        }
        
        MAX_UPDATE_DISTANCE = getConfig().getInt("update-distance") ^ 2;
        MAKE_PLAYERS_INVISIBLE = getConfig().getBoolean("make-players-invisible",true);
        UNDISGUISE_ON_PVP = getConfig().getBoolean("undisguise-on-pvp",true);
        ALLOWED_BLOCKS = getConfig().getIntegerList("allowed-blocks");
        
// Setup managers
        this.disguiseManager = new DisguiseManager(this);

// Setup listeners
        Bukkit.getPluginManager().registerEvents(new BlockDisguisePlayerListener(this), this);
    
        Bukkit.getPluginCommand("bd").setExecutor(new BlockDisguiseCommandExecutor(this));
        
// Setup updater
        if(getConfig().getBoolean("notify-about-updates"))
        {
            Updater updater = new Updater(
                    this,
                    "block-disguise",
                    this.getFile(),
                    Updater.UpdateType.NO_DOWNLOAD,
                    false); // Start Updater but just do a version check

            UPDATE_AVAILABLE = updater.getResult() == Updater.UpdateResult.UPDATE_AVAILABLE;
            
            if(UPDATE_AVAILABLE)
            {
                UPDATE_NAME = updater.getLatestVersionString();
                
                getLogger().log(Level.INFO,"--------------------------------");
                getLogger().log(Level.INFO,"    An update is available:");
                getLogger().log(Level.INFO,"    "+UPDATE_NAME);
                getLogger().log(Level.INFO,"--------------------------------");
            }
        }
    }
    
//Copies files from inside the jar
    private void copy(InputStream in, File file) {
        OutputStream out = null;
        try {
            out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=in.read(buf))>0){
                out.write(buf,0,len);
            }
            out.close();
            in.close();
        }
        catch (IOException ex)
        {
            Logger.getLogger(BlockDisguise.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            try
            {
                out.close();
            }
            catch (IOException ex)
            {
                Logger.getLogger(BlockDisguise.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public boolean isDisguised(Player p)
    {
        return this.disguiseManager.isDisguised(p);
    }

    @Override
    public void disguisePlayer(Player p, Material material, byte blockData)
    {
        this.disguiseManager.disguise(p, material, blockData);
    }

    @Override
    public void undisguisePlayer(Player p)
    {
        this.disguiseManager.undisguise(p);
    }

    @Override
    public void disguisePlayer(Player p, Material material)
    {
        this.disguiseManager.disguise(p, material, (byte) 0);
    }
}
