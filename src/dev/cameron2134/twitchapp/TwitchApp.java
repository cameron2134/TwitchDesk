
package dev.cameron2134.twitchapp;

import com.mb3364.twitch.api.Twitch;
import com.mb3364.twitch.api.auth.Scopes;
import com.mb3364.twitch.api.handlers.ChannelResponseHandler;
import com.mb3364.twitch.api.handlers.StreamsResponseHandler;
import com.mb3364.twitch.api.handlers.TokenResponseHandler;
import com.mb3364.twitch.api.handlers.UserFollowsResponseHandler;
import com.mb3364.twitch.api.models.Channel;
import com.mb3364.twitch.api.models.Stream;
import com.mb3364.twitch.api.models.Token;
import com.mb3364.twitch.api.models.UserFollow;
import com.sun.jna.NativeLibrary;
import dev.cameron2134.twitchapp.gui.StreamUI;
import dev.cameron2134.twitchapp.livestreamer.Livestream;
import dev.cameron2134.twitchapp.livestreamer.LivestreamerSetup;
import dev.cameron2134.twitchapp.utils.IO;
import dev.cameron2134.twitchapp.video.VideoPlayer;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.apache.commons.lang3.exception.ExceptionUtils;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;


public class TwitchApp {

    private final File tokenFile = new File("res/data/token.cfg");
    
    private List<UserFollow> usersFollowing = new ArrayList<>();
    private List<Stream> usersLive = new ArrayList<>();
    
    private Twitch twitch;
    private AutoUpdater updater;
    
    private String username, status;
    
    private boolean requiresUpdates, dataReady;
    
    private StreamUI gui;
    private LivestreamerSetup liveSetup;
    private Livestream stream;
    private VideoPlayer player;
    
    
    
    /**
     * Creates a new TwitchApp object and initialises the API.
     * @param gui The StreamUI object to update.
     */
    public TwitchApp(StreamUI gui) {
        
        if (!new File("res").exists())
            new File("res").mkdir();
        
        if (!new File("res/data").exists())
            new File("res/data").mkdir();
        
        NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), "res/data/vlc");
        
        this.twitch = new Twitch();
        this.updater = new AutoUpdater(this, gui);
        this.liveSetup = new LivestreamerSetup();
        this.player = new VideoPlayer(gui);
        
        this.username = "N/A";
        this.status = "Generic Status";
        
        this.gui = gui;
        this.requiresUpdates = dataReady = false;

        authenticateUser();
        
        new Thread(this.updater).start();

    }
    
    
    
    
    /**
     * Authenticates the user with the Twitch API using the application's client ID and the users authentication token.
     */
    private void authenticateUser() {
        final String clientID = "a9xodw766xrik26uhddfmtmq7slsgbk";
        twitch.setClientId(clientID);
        
        // User has not authenticated/ran the application yet
        if (!tokenFile.exists()|| IO.isEmpty(tokenFile)) {

            URI callbackUri = null;
            
            try {
                callbackUri = new URI("http://127.0.0.1:23522/authorize.html");
            } 
            
            catch (URISyntaxException ex) {
                System.err.println(ex);
                IO.writeDebugLog(ExceptionUtils.getStackTrace(ex));
            }

            String auth = twitch.auth().getAuthenticationUrl(twitch.getClientId(), callbackUri, Scopes.USER_READ, Scopes.CHANNEL_READ, Scopes.CHANNEL_STREAM);
            URI authURL = null;
            
            try {
                authURL = new URI(auth);
            } 
            
            catch (URISyntaxException ex) {
                System.err.println(ex);
                IO.writeDebugLog(ExceptionUtils.getStackTrace(ex));
            }
            
            try {
                Desktop.getDesktop().browse(authURL);
            } 
            
            catch (IOException ex) {
                System.err.println(ex);
                IO.writeDebugLog(ExceptionUtils.getStackTrace(ex));
            }


            // Waits for authorization, hangs here until pass/fail
            boolean authSuccess = twitch.auth().awaitAccessToken();


            if (authSuccess) {
              String accessToken = twitch.auth().getAccessToken();
              IO.write(tokenFile, accessToken);

            } 

            else {
                System.err.println(twitch.auth().getAuthenticationError());
            }
            
        }
        
        
        // Obtain authentication data from file
        else {
            twitch.auth().setAccessToken(IO.read(tokenFile));
            System.out.println("Authentication token loaded...");
        }
        
        
    }
    
    
    
    // Load all the data from the API first, as this takes time and runs separately
    // Will prevent the app from doing anything until this data is loaded
    /**
     * Loads the users username, follows and live follows from the Twitch API.
     */
    public void loadData() {
                
        // Using the root of the API to obtain the username
        twitch.root().get(new TokenResponseHandler() {

            @Override
            public void onSuccess(Token token) {
                username = token.getUserName();
                
                System.out.println("Username retrieved...");
                
                // The full list of people the user follows
                twitch.users().getFollows(username, new UserFollowsResponseHandler(){

                    @Override
                    public void onSuccess(int i, List<UserFollow> list) {
                        usersFollowing = list;
                        System.out.println("Follower list retrieved...");
                        
                        // Doesnt need username, automatically detects who's online from who you follow
                        twitch.streams().getFollowed(new StreamsResponseHandler(){

                            @Override
                            public void onSuccess(int i, List<Stream> list) {
                                usersLive = list;
                                System.out.println("Live followers retrieved...");
                                dataReady = true;
                            }

                            @Override
                            public void onFailure(int i, String string, String string1) {
                                System.err.println(string);
                            }

                            @Override
                            public void onFailure(Throwable thrwbl) {
                                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                            }
                        });
                    }

                    @Override
                    public void onFailure(int i, String string, String string1) {
                        System.err.println(string);
                    }

                    @Override
                    public void onFailure(Throwable thrwbl) {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                });
            }

            @Override
            public void onFailure(int i, String string, String string1) {
                System.err.println(string);
            }

            @Override
            public void onFailure(Throwable thrwbl) {
                System.err.println(thrwbl);
            }
        
        });
        
        
        
        
        
        
        
    }
    
    
    
    
    // Updates GUI with the latest API data
    public void updateGUI() {
        
        gui.updateLiveFollowers();
        resetDataStatus();
    }
    
    
    
    /**
     * Initialises a new live stream on a separate thread.
     * @param url The URL of the streamer to start watching.
     */
    public void initStream(String url) {
        
        if (this.stream != null && stream.isActive())
            this.stream.endStream();
        
        this.liveSetup.setStreamURL(url);
        this.stream = new Livestream(this.gui, this.liveSetup.createCmd());
        new Thread(this.stream).start();
        
    }
    
    

    /**
     * Obtains the status of the current stream to display in the title bar.
     * @param url The URL of the streamer.
     * @return The streamers status.
     */
    public String findStreamerStatus(String url) {
        
        String[] temp = url.split("/");
        
        
        
        for (Stream stream : usersLive) {
            if (temp[3].equals(stream.getChannel().getName())) {
                this.status = stream.getChannel().getStatus();
            }
            
            // The streamer isnt in the users follows, so obtain from twitch api
            else if (stream.equals(usersLive.get(usersLive.size() - 1))) {
                
                // If the user is finding a stream that is not in their follows
                twitch.channels().get(temp[3], new ChannelResponseHandler() {
                    @Override
                    public void onSuccess(Channel chnl) {
                         status = chnl.getStatus();
                    }

                    @Override
                    public void onFailure(int i, String string, String string1) {
                        JOptionPane.showMessageDialog(null,
                            "Streamer does not exist or is not currently streaming.",
                            "Stream Error",
                            JOptionPane.ERROR_MESSAGE);
                    }

                    @Override
                    public void onFailure(Throwable thrwbl) {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                });
                
            }
        }
        
        try {
            // Wait for api to retrieve data - bad way to do on event thread, will change later
            Thread.sleep(1000);
        } 
        
        catch (InterruptedException ex) {
            Logger.getLogger(TwitchApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return this.status;
    }
    
    
    
    
    

    
    public void resetDataStatus() {
        dataReady = false;
    }
    
    
    public boolean isDataReady() {
        return dataReady;
    }
    
    
    
    
    public VideoPlayer getVideoPlayer() {
        return this.player;
    }
        
    
    // Return list of users follows who are currently streaming
    public List<Stream> getLiveFollowList() {
        return usersLive;
    }
    
    
    // Return list of everyone the user followa
    public List<UserFollow> getFollowList() {
        return usersFollowing;

    }
    
    
    
}
