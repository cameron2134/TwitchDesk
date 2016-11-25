
package dev.cameron2134.twitchapp.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicMenuItemUI;
import javax.swing.plaf.basic.BasicMenuUI;

public class TwitchMenu extends BasicMenuUI implements MouseListener {
    
    private final Font f = new Font("Arial", Font.BOLD, 13);
    private boolean init;
    
    public TwitchMenu() {
        this.init = true;
    }
    
    
    @Override
    public void paint(Graphics g, JComponent c) {
    
        super.paint(g, c);
        
        c.setFont(f);
        
        if (this.init) {
            c.addMouseListener(this);

            c.setBackground(new Color(100, 65, 165, 255));
            this.init = false;
        }
    }
    
    
    @Override
    protected void paintText(Graphics g, JMenuItem menuItem,
            Rectangle textRect, String text) {
        g.setColor(Color.WHITE);
        int w2 = menuItem.getBounds().width / 2;
        textRect.translate(w2 - textRect.width / 2, 0);
        super.paintText(g, menuItem, textRect, text);
    }
    

    @Override
    public void mouseClicked(MouseEvent e) {
      
    }

    @Override
    public void mousePressed(MouseEvent e) {
     
        JComponent c = (JComponent) e.getComponent();

        c.setBackground(new Color(122, 101, 163, 255));
        c.repaint();
        
    }

    @Override
    public void mouseReleased(MouseEvent e) {
       
        JComponent c = (JComponent) e.getComponent();

        c.setBackground(new Color(100, 65, 165, 255));
        c.repaint();
        
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        JComponent c = (JComponent) e.getComponent();

        c.setBackground(new Color(122, 101, 163, 255));
        c.repaint();
        
        //item.setSelected(true);
    }

    @Override
    public void mouseExited(MouseEvent e) {
      JComponent c = (JComponent) e.getComponent();

        c.setBackground(new Color(100, 65, 165, 255));
        c.repaint();
    }
    
    
}
