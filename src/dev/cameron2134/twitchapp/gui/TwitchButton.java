
package dev.cameron2134.twitchapp.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicButtonUI;


public class TwitchButton extends BasicButtonUI implements MouseListener  {
    
    private final Color base = new Color(100, 65, 165, 255);
    private final Color highlight = new Color(122, 101, 163, 255);
    
    private final Font f = new Font("Arial", Font.BOLD, 13);
    
    private boolean init;
    
    
    public TwitchButton() {
        
        this.init = true;
        
    }
    
    
    @Override
    public void paint(Graphics g, JComponent c) {
        AbstractButton b = (AbstractButton) c;
        ButtonModel model = b.getModel();
       
        super.paint(g, c);
        
        
        
        
        
        if (this.init) {
            c.addMouseListener(this);
            b.setBackground(base);
            b.setBorder(BorderFactory.createEtchedBorder());
            this.init = false;
        }
        
        b.setForeground(Color.WHITE);
        
        
        
        b.setRolloverEnabled(true);
        b.setMinimumSize(new Dimension(75, 25));
        b.setPreferredSize(new Dimension(125, 25));
        

        b.setFont(f);
    }
    
    
    
    @Override
    public void mouseEntered(MouseEvent e) {
        JComponent c = (JComponent) e.getComponent();

        c.setBackground(highlight);
        c.repaint();
      }

    @Override
    public void mouseClicked(MouseEvent e) {
        
    }

    @Override
    public void mousePressed(MouseEvent e) {
        JComponent c = (JComponent) e.getComponent();
        c.setBorder(BorderFactory.createLoweredSoftBevelBorder());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        JComponent c = (JComponent) e.getComponent();
        c.setBorder(BorderFactory.createEtchedBorder());
    }

    @Override
    public void mouseExited(MouseEvent e) {
        JComponent c = (JComponent) e.getComponent();
        c.setBackground(base);
        c.repaint();
    }
    
}
