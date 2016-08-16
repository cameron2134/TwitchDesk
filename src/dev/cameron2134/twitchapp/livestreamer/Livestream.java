
package dev.cameron2134.twitchapp.livestreamer;

import dev.cameron2134.twitchapp.gui.GUI;
import dev.cameron2134.twitchapp.utils.IO;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.swing.JOptionPane;


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
        
        IO.log("[Info] Stream has started.");
        
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
                    gui.setURL(url);
                    
                }
                
                System.out.println(url);
            }
        } 
        
        catch (IOException ex) {
            IO.log("[Error] " + ex.toString());
            System.err.println(ex.toString());
        }
        
    }
    
    
    
    
    
    public void endStream() {
        IO.log("[Info] Stream has ended.");
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
