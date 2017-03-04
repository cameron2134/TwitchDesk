
package dev.cameron2134.twitchapp.livestreamer;

import dev.cameron2134.twitchapp.utils.IO;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;


public class StreamlinkSetup {

    private String installationPath = "";
    private String args = "--player-external-http";
    private String quality = "source";
    private String streamURL = "";
    
    private final File streamlinkConfig = new File("res/data/streamlink.cfg");
    
    
    
    
    public StreamlinkSetup() {
        // Load settings here or create them
        if (streamlinkConfig.exists() && !IO.isEmpty(streamlinkConfig)) {
            String[] temp = IO.readMultiple(streamlinkConfig);
            installationPath = temp[0];
            
            args = temp[1];
            
            if (!args.contains("--player-external-http"))
                args += " --player-external-http"; // This is always needed so never exclude it
            
            quality = temp[2];
        }
        
        else
            findInstallation(); 
        
    
        
        
    }
    
    
    
    
    /**
     * Attempt to find Streamlink on the users system. If that fails, prompt user for manual input of location.
     */
    private void findInstallation() {
        
        // Attempt to automatically detect installation first
        if (new File("C:\\Program Files (x86)\\Streamlink\\bin\\streamlink.exe").exists()) 
            installationPath = "C:\\Program Files (x86)\\Streamlink\\bin\\streamlink.exe";

        else {
            JOptionPane.showMessageDialog(null, "Streamlink could not be detected. Please select your installation directory.", "Streamlink Error", JOptionPane.ERROR_MESSAGE);
            
            final JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setApproveButtonToolTipText("Select your Streamlink folder");
            
            final int result = chooser.showDialog(chooser, "Select");
            
            if (result == JFileChooser.APPROVE_OPTION) 
                installationPath = chooser.getSelectedFile().toString();
            
            if (!installationPath.contains("bin")) {
                installationPath += "\\bin\\streamlink.exe";
            }
            
            else
                installationPath += "\\streamlink.exe";
        }          

        IO.write(streamlinkConfig, "path=" + installationPath, "args=" + args, "quality=" + quality);
        
       
                
    }
    
    
    /**
     * Builds the Streamlink command required to launch a stream.
     * @return The constructed command with arguments.
     */
    public String[] createCmd() {    
        
        // Since theres two different setup objects, one in GUI one in setupgui, make sure the one in GUI is up to date if any settings are changes
        String[] temp = IO.readMultiple(streamlinkConfig);
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
