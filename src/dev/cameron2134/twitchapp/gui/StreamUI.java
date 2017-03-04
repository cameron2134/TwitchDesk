
package dev.cameron2134.twitchapp.gui;

import com.mb3364.twitch.api.models.Stream;
import com.mb3364.twitch.api.models.UserFollow;
import dev.cameron2134.twitchapp.TwitchApp;
import dev.cameron2134.twitchapp.utils.IO;
import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.plaf.ScrollPaneUI;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.StopWatch;

/**
 * 
 * @author cameron2134 https://github.com/cameron2134/TwitchDesk
 * @version alpha v0.4.1
 */
public class StreamUI extends GUI {

    private final String VERSION = "TwitchDesk Alpha v0.4.1";
    private final TwitchApp app;
    
    private String streamerLink;
    private boolean refreshed, pauseOnMin, minToTray, showNotifications;
    
    private JSplitPane splitPane;
    
    private TrayIcon trayIcon;
    
    private List<Stream> liveFollowList;
    private List<UserFollow> followList;
    private List<Stream> previousOnlineFollows;
    
    
    
    
    public static void main(String args[]) {
        new StreamUI().setVisible(true);
    }
    
    
    
    
    public StreamUI() {
        
        feel();
        initComponents();
        
        final ImageIcon frameIcon = new ImageIcon("res/img/twitch.png");
        this.setIconImage(frameIcon.getImage());
        
        
        if (new File("res/data/settings.cfg").exists() && !IO.isEmpty(new File("res/data/settings.cfg"))) {
            try {
                final String[] temp = IO.readMultiple(new File("res/data/settings.cfg"));

                this.pauseOnMin = Boolean.parseBoolean(temp[0]);
                this.minToTray = Boolean.parseBoolean(temp[1]);
                this.showNotifications = Boolean.parseBoolean(temp[2]);
            }
            
            catch (ArrayIndexOutOfBoundsException ex) {
                this.minToTray = this.pauseOnMin = this.showNotifications = false;
            }
        }
        
        else
            this.minToTray = this.pauseOnMin = this.showNotifications = false;
        
        
        if (this.minToTray)
            setUpTray();
        
        this.app = new TwitchApp(this);
        
        this.setTitle(VERSION);
        
        this.refreshed = false;

        applyCSS();
        createListeners();
        setUpInterface();
    }
    

    
    /**
     * Sets up the StreamUI interface with custom look and feel, sets the size of components.
     */
    private void setUpInterface() {
        
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


        btn_options.setUI(new TwitchButton());
        btn_refresh.setUI(new TwitchButton());
        
        videoPanel.add(app.getVideoPlayer().getPlayerComponent(), BorderLayout.CENTER);
        
        followPanel.setBackground(new Color(42, 35, 82, 255));
        jPanel1.setBackground(new Color(42, 35, 82, 255));
        jScrollPane1.getVerticalScrollBar().setUI(new TwitchScroll());
        jMenuBar1.setUI(new TwitchMenuBar());
        menu_follows.setUI(new TwitchMenu());
        menu_chat.setUI(new TwitchMenu());
        menu_findStreamer.setUI(new TwitchMenu());
        menu_About.setUI(new TwitchMenu());
        
    }

    
    /**
     * Apply CSS styling to the follower editor pane.
     */
    private void applyCSS() {      
        
        HTMLEditorKit kit = new HTMLEditorKit();
        streamerPane.setEditorKit(kit);
        
        StyleSheet css = kit.getStyleSheet();
        
        css.addRule("a {color: green; font-size: 14px; font-family: 'Lucida Sans Unicode', 'Lucida Grande', sans-serif; font-weight: bold; text-decoration: none;}");
        css.addRule(".streamGame {font-size: 10px; font-style: italic; font-family: 'Trebuchet MS', Helvetica, sans-serif;}");
        css.addRule(".offline {color: red;}");
        css.addRule("body {background-color:EBE0FF;}");
        css.addRule("h1 {font-size: 18px; font-family: 'Lucida Sans Unicode', 'Lucida Grande', sans-serif; font-weight: bold;}");
        
        
        streamerPane.setDocument(kit.createDefaultDocument());

    }
    
    
    /**
     * Set up the application task tray for when the application is minimized.
     */
    private void setUpTray() {

        //Check the SystemTray is supported
        if (!SystemTray.isSupported()) {
            System.err.println("SystemTray is not supported");
            return;
        }

        final PopupMenu popup = new PopupMenu();
        
        this.trayIcon = new TrayIcon(Toolkit.getDefaultToolkit().getImage("res/img/twitch.png"), "TwitchDesk", popup);
        this.trayIcon.setImageAutoSize(true);


        final MenuItem aboutItem = new MenuItem("Show");
        popup.add(aboutItem);


        // When the application is minimized, hide it in the tray.
        final SystemTray tray = SystemTray.getSystemTray();
        
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowIconified(WindowEvent e) {

                //Hides it from screen
                StreamUI.this.setState(StreamUI.ICONIFIED);
                //Hides it from taskbar and screen
                StreamUI.this.setVisible(false);

                try {
                    tray.add(trayIcon);
                    
                } 

                catch (AWTException ex) {
                    System.err.println("TrayIcon could not be added.");
                    IO.writeDebugLog(ExceptionUtils.getStackTrace(ex));
                }

            }
        });



        // Restore the frame from system tray
        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                StreamUI.this.setVisible(true);
                StreamUI.this.setState (StreamUI.this.NORMAL);


                tray.remove(trayIcon);

            }
        });
        
        

    }
    
    
    /**
     * Start a stream with the supplied streamer URL and set the stream status.
     * @param url The streamers URL to obtain the stream from.
     */
    private void initiateStream(String url) {
        
        System.out.println(url);
        this.streamerLink = url;
        this.app.initStream(this.streamerLink);

        this.setTitle(VERSION + " - " + this.app.findStreamerStatus(url));
        
    }
    
    
    /**
     * Get the latest follow lists from TwitchApp and update the editor pane.
     */
    public void updateLiveFollowers() {
        
        String liveFollows = "";
        String offlineFollows = "";
        
        final DecimalFormat df = new DecimalFormat("#,###");
        
        // Compare the follows online in the last update to see if anyone new is online to notify the user about
        previousOnlineFollows = liveFollowList;
        
        liveFollowList = app.getLiveFollowList();
        followList = app.getFollowList();
        

        // Show live follows
        if (!liveFollowList.isEmpty()) {
            String game;
            
            for (int i = 0; i < liveFollowList.size(); i++) {
                
                // If the person is live, don't show them in the offline list
                for (int x = 0; x < followList.size(); x++) {
                    if (followList.get(x).getChannel().getDisplayName().equals(liveFollowList.get(i).getChannel().getDisplayName())) 
                        followList.remove(x); 
                }
                
                if (liveFollowList.get(i).getChannel().getGame() == null)
                    game = "N/A";
                else
                    game = liveFollowList.get(i).getChannel().getGame();
                    
                liveFollows += "<a href='http://twitch.tv/" + liveFollowList.get(i).getChannel().getName() + "'>" + liveFollowList.get(i).getChannel().getDisplayName() + "</a> <br> <div class='streamGame'> Playing " + game + ", " + df.format(liveFollowList.get(i).getViewers()) + " viewers </div> <br><br>";

            }
            
            // If the app is in the system tray, throw a notification when a new streamer is online
            if (StreamUI.this.getState() == StreamUI.ICONIFIED && showNotifications) {
                
                for (Stream stream : liveFollowList) {
                    
                    if (!previousOnlineFollows.contains(stream)) {
                        displayBalloonMsg(stream.getChannel().getDisplayName() + " is now streaming.");
                    }
                }
            }
        }
        
        
        
        // Display offline follows
        for (int i = 0; i < followList.size(); i++) 
            offlineFollows += "<a class='offline' href='http://www.twitch.tv/" + followList.get(i).getChannel().getName() + "'>" + followList.get(i).getChannel().getDisplayName() + "</a> </div> <br> Offline <br><br>";
        
        
        
        String html = "<html> <body> <h1> You Follow </h1> <h2> Live: </h2>" + liveFollows + " <hr> <h2> Offline: </h2>" + offlineFollows + "</body> </html>";
        streamerPane.setText(html);
        
        
    }
    

    /**
     * Start the video player with the supplied URL and set the default volume level.
     * @param url The URL to play in the video player.
     */
    public void displayVideo(String url) {
        this.app.getVideoPlayer().startPlayer(url);
        
        // Initialise at volume 50
        this.app.getVideoPlayer().playerVolume(volumeSlider.getValue());
        
        
    }
    
    
    /**
     * Stops the video player.
     */
    public void stopVideo() {
        this.app.getVideoPlayer().stopPlayer();
    }
    

    public void displayBalloonMsg(String msg) {
        this.trayIcon.displayMessage("TwitchDesk", msg, TrayIcon.MessageType.INFO);
    }
    

    /**
     * Creates the listeners for all of the UI elements.
     */
    @Override
    public void createListeners() {
        
        final StopWatch timer = new StopWatch();
        final SetupGUI setupGUI = new SetupGUI(this, this.pauseOnMin, this.minToTray, this.showNotifications);
        
        btn_options.addActionListener((ActionEvent e) -> {
            
            if (!setupGUI.isVisible()) 
                setupGUI.setVisible(true);
        
        
            else 
                setupGUI.setVisible(false);
            
        });
        

        btn_refresh.addActionListener((ActionEvent e) -> {
            
            // Restrict manual refresh to every 5 seconds to prevent API spam
            if (timer.getTime() > 5000) {
                this.refreshed = false;
                timer.stop();
                timer.reset();
            }

            if (!this.refreshed) {
                timer.start();
                this.refreshed = true;

                this.app.loadData();
                this.app.updateGUI();

            }

            else
                System.out.println("Please wait more than 5 seconds before manually refreshing again");

            // Prevent the scroll bar from moving to the last position on refresh
            streamerPane.setCaretPosition(0);
            
        });
        
        
        // Video player volume
        volumeSlider.addChangeListener(new ChangeListener(){

            @Override
            public void stateChanged(ChangeEvent e) {
                app.getVideoPlayer().playerVolume(volumeSlider.getValue());
            }
        
        });
        
        
        
        menu_findStreamer.addMouseListener(new MouseAdapter() {
            
            @Override
            public void mouseClicked(MouseEvent e) {
            
                String streamerUsername = JOptionPane.showInputDialog(null,
                        "Enter a streamers username:",
                        "Find a Streamer",
                        JOptionPane.INFORMATION_MESSAGE);

                if (streamerUsername != null)
                    initiateStream("http://twitch.tv/" + streamerUsername.toLowerCase());

            }
        });
        
        
        
        menu_follows.addMouseListener(new MouseAdapter() {
            
            @Override
            public void mouseClicked(MouseEvent e) {
            
                if (menu_follows.getText().equals("Hide Side Panel")) {
            
                    menu_follows.setText("Show Side Panel");

                    splitPane.remove(followPanel);
                }

            else {
                menu_follows.setText("Hide Side Panel");

                splitPane.setRightComponent(followPanel);
            }

            }
        });
        
        
        
        menu_chat.addMouseListener(new MouseAdapter() {
            
            @Override
            public void mouseClicked(MouseEvent e) {
                
                try {
                    Desktop.getDesktop().browse(new URI(streamerLink + "/chat?popout="));
                } 

                catch (URISyntaxException | IOException ex) {
                    System.err.println(ex);
                    IO.writeDebugLog(ExceptionUtils.getStackTrace(ex));
                }
                
            }
            
        });
        
        
        menu_About.addMouseListener(new MouseAdapter() {
            
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("https://github.com/cameron2134/TwitchDesk"));
                } 

                catch (URISyntaxException | IOException ex) {
                    System.err.println(ex);
                    IO.writeDebugLog(ExceptionUtils.getStackTrace(ex));
                }
            }
            
        });
        
        
        // Follow pane hyperlinks
        streamerPane.addHyperlinkListener(new HyperlinkListener() {

            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    
                    initiateStream(e.getURL().toString());
                }
                
            }
        
        
        });
        
        
        
        
        if (pauseOnMin) {
            this.addWindowStateListener(new WindowAdapter() {
                @Override
                public void windowStateChanged(WindowEvent e) {

                    if ((e.getNewState() & StreamUI.this.ICONIFIED) == StreamUI.this.ICONIFIED) {
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
    
    public void notificationsEnabled(boolean value) {
        this.showNotifications = value;
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
                java.util.logging.Logger.getLogger(StreamUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            } catch (InstantiationException ex) {
                java.util.logging.Logger.getLogger(StreamUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                java.util.logging.Logger.getLogger(StreamUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            } catch (javax.swing.UnsupportedLookAndFeelException ex) {
                java.util.logging.Logger.getLogger(StreamUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
        menu_chat = new javax.swing.JMenu();
        menu_findStreamer = new javax.swing.JMenu();
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

        volumeSlider.setForeground(new java.awt.Color(255, 255, 255));
        volumeSlider.setMajorTickSpacing(25);
        volumeSlider.setPaintLabels(true);
        volumeSlider.setPaintTicks(true);

        btn_options.setText("Options");

        jLabel1.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Volume");

        btn_refresh.setText("Manual Refresh");

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

        menu_follows.setForeground(new java.awt.Color(255, 255, 255));
        menu_follows.setText("Hide Side Panel");
        menu_follows.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jMenuBar1.add(menu_follows);

        menu_chat.setForeground(new java.awt.Color(255, 255, 255));
        menu_chat.setText("Show Popout Chat");
        menu_chat.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jMenuBar1.add(menu_chat);

        menu_findStreamer.setForeground(new java.awt.Color(255, 255, 255));
        menu_findStreamer.setText("Find Streamer");
        menu_findStreamer.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jMenuBar1.add(menu_findStreamer);

        menu_About.setForeground(new java.awt.Color(255, 255, 255));
        menu_About.setText("About");
        menu_About.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
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
    private javax.swing.JMenu menu_chat;
    private javax.swing.JMenu menu_findStreamer;
    private javax.swing.JMenu menu_follows;
    private javax.swing.JEditorPane streamerPane;
    private javax.swing.JPanel videoPanel;
    private javax.swing.JSlider volumeSlider;
    // End of variables declaration//GEN-END:variables

}
