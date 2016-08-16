
package dev.cameron2134.twitchapp.gui;

import com.mb3364.twitch.api.models.Stream;
import com.mb3364.twitch.api.models.UserFollow;
import com.sun.jna.NativeLibrary;
import dev.cameron2134.twitchapp.TwitchApp;
import dev.cameron2134.twitchapp.livestreamer.Livestream;
import dev.cameron2134.twitchapp.livestreamer.LivestreamerSetup;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;


public class GUI extends javax.swing.JFrame {

    private TwitchApp app;
    private String url;

    
    
    public GUI() {
        
        feel();
        initComponents();
        
        applyCSS();
        app = new TwitchApp(this);

        videoPanel.setLayout(new BorderLayout());
        videoPanel.setPreferredSize(new Dimension(695, 4));
        videoPanel.add(app.getVideoPlayer(), BorderLayout.CENTER);
        

        //this.stream = new Livestream(this, liveSetup.createCmd());
        //new Thread(this.stream).start();
        
        //displayVideo();
    }
    
    
    
    public void setURL(String url) {
        this.url = url;
        displayVideo();
    }


    
    public void updateLiveFollowers() {
        
        String liveFollows = "";
        String offlineFollows = "";
        
        List<Stream> liveFollowList = app.getLiveFollowList();
        List<UserFollow> followList = app.getFollowList();

        System.out.println(liveFollowList.get(0).hashCode());
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
        
        
        
        
        streamerPane.addHyperlinkListener(new HyperlinkListener() {

            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    
                    System.out.println(e.getURL());
                    app.initStream(e.getURL().toString());
                }
                
            }
        
        
        });
        
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
    
    
    
    public void displayVideo() {
        
        
        // Remember to destroy the process before closing the app!!!
        app.getVideoPlayer().getMediaPlayer().playMedia(url);
        
    }
    
    
    public void stopVideo() {
        app.getVideoPlayer().getMediaPlayer().stop();
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

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        streamerPane = new javax.swing.JEditorPane();
        videoPanel = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("TwitchDesk");

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        streamerPane.setEditable(false);
        streamerPane.setContentType("text/html"); // NOI18N
        streamerPane.setText("");
        jScrollPane1.setViewportView(streamerPane);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 242, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 506, Short.MAX_VALUE)
                .addContainerGap())
        );

        videoPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        javax.swing.GroupLayout videoPanelLayout = new javax.swing.GroupLayout(videoPanel);
        videoPanel.setLayout(videoPanelLayout);
        videoPanelLayout.setHorizontalGroup(
            videoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 691, Short.MAX_VALUE)
        );
        videoPanelLayout.setVerticalGroup(
            videoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jMenu1.setText("Options");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Help");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(videoPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(videoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JEditorPane streamerPane;
    private javax.swing.JPanel videoPanel;
    // End of variables declaration//GEN-END:variables

}
