
package dev.cameron2134.twitchapp.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JComponent;
import javax.swing.plaf.ScrollBarUI;
import javax.swing.plaf.basic.BasicScrollBarUI;


public class TwitchScroll extends BasicScrollBarUI {
    
    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        super.paintTrack(g, c, trackBounds);
        
        g.setColor(new Color(100, 65, 165, 255));
        g.fillRect(trackBounds.width / 2, trackBounds.y, 3, trackBounds.height);
        
        if (this.trackHighlight == BasicScrollBarUI.DECREASE_HIGHLIGHT) {
            this.paintDecreaseHighlight(g);
        } 
        
        else if (this.trackHighlight == BasicScrollBarUI.INCREASE_HIGHLIGHT) {
            this.paintIncreaseHighlight(g);
        }
    } 

    
    
}
