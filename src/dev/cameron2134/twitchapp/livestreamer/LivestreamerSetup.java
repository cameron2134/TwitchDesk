
package dev.cameron2134.twitchapp.livestreamer;

import dev.cameron2134.twitchapp.utils.IO;
import java.io.File;
import javax.swing.JOptionPane;


public class LivestreamerSetup {

    private String installationPath = "";
    private String args = "--player-external-http";
    private String quality = "source";
    private String streamURL = "";
    
    private File livestreamerConfig = new File("res/data/livestreamer.cfg");
    
    
    // This should only run on first time setup, path should be stored in a config file for quick retrieval
    // Things like stream quality etc should also be stored in the config file
    
    public void findInstallation() {
        
        if (livestreamerConfig.exists() && !IO.isEmpty(livestreamerConfig)) {
            String[] temp = IO.readMultiple(livestreamerConfig);
            installationPath = temp[0];
            
            args = temp[1];
            
            if (!args.contains("--player-external-http"))
                args += " --player-external-http"; // This is always needed so never exclude it
            
            quality = temp[2];
            
        }
        
        else {
            
            // Attempt to automatically detect installation first
            
            if (new File("C:/Program Files (x86)/Livestreamer/livestreamer.exe").exists()) 
                installationPath = "C:/Program Files (x86)/livestreamer/livestreamer.exe";
            
            
            else {
                installationPath = JOptionPane.showInputDialog(null, "Failed to auto detect path. Please specify your Livestreamer installation path:","Setup", JOptionPane.INFORMATION_MESSAGE);

                if (!installationPath.contains("livestreamer.exe")) {
                    installationPath += "/livestreamer.exe";
                }
            }          
            
            IO.write(livestreamerConfig, "path=" + installationPath, "args=" + args, "quality=" + quality);
        }
       
                
    }
    
    
    
    
    public void setStreamURL(String streamURL) {
        this.streamURL = streamURL;
    }
    
    
    
    public String[] createCmd() {     
        return new String[] {installationPath, streamURL, quality, args};
    }
    

    
}
