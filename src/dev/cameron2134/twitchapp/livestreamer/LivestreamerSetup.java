
package dev.cameron2134.twitchapp.livestreamer;

import dev.cameron2134.twitchapp.utils.IO;
import java.io.File;
import javax.swing.JOptionPane;


public class LivestreamerSetup {

    private String installationPath = "";
    private String args = "--player-external-http";
    private String quality = "source";
    private String streamURL = "";
    
    private final File livestreamerConfig = new File("res/data/livestreamer.cfg");
    
    
    
    
    public LivestreamerSetup() {
        // Load settings here or create them
        if (livestreamerConfig.exists() && !IO.isEmpty(livestreamerConfig)) {
            String[] temp = IO.readMultiple(livestreamerConfig);
            installationPath = temp[0];
            
            args = temp[1];
            
            if (!args.contains("--player-external-http"))
                args += " --player-external-http"; // This is always needed so never exclude it
            
            quality = temp[2];
        }
        
        else
            findInstallation(); 
        
    
        
        
    }
    
    
    
    // This should only run on first time setup, path should be stored in a config file for quick retrieval
    // Things like stream quality etc should also be stored in the config file
    
    /**
     * Attempt to find Livestreamer on the users system. If that fails, prompt user for manual input of location.
     */
    private void findInstallation() {
        
 
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
    
    
    /**
     * Builds the Livesreamer command required to launch a stream.
     * @return The constructed command with arguments.
     */
    public String[] createCmd() {    
        
        // Since theres two different setup objects, one in GUI one in setupgui, make sure the one in GUI is up to date if any settings are changes
        String[] temp = IO.readMultiple(livestreamerConfig);
        installationPath = temp[0];

        args = temp[1];

        if (!args.contains("--player-external-http"))
            args += " --player-external-http"; // This is always needed so never exclude it

        quality = temp[2];
        
        return new String[] {installationPath, streamURL, quality, args};
    }
    
    
    
    
    
    public void setStreamURL(String streamURL) {
        this.streamURL = streamURL;
    }
    
    public void setLivestreamerPath(String installationPath) {
        this.installationPath = installationPath;
    }
    
    public void setArgs(String args) {
        this.args = args;
    }
    
    public void setQuality(String quality) {
        this.quality = quality;
    }
    
    
    
    
    

    
    
    public String getVLCPath() {
        return this.installationPath;
    }
    
    public String getArgs() {
        return this.args;
    }
    
    public String getQuality() {
        return this.quality;
    }
    
    public String getStreamURL() {
        return this.streamURL;
    }
    
}
