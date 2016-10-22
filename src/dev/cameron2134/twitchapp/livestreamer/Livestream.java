
package dev.cameron2134.twitchapp.livestreamer;

import dev.cameron2134.twitchapp.gui.GUI;
import dev.cameron2134.twitchapp.utils.IO;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.swing.JOptionPane;
import org.apache.commons.lang3.exception.ExceptionUtils;


public class Livestream implements Runnable {

    // This class will manage the starting and stopping of the stream
    
    
    private String[] streamCmd;
    private ProcessBuilder pb;
    private Process process;
    private GUI gui;
    private String url;
    
    
    public Livestream(GUI gui, String[] streamCmd) {
        
        this.streamCmd = streamCmd;
        this.gui = gui;
    }
    
    
    
    
    
    public void startStream() {

        ProcessBuilder builder;
        
        
        try {
            builder = new ProcessBuilder(streamCmd);
            process = builder.start();

            
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while (process.isAlive()) {
                String output = in.readLine();

                url = "";
                
                if (output.contains("127.0.0.1")) {
                    String[] h = output.split(" ");
                    url = h[2];
                    gui.displayVideo(url);
                    
                }
                
                System.out.println(url);
            }
        } 
        
        catch (IOException ex) {
            System.err.println(ex);
            IO.writeDebugLog(ExceptionUtils.getStackTrace(ex));
        }
        
        catch (NullPointerException ex) {
            System.err.println(ex);
            IO.writeDebugLog(ExceptionUtils.getStackTrace(ex));

            JOptionPane.showMessageDialog(null,
                    "Streamer not found or not online",
                    "Error starting stream",
                    JOptionPane.ERROR_MESSAGE);
        }
        
    }
    
    
    
    
    
    public void endStream() {
        process.destroy();
        gui.stopVideo();
        
    }
    
    
    public boolean isActive() {
        return process.isAlive();
    }
    
    

    @Override
    public void run() {
        startStream();
    }
    
    
    
    
}
