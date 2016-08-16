
package dev.cameron2134.twitchapp;

import com.sun.jna.NativeLibrary;
import dev.cameron2134.twitchapp.gui.GUI;
import dev.cameron2134.twitchapp.livestreamer.LivestreamerSetup;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;



public class Launcher {

    
    
    public static void main(String args[]) {
        //new LivestreamerSetup().findInstallation();
        
        new GUI().setVisible(true);
        
        
    }
    
    
}
