
package dev.cameron2134.twitchapp.video;

import dev.cameron2134.twitchapp.gui.StreamUI;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;


public class VideoPlayer {

    // Manages the VLCJ video player
    
    private final EmbeddedMediaPlayerComponent player;
    
    
    
    public VideoPlayer(StreamUI gui) {
        
        player = new EmbeddedMediaPlayerComponent();
    
        player.getMediaPlayer().setEnableKeyInputHandling(false);
        player.getMediaPlayer().setEnableMouseInputHandling(false);
        
        player.getVideoSurface().addMouseListener(new MouseAdapter() {
        
            @Override
            public void mouseClicked(MouseEvent e) {
                
                if (player.getMediaPlayer().isPlaying())
                    pausePlayer();
                else
                    resumePlayer();
               
            }
            
        });
        
    }
    
    
    public void startPlayer(String url) {
        this.player.getMediaPlayer().playMedia(url);
    }
    
    public void stopPlayer() {
        this.player.getMediaPlayer().stop();
        
    }
    
    public void pausePlayer() {
        this.player.getMediaPlayer().pause();
    }
    
    public void resumePlayer() {
        this.player.getMediaPlayer().play();
    }
    
    
    
    public void playerVolume(int volume) {
        
        this.player.getMediaPlayer().setVolume(volume);
        
    }
    
    
    public void mutePlayer() {
        if (!this.player.getMediaPlayer().isMute())
            this.player.getMediaPlayer().mute(true);
        
        else
            this.player.getMediaPlayer().mute();
    }
    
    
    
    public EmbeddedMediaPlayerComponent getPlayerComponent() {
        return this.player;
    }
    
    
    
    
    
}
