
package dev.cameron2134.twitchapp.video;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;


public class VideoPlayer {

    // Manages the VLCJ video player
    
    private EmbeddedMediaPlayerComponent player;
    
    
    
    public VideoPlayer() {
        
        player = new EmbeddedMediaPlayerComponent();
    
    
        player.getMediaPlayer().setEnableKeyInputHandling(false);
        player.getMediaPlayer().setEnableMouseInputHandling(false);
        player.getVideoSurface().addMouseListener(new MouseAdapter() {
        
            @Override
            public void mouseClicked(MouseEvent e) {
                
                if (player.getMediaPlayer().isPlaying())
                    player.getMediaPlayer().pause();
                else
                    player.getMediaPlayer().play();
                
            }
            
        });
        
    }
    
    
    
    
    public EmbeddedMediaPlayerComponent getVideoPlayer() {
        return this.player;
    }
    
    
    
    
    
}
