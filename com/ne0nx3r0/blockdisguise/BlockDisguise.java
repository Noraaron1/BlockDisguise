package com.ne0nx3r0.blockdisguise;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class BlockDisguise extends JavaPlugin{     
    public static Map<String,Integer[]> enabledFor;
    public static ArrayList<String> pending;
    public static Map<String,Block> lastUpdate;   
    public static int MAX_UPDATE_DISTANCE;
    
    @Override
    public void onEnable(){    
        enabledFor = new HashMap<String,Integer[]>();
        pending    = new ArrayList<String>();
        lastUpdate = new HashMap<String,Block>();
        
//Load config
        File configFile = new File(this.getDataFolder(), "config.yml");   
        
        if(!configFile.exists()){
            configFile.getParentFile().mkdirs();
            copy(this.getResource(configFile.getName()), configFile);
        }
        
        MAX_UPDATE_DISTANCE = getConfig().getInt("update-distance") ^ 2;
        
// Setup listeners
        Bukkit.getPluginManager().registerEvents(new BlockDisguisePlayerListener(this), this);
    
        Bukkit.getPluginCommand("bd").setExecutor(new BlockDisguiseCommandExecutor(this));

// Setup scheduled task
        long lUpdateInverval = getConfig().getLong("update-interval");
        Bukkit.getScheduler().scheduleSyncRepeatingTask(
                this, 
                new hidePlayers(),
                lUpdateInverval,
                lUpdateInverval
        );
        
    }
    
     private static class hidePlayers implements Runnable{
        @Override
        public void run(){

            for(String sPlayer : pending){
                Player p = Bukkit.getPlayer(sPlayer);
                Integer[] iBlockData = enabledFor.get(sPlayer);

                if(p == null || iBlockData == null){
                    continue;
                }

                Block b = p.getLocation().getBlock();
                Material m = Material.getMaterial(iBlockData[0]);

                Block bUpdate = lastUpdate.get(p.getName());
                Location lUpdate = null;
                byte bdUpdate = 0;
                Material mUpdate = null;
                
                if(bUpdate != null){
                    lUpdate = bUpdate.getLocation();
                    bdUpdate = bUpdate.getData();
                    mUpdate = bUpdate.getType();
                }
                
                if(b != null && (bUpdate == null || !b.getLocation().equals(bUpdate.getLocation()))){
                    for(Player pHideFrom : p.getWorld().getPlayers()){
                       if(!p.getName().equals(pHideFrom.getName()) 
                       && p.getLocation().distanceSquared(pHideFrom.getLocation()) < MAX_UPDATE_DISTANCE){    

                            pHideFrom.sendBlockChange(b.getLocation(), m, iBlockData[1].byteValue());

                            if(bUpdate != null){
                                pHideFrom.sendBlockChange(lUpdate, mUpdate, bdUpdate);
                            }
                        }
                    }
                }
                lastUpdate.put(p.getName(),b.getLocation().getBlock());
            }
            pending.clear();
        }
    }
    
    @Override
    public void onDisable(){
    }

//generic wrapper to message a player
    public void msg(Player player, String sMessage) {
        player.sendMessage(ChatColor.DARK_RED + "[BD] " + ChatColor.WHITE + sMessage);
    }

//Generic wrappers for console messages
    public void log(Level level,String sMessage){
        if(!sMessage.equals(""))
            getLogger().log(level,sMessage);
    }
    public void log(String sMessage){
        log(Level.INFO,sMessage);
    }
    public void error(String sMessage){
        log(Level.WARNING,sMessage);
    }
    
//Copies files from inside the jar
    private void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=in.read(buf))>0){
                out.write(buf,0,len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}