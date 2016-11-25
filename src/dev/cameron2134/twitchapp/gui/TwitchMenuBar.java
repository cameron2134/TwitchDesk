
package dev.cameron2134.twitchapp.gui;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicMenuBarUI;
import javax.swing.plaf.basic.BasicMenuUI;


public class TwitchMenuBar extends BasicMenuBarUI {
    
    
    @Override
    public void paint(Graphics g, JComponent c) {
    
        super.paint(g, c);
        
        g.setColor(new Color(100, 65, 165, 255));
        g.fillRect(0, 0, c.getWidth (), c.getHeight () );
        
    }
    
    
}
