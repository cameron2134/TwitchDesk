
package dev.cameron2134.twitchapp;

import dev.cameron2134.twitchapp.gui.StreamUI;
import dev.cameron2134.twitchapp.utils.IO;
import org.apache.commons.lang3.exception.ExceptionUtils;


public class AutoUpdater implements Runnable {

    // Auto refreshes API data every x minutes/seconds that the user specifies
    private final int UPDATE_TIME = 60000;
    
    private final TwitchApp app;  
    
    
    public AutoUpdater(TwitchApp app, StreamUI gui) {
        this.app = app;
    }
    
    
    
    
    @Override
    public void run() {

        // No need for the auto updater to stop running
        while (true) {
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
    

}
