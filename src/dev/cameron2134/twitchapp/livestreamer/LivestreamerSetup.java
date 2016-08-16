
package dev.cameron2134.twitchapp.livestreamer;

import dev.cameron2134.twitchapp.utils.IO;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.swing.JOptionPane;
import org.apache.commons.lang3.ArrayUtils;


public class LivestreamerSetup {

    private String installationPath = "D:\\Software\\Livestreamer\\livestreamer.exe";
    private String args = "--player-external-http";
    private String quality = "source";
    private String streamURL = "twitch.tv/lodeclaw";
    
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
            
            // Maybe later on make a thing to auto find the installation path
            installationPath += JOptionPane.showInputDialog(null, "Specify your Livestreamer installation path:","Setup", JOptionPane.INFORMATION_MESSAGE);

            if (!installationPath.contains("livestreamer.exe")) {
                installationPath += "\\livestreamer.exe";
            }

            IO.write(livestreamerConfig, installationPath, args, quality);
            
        }
        

        
        

        
		/*boolean found = false;
		try {
			if (checkLivestreamer("")) {
				LogManager.getLogger().debug("Livestreamer is nativly installed.");
				LIVESTREAMER_PATH = "";
				found = true;
			}
		} catch (Exception e) {
			String configPath = config.path;
			try {
				if (checkLivestreamer(configPath)) {
					LogManager.getLogger().debug("Livestreamer location: " + configPath);
					LIVESTREAMER_PATH = configPath;
					found = true;
				}
			} catch (Exception e1) {
				if (SystemInfo.getOS().equals(OperatingSystem.WIN)) {
					File file = new File("C:/Program Files (x86)/livestreamer/livestreamer.exe");
					if (file.exists()) {
						LogManager.getLogger().debug("[WIN] Livestreamer location: C:/Program Files (x86)/livestreamer/");
						LIVESTREAMER_PATH = "C:/Program Files (x86)/livestreamer/";
						found = true;
					}
				} else if (SystemInfo.getOS().equals(OperatingSystem.UNIX)) {
					File file = new File("/usr/local/bin/livestreamer");
					if (file.exists()) {
						LogManager.getLogger().debug("[UNIX] Livestreamer location: /usr/local/bin/");
						LIVESTREAMER_PATH = "/usr/local/bin/";
						found = true;
					}
				}
			}
		}
		if (!found) {
			LogManager.getLogger().debug("Livestreamer is not installed or can't be found on this system.");
			LogManager.getLogger().debug("Please set 'livestreamer=' variable in 'livestreamer.properties' to your livestreamer installation path.");
		}
		return found;*/
        
                
	}
    
    
    
    
    public void setStreamURL(String streamURL) {
        this.streamURL = streamURL;
    }
    
    
    
    public String[] createCmd() {
        
        
        
        String[] cmd = new String[4];
        
        cmd[0] = installationPath;
        cmd[1] = streamURL;
        cmd[2] = quality;
        cmd[3] = args;
        
        System.out.println("Command: " + installationPath + " " + streamURL + " " + quality + " " + args);
        return cmd;
    }
    

    
}
