
package dev.cameron2134.twitchapp.gui;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;


public class GraphicsPanel extends JPanel {
    
    BufferedImage img;
   

    public GraphicsPanel() {
        

        try {
            this.img = ImageIO.read(new File("res/img/test.png"));
        } catch (IOException ex) {
            Logger.getLogger(GraphicsPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    

  @Override
  protected void paintComponent(Graphics g) {

    super.paintComponent(g);
        g.drawImage(img, 0, 0, null);
}
    
    
    
}
