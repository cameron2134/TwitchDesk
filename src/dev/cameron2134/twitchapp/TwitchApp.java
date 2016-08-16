
package dev.cameron2134.twitchapp;

import com.mb3364.twitch.api.Twitch;
import com.mb3364.twitch.api.auth.Scopes;
import com.mb3364.twitch.api.handlers.ChannelFollowsResponseHandler;
import com.mb3364.twitch.api.handlers.ChannelSubscriptionsResponseHandler;
import com.mb3364.twitch.api.handlers.StreamResponseHandler;
import com.mb3364.twitch.api.handlers.StreamsResponseHandler;
import com.mb3364.twitch.api.handlers.TokenResponseHandler;
import com.mb3364.twitch.api.handlers.UserFollowsResponseHandler;
import com.mb3364.twitch.api.models.ChannelFollow;
import com.mb3364.twitch.api.models.ChannelSubscription;
import com.mb3364.twitch.api.models.Stream;
import com.mb3364.twitch.api.models.Token;
import com.mb3364.twitch.api.models.UserFollow;
import com.mb3364.twitch.api.resources.StreamsResource;
import dev.cameron2134.twitchapp.gui.GUI;
import dev.cameron2134.twitchapp.utils.IO;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class TwitchApp {

    private final File settings = new File("res/data/settings.cfg");
    private final File tokenFile = new File("res/data/token.cfg");
    
    private List<UserFollow> usersFollowing = new ArrayList<>();
    private List<Stream> usersLive = new ArrayList<>();
    
    private Twitch twitch;
    private AutoUpdater updater;
    
    private String username;
    
    private boolean requiresUpdates, dataReady;
    
    private GUI gui;
    
    
    
    public TwitchApp(GUI gui) {
        
        if (!new File("res").exists())
            new File("res").mkdir();
        
        if (!new File("res/data").exists())
            new File("res/data").mkdir();
        
        this.twitch = new Twitch();
        this.updater = new AutoUpdater(this, gui);
        
        this.username = "N/A";
        this.gui = gui;
        this.requiresUpdates = dataReady = false;
        
        authenticateUser();
        
        new Thread(this.updater).start();

    }
    
    
    
    
    private void authenticateUser() {
        
        twitch.setClientId("a9xodw766xrik26uhddfmtmq7slsgbk");
        
        // User has not authenticated/ran the application yet
        if (!tokenFile.exists()|| IO.isEmpty(tokenFile)) {

            /* Specify your registered callback URI */
            URI callbackUri = null;
            
            try {
                callbackUri = new URI("http://127.0.0.1:23522/authorize.html");
            } 
            
            catch (URISyntaxException ex) {
                IO.log("[Error] " + ex.toString());
                System.err.println(ex.toString());
            }

            /* Get the authentication URL. Note: you will set the required scopes needed here. */
            String auth = twitch.auth().getAuthenticationUrl(twitch.getClientId(), callbackUri, Scopes.USER_READ, Scopes.CHANNEL_READ, Scopes.CHANNEL_STREAM);
            URI authURL = null;
            
            try {
                authURL = new URI(auth);
            } 
            
            catch (URISyntaxException ex) {
                IO.log("[Error] " + ex.toString());
                System.err.println(ex.toString());
            }
            
            try {
                /* Send the user to the webpage somehow so that they can authorize your application */
                Desktop.getDesktop().browse(authURL);
            } 
            
            catch (IOException ex) {
                IO.log("[Error] " + ex.toString());
                System.err.println(ex.toString());
            }


            /* Waits for the user to authorize or deny your application. Note: this function will block until a response is received! */
            boolean authSuccess = twitch.auth().awaitAccessToken();

            /* Check if authentication was successful */
            if (authSuccess) {
              /* The access token is automatically set in the Twitch object and will be sent with all further API requests! */
              String accessToken = twitch.auth().getAccessToken();
              IO.write(tokenFile, accessToken);

            } 

            else {
              /* Authentication failed, most likely because the user denied the authorization request */
                IO.log("[Error] " + twitch.auth().getAuthenticationError().toString());
                
                System.err.println(twitch.auth().getAuthenticationError());
            }
            
        }
        
        
        // Obtain authentication data from file
        else {
            twitch.auth().setAccessToken(IO.read(tokenFile));
            IO.log("[Info] Authentication token loaded...");
            System.out.println("Authentication token loaded...");
        }
        
        
    }
    
    
    
    // Load all the data from the API first, as this takes time and runs separately
    // Will prevent the app from doing anything until this data is loaded
    public void loadData() {
                
        // Using the root of the API to obtain the username
        twitch.root().get(new TokenResponseHandler() {

            @Override
            public void onSuccess(Token token) {
                username = token.getUserName();
                IO.log("[Info] Username retrieved...");
                System.out.println("Username retrieved...");
                
                // The full list of people the user follows
                twitch.users().getFollows(username, new UserFollowsResponseHandler(){

                    @Override
                    public void onSuccess(int i, List<UserFollow> list) {
                        usersFollowing = list;
                        IO.log("[Info] Follower list received...");
                        System.out.println("Follower list retrieved...");
                        
                        // Doesnt need username, automatically detects who's online from who you follow
                        twitch.streams().getFollowed(new StreamsResponseHandler(){

                            @Override
                            public void onSuccess(int i, List<Stream> list) {
                                usersLive = list;
                                IO.log("[Info] Live followers retrieved...");
                                System.out.println("Live followers retrieved...");
                                dataReady = true;
                            }

                            @Override
                            public void onFailure(int i, String string, String string1) {
                                IO.log("[Error] " + string);
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
                        IO.log("[Error] " + string);
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
                IO.log("[Error] " + string);
                System.err.println(string);
            }

            @Override
            public void onFailure(Throwable thrwbl) {
                System.err.println(thrwbl);
            }
        
        });
        
        
        
        
        
        
        
    }
    
    
    
    
    // Automatically update any data that may have changed, e.g. live follows
    public void refresh() {
        
        
    }
    
    
    

    
    public void resetDataStatus() {
        dataReady = false;
    }
    
    
    public boolean isDataReady() {
        return dataReady;
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
