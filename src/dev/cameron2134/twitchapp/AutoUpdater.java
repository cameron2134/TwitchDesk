
package dev.cameron2134.twitchapp;

import dev.cameron2134.twitchapp.gui.GUI;
import dev.cameron2134.twitchapp.utils.IO;
import java.util.logging.Level;
import java.util.logging.Logger;


public class AutoUpdater implements Runnable {

    // Auto refreshes API data every x minutes/seconds that the user specifies
    
    private TwitchApp app;
    private GUI gui;
    
    private boolean update;
    
    
    public AutoUpdater(TwitchApp app, GUI gui) {
        
        this.app = app;
        this.gui = gui;
        
        this.update = true;
    }
    
    
    
    
    @Override
    public void run() {

        while (update) {
            app.loadData();
        
            while (!app.isDataReady()) {
                try {
                    Thread.sleep(500);
                } 
                
                catch (InterruptedException ex) {
                    IO.log("[Error] " + ex.toString());
                    System.err.println(ex.toString());
                }
            }

            gui.updateFeatGames();
            gui.updateLiveFollowers();
            app.resetDataStatus();


            try {
                Thread.sleep(60000);
            } 
            
            catch (InterruptedException ex) {
                IO.log("[Error] " + ex.toString());
                System.err.println(ex.toString());
            }
        }
    }
    
    
    
    public synchronized void stop() {
        
        update = false;
        
    }

}
