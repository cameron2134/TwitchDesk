
package dev.cameron2134.twitchapp.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class IO {
  
    private static File log = new File("res/data/log.txt");
    
    
    
    public static void write(File file, String... options) {

        BufferedWriter writer = null;
        
        try {
            
            writer = new BufferedWriter(new FileWriter(file));

            for (String option : options) {
                writer.write(option);
                writer.newLine();
            }
            
            

        } 
        
        catch (IOException ex) {
            System.out.println(ex.toString());
            
        } 
        
        finally {
            try {
                writer.close();
            }
            catch (Exception ex) {
                System.out.println(ex.toString());
            }
        }
        
    }
    
    

    
    
    
    
    public static boolean isEmpty(File file) {
        
        BufferedReader br = null;     
        
        try {
            br = new BufferedReader(new FileReader(file));
            
            if (br.readLine() == null) {
                return true;
            }   
            
        } 
        
        catch (FileNotFoundException ex) {
            System.out.println(ex.toString());
        } 
        
        catch (IOException ex) {
            System.out.println(ex.toString());
        } 
        
        finally {
            try {
                br.close();
            } 
            
            catch (IOException ex) {
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
            System.out.println(ex.toString());
        }
        
        
        finally {
            try {
                reader.close();
            }
            catch (Exception ex) {
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
            System.out.println(ex.toString());
        }
        
        
        finally {
            try {
                reader.close();
            }
            catch (Exception ex) {
                System.out.println(ex.toString());
            }
        }
        
        
        return fileList.toArray(new String[0]);
    }
    
    

    
    public static void writeDebugLog(String msg) {
        
        BufferedWriter writer = null;
        
        Date currDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        
        try {
            
            writer = new BufferedWriter(new FileWriter(log, true));

            writer.write("[ERROR]\n");
            writer.write("[" + dateFormat.format(currDate) + "]: " + msg);
            writer.write("[END ERROR]\n\n");

        } 
        
        catch (IOException ex) {
            System.out.println(ex.toString());
            
        } 
        
        finally {
            try {
                writer.close();
            }
            catch (Exception ex) {
                System.out.println(ex.toString());
            }
        }
        
    }
    
    
}
