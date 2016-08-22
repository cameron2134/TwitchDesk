
package dev.cameron2134.twitchapp.gui;

import com.mb3364.twitch.api.models.Stream;
import com.mb3364.twitch.api.models.UserFollow;
import dev.cameron2134.twitchapp.TwitchApp;
import dev.cameron2134.twitchapp.utils.IO;
import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JSplitPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import org.apache.commons.lang3.time.StopWatch;

/**
 * 
 * @author cameron2134 https://github.com/cameron2134/TwitchDesk
 * @version alpha v0.2.1
 */
public class GUI extends javax.swing.JFrame {

    private final String VERSION = "TwitchDesk Alpha v0.2.1";
    
    private TwitchApp app;
    private SetupGUI setupGUI;
    
    private String url;
    private String streamerLink;
    private String refreshTime;
    
    private boolean refreshed, pauseOnMin, minToTray;
    
    private StopWatch timer;


    private JSplitPane splitPane;
    
    
    public GUI() {
        
        feel();
        initComponents();
        
        if (new File("res/data/settings.cfg").exists() && !IO.isEmpty(new File("res/data/settings.cfg"))) {
            String[] temp = IO.readMultiple(new File("res/data/settings.cfg"));
            pauseOnMin = Boolean.parseBoolean(temp[0]);
            
            minToTray = Boolean.parseBoolean(temp[1]);
        }
        
        else
            minToTray = pauseOnMin = false;
        
        
        if (minToTray)
            setUpTray();
        
        applyCSS();
        
        app = new TwitchApp(this);
        timer = new StopWatch();
        setupGUI = new SetupGUI(this, pauseOnMin, minToTray);
        
        refreshed = false;
        
        this.setTitle(VERSION);
        
        createListeners();
        
        this.setMinimumSize(this.getPreferredSize());
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, videoPanel, followPanel);
        // Video player gets priority over the extra space so the follow pane stays static
        splitPane.setResizeWeight(1);
        
        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(splitPane, BorderLayout.CENTER);

        followPanel.setMinimumSize(new Dimension(225, 602));
        followPanel.setPreferredSize(new Dimension(225, 602));
        
        videoPanel.setLayout(new BorderLayout());
        videoPanel.setMinimumSize(new Dimension(826, 4));
        videoPanel.setPreferredSize(new Dimension(826, 4));
        
        videoPanel.add(app.getVideoPlayer().getPlayerComponent(), BorderLayout.CENTER);
        
    }
    



    
    public void updateLiveFollowers() {
        
        String liveFollows = "";
        String offlineFollows = "";
        
        List<Stream> liveFollowList = app.getLiveFollowList();
        List<UserFollow> followList = app.getFollowList();

        
        // Show live follows
        if (!liveFollowList.isEmpty()) {
            for (int i = 0; i < liveFollowList.size(); i++) {
                
                // If the person is live, don't show them in the offline list  
                for (int x = 0; x < followList.size(); x++) {
                      
                    if (followList.get(x).getChannel().getDisplayName().equals(liveFollowList.get(i).getChannel().getDisplayName())) {
                        followList.remove(x);
                    }

                }
                
                liveFollows += "<a href='http://twitch.tv/" + liveFollowList.get(i).getChannel().getName() + "'>" + liveFollowList.get(i).getChannel().getDisplayName() + "</a> <br> <div class='streamGame'> Playing " + liveFollowList.get(i).getChannel().getGame() + ", " + liveFollowList.get(i).getViewers() + " viewers </div> <br><br>";
            }
        }
        
        
        // Display offline follows
        for (int i = 0; i < followList.size(); i++) {

            offlineFollows += "<a class='offline' href='http://www.twitch.tv/" + followList.get(i).getChannel().getName() + "'>" + followList.get(i).getChannel().getDisplayName() + "</a> </div> <br> Offline <br><br>";
        }
        
        
        String html = "<html> <body> <h1> You Follow </h1> <h2> Live: </h2>" + liveFollows + " <hr> <h2> Offline: </h2>" + offlineFollows + "</body> </html>";
        streamerPane.setText(html);
        
    }
    

    
    
    
    public void updateFeatGames() {
        
        //String html = "<iframe src='http://player.twitch.tv/?channel={CohhCarnage}' height='720' width='1280' frameborder='0' scrolling='no'allowfullscreen='true'></iframe>";
        //JWebBrowser x;
        //String html = "<html> <body> <h1> Featured Games </h1> </body> </html>";
        //mainPane.setText(html);
    }
    

    
    
    
    private void applyCSS() {      
        
        HTMLEditorKit kit = new HTMLEditorKit();
        streamerPane.setEditorKit(kit);
        //mainPane.setEditorKit(kit);
        
        StyleSheet css = kit.getStyleSheet();
        
        css.addRule("a {color: green; font-size: 14px; font-family: 'Lucida Sans Unicode', 'Lucida Grande', sans-serif; font-weight: bold; text-decoration: none;}");
        css.addRule(".streamGame {font-size: 10px; font-style: italic; font-family: 'Trebuchet MS', Helvetica, sans-serif;}");
        css.addRule(".offline {color: red;}");
        css.addRule("body {background-color:FFFDF0;}");
        css.addRule("h1 {font-size: 18px; font-family: 'Lucida Sans Unicode', 'Lucida Grande', sans-serif; font-weight: bold;}");
        
        
        streamerPane.setDocument(kit.createDefaultDocument());
        //mainPane.setDocument(kit.createDefaultDocument());
        
    }
    
    
    
    public void displayVideo(String url) {
        this.url = url;
        
        app.getVideoPlayer().startPlayer(url);
        
        // Initialise at volume 50
        app.getVideoPlayer().playerVolume(volumeSlider.getValue());
        
        
    }
    
    
    public void stopVideo() {
        app.getVideoPlayer().stopPlayer();
    }
    


    
    

    private void setUpTray() {
            
        Image img = Toolkit.getDefaultToolkit().getImage("res/img/twitch.png");

        //Check the SystemTray is supported
        if (!SystemTray.isSupported()) {
            System.err.println("SystemTray is not supported");
            return;
        }

        PopupMenu popup = new PopupMenu();
        TrayIcon trayIcon = new TrayIcon(img, "TwitchDesk", popup);
        trayIcon.setImageAutoSize(true);

        SystemTray tray = SystemTray.getSystemTray();


        MenuItem aboutItem = new MenuItem("Show");
        popup.add(aboutItem);



        // When the application is minimized, hide it in the tray.
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowIconified(WindowEvent e) {

                //Hides it from screen
                GUI.this.setState(GUI.ICONIFIED);
                //Hides it from taskbar and screen
                GUI.this.setVisible(false);

                try {
                    tray.add(trayIcon);
                } 

                catch (AWTException ex) {
                    System.err.println("TrayIcon could not be added.");
                }

            }
        });



        // Restore the frame from system tray
        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                GUI.this.setVisible(true);
                GUI.this.setState (GUI.this.NORMAL);


                tray.remove(trayIcon);

            }
        });
        
        

    }
    
    
    
    
    private void createListeners() {
        
        // Video player volume
        volumeSlider.addChangeListener(new ChangeListener(){

            @Override
            public void stateChanged(ChangeEvent e) {
                app.getVideoPlayer().playerVolume(volumeSlider.getValue());
            }
        
        });
        
        
        
        // Follow pane hyperlinks
        streamerPane.addHyperlinkListener(new HyperlinkListener() {

            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    
                    System.out.println(e.getURL());
                    streamerLink = e.getURL().toString();
                    app.initStream(streamerLink);
                    
                    GUI.this.setTitle(VERSION + " - " + app.findStreamerStatus(e.getURL().toString()));
                }
                
            }
        
        
        });
        
        
        
        
        if (pauseOnMin) {
            this.addWindowStateListener(new WindowAdapter() {
                @Override
                public void windowStateChanged(WindowEvent e) {

                    if ((e.getNewState() & GUI.this.ICONIFIED) == GUI.this.ICONIFIED) {
                        app.getVideoPlayer().pausePlayer();
                    }

                    else                
                        app.getVideoPlayer().resumePlayer();

                }


            });
        }

    }
    
    
    
    
    public void setMinToTray(boolean value) {
        this.minToTray = value;
    }
    
    public void setPauseOnMin(boolean value) {
        this.pauseOnMin = value;
    }
    
    
    public TwitchApp getApp() {
        return this.app;
    }
    
    public String getStreamerLink() {
        return this.streamerLink;
    }
    
    
   // <editor-fold defaultstate="collapsed" desc=" Look and Feel ">   
    private void feel() {

        try {
                for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        javax.swing.UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (ClassNotFoundException ex) {
                java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            } catch (InstantiationException ex) {
                java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            } catch (javax.swing.UnsupportedLookAndFeelException ex) {
                java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }

    }
// </editor-fold>


    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        contentPanel = new javax.swing.JPanel();
        videoPanel = new javax.swing.JPanel();
        followPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        streamerPane = new javax.swing.JEditorPane();
        jPanel1 = new javax.swing.JPanel();
        volumeSlider = new javax.swing.JSlider();
        btn_options = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        btn_refresh = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        menu_follows = new javax.swing.JMenu();
        menu_About = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        videoPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        javax.swing.GroupLayout videoPanelLayout = new javax.swing.GroupLayout(videoPanel);
        videoPanel.setLayout(videoPanelLayout);
        videoPanelLayout.setHorizontalGroup(
            videoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 822, Short.MAX_VALUE)
        );
        videoPanelLayout.setVerticalGroup(
            videoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        followPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        streamerPane.setEditable(false);
        streamerPane.setContentType("text/html"); // NOI18N
        streamerPane.setText("");
        jScrollPane1.setViewportView(streamerPane);

        volumeSlider.setMajorTickSpacing(25);
        volumeSlider.setMinorTickSpacing(15);
        volumeSlider.setPaintLabels(true);
        volumeSlider.setPaintTicks(true);

        btn_options.setText("Options");
        btn_options.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_optionsActionPerformed(evt);
            }
        });

        jLabel1.setText("Volume");

        btn_refresh.setText("Manual Refresh");
        btn_refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_refreshActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(volumeSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(92, 92, 92)
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btn_options)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btn_refresh)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_options)
                    .addComponent(btn_refresh))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(volumeSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );

        javax.swing.GroupLayout followPanelLayout = new javax.swing.GroupLayout(followPanel);
        followPanel.setLayout(followPanelLayout);
        followPanelLayout.setHorizontalGroup(
            followPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(followPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        followPanelLayout.setVerticalGroup(
            followPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(followPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 428, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout contentPanelLayout = new javax.swing.GroupLayout(contentPanel);
        contentPanel.setLayout(contentPanelLayout);
        contentPanelLayout.setHorizontalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(videoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(followPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        contentPanelLayout.setVerticalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(videoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(followPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        menu_follows.setText("Hide Side Panel");
        menu_follows.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                menu_followsMouseClicked(evt);
            }
        });
        jMenuBar1.add(menu_follows);

        menu_About.setText("About");
        menu_About.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                menu_AboutMouseClicked(evt);
            }
        });
        jMenuBar1.add(menu_About);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(contentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(contentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btn_optionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_optionsActionPerformed
        
        if (!setupGUI.isVisible()) 
            setupGUI.setVisible(true);
        
        
        else 
            setupGUI.setVisible(false);
        
    }//GEN-LAST:event_btn_optionsActionPerformed

    private void menu_followsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menu_followsMouseClicked
        if (menu_follows.getText().equals("Hide Side Panel")) {
            
            menu_follows.setText("Show Side Panel");
            
            splitPane.remove(followPanel);
        }
        
        else {
            menu_follows.setText("Hide Side Panel");
            
            splitPane.setRightComponent(followPanel);
        }
    }//GEN-LAST:event_menu_followsMouseClicked

    private void menu_AboutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menu_AboutMouseClicked
        try {
            Desktop.getDesktop().browse(new URI("https://github.com/cameron2134/TwitchDesk"));
        } 
        
        catch (URISyntaxException | IOException ex) {
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_menu_AboutMouseClicked

    private void btn_refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_refreshActionPerformed
        
        // Restrict manual refresh to every 5 seconds to prevent API spam
        if (timer.getTime() > 5000) {
            refreshed = false;
            timer.stop();
            timer.reset();
        }
        
        if (!refreshed) {
            timer.start();
            refreshed = true;
            
            app.loadData();
            app.updateGUI();
            
        }
        
        else
            System.out.println("Please wait more than 5 seconds before manually refreshing again");
        
        
    }//GEN-LAST:event_btn_refreshActionPerformed



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_options;
    private javax.swing.JButton btn_refresh;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JPanel followPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JMenu menu_About;
    private javax.swing.JMenu menu_follows;
    private javax.swing.JEditorPane streamerPane;
    private javax.swing.JPanel videoPanel;
    private javax.swing.JSlider volumeSlider;
    // End of variables declaration//GEN-END:variables

}
