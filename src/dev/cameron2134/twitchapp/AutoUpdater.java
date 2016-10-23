
package dev.cameron2134.twitchapp;

import dev.cameron2134.twitchapp.gui.StreamUI;
import dev.cameron2134.twitchapp.utils.IO;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.exception.ExceptionUtils;


public class AutoUpdater implements Runnable {

    // Auto refreshes API data every x minutes/seconds that the user specifies
    
    private TwitchApp app;
    private StreamUI gui;
    
    private boolean update;
    private final int UPDATE_TIME = 60000;
    
    
    public AutoUpdater(TwitchApp app, StreamUI gui) {
        
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
                    Thread.sleep(100);
                } 
                
                catch (InterruptedException ex) {
                    System.err.println(ex);
                    IO.writeDebugLog(ExceptionUtils.getStackTrace(ex));
                }
            }

            app.updateGUI();


            try {
                Thread.sleep(UPDATE_TIME);
            } 
            
            catch (InterruptedException ex) {
                System.err.println(ex);
                IO.writeDebugLog(ExceptionUtils.getStackTrace(ex));
            }
        }
    }
    
    
    
    public synchronized void stop() {
        
        update = false;
        
    }

}
