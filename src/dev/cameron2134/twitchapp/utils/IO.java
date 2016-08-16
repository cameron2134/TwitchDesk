
package dev.cameron2134.twitchapp.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class IO {
  
    private static File log = new File("res/data/log.txt");
    
    public static void write(File file, String... options) {

        BufferedWriter writer = null;
        
        // Appends each new option to the options config file
        
        
        try {
            // The boolean value tells the filewriter to append to the text file rather than overwrite
            writer = new BufferedWriter(new FileWriter(file));

            for (String option : options) {
                writer.write(option);
                writer.newLine();
            }
            
            

        } 
        
        catch (IOException ex) {
            log("[Error] " + ex.toString());
            System.out.println(ex.toString());
            
        } 
        
        finally {
            try {
                writer.close();
            }
            catch (Exception ex) {
                log("[Error] " + ex.toString());
                System.out.println(ex.toString());
            }
        }
        
    }
    
    
    
    public static void log(String text) {
        java.util.Date date= new java.util.Date();
        System.out.println();
        BufferedWriter writer = null;
        
        // Appends each new option to the options config file
        
        
        try {
            // The boolean value tells the filewriter to append to the text file rather than overwrite
            writer = new BufferedWriter(new FileWriter(log, true));

            writer.write("[" + new Timestamp(date.getTime()) + "] " + text);
            writer.newLine();
        } 
        
        catch (IOException ex) {
            log("[Error] " + ex.toString());
            System.out.println(ex.toString());
        } 
        
        finally {
            try {
                writer.close();
            }
            catch (Exception ex) {
                log("[Error] " + ex.toString());
                System.out.println(ex.toString());
            }
        }
             
        
    }
    
    
    
    // Generic read from file
    public static boolean isEmpty(File file) {
        
        BufferedReader br = null;     
        
        try {
            br = new BufferedReader(new FileReader(file));
            
            if (br.readLine() == null) {
                return true;
            }   
            
        } 
        
        catch (FileNotFoundException ex) {
            log("[Error] " + ex.toString());
            System.out.println(ex.toString());
        } 
        
        catch (IOException ex) {
            log("[Error] " + ex.toString());
            System.out.println(ex.toString());
        } 
        
        finally {
            try {
                br.close();
            } 
            
            catch (IOException ex) {
                log("[Error] " + ex.toString());
                System.out.println(ex.toString());
            }
        }
        
        return false;
    }
    
    

    
    
    
    public static String read(File file) {
        BufferedReader reader = null;
        String text = "";
        
        try {
            reader = new BufferedReader(new FileReader(file));
            
            text = reader.readLine();

        }
        
        catch(IOException ex) {
            log("[Error] " + ex.toString());
            System.out.println(ex.toString());
        }
        
        
        finally {
            try {
                reader.close();
            }
            catch (Exception ex) {
                log("[Error] " + ex.toString());
                System.out.println(ex.toString());
            }
        }
        
        
        return text;
        
    }
    
    
    
    
    public static String[] readMultiple(File file) {
        
        List<String> fileList = new ArrayList<>();
        
        String line;
        
        BufferedReader reader = null;
        
        
        try {
            reader = new BufferedReader(new FileReader(file));
            
            while ((line = reader.readLine()) != null) {
                String[] temp = line.split("=");
                fileList.add(temp[1]);
            }
            

            
        }
        
        catch(IOException ex) {
            log("[Error] " + ex.toString());
            System.out.println(ex.toString());
        }
        
        
        finally {
            try {
                reader.close();
            }
            catch (Exception ex) {
                log("[Error] " + ex.toString());
                System.out.println(ex.toString());
            }
        }
        
        
        return fileList.toArray(new String[0]);
    }
    
    
    
    
    
    
    public static String numToGB(long num) {
        return String.valueOf(num / 1024 / 1024 / 1024);
    }
    
    public static String numToMB(long num) {
        return String.valueOf(num / 1024 / 1024);
    }
    
    
    public static int parseInt(String value) {
        return Integer.parseInt(value);
    }
    
    
}
