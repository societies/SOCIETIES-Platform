package dlr.stressrecognition.utils;

import java.util.HashSet;
import java.io.IOException;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;

/**
 * Utility class for playing notification sounds.
 * 
 * @author Michael Gross
 *
 */
public final class PlaySound {
    private static HashSet<MediaPlayer> mpSet = new HashSet<MediaPlayer>();

    public static void play(Context context) {
        try {
            MediaPlayer mp = new MediaPlayer();
            mpSet.add(mp);
            
    	    Uri alert  = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mp.setDataSource(context, alert);
            mp.setLooping(false);
            mp.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.stop();
                    mp.release();
                }
            });
            mp.prepare();
            mp.start();
        } catch (IOException e) {
        	e.printStackTrace();
        }
    }

    static void stop() {
        for (MediaPlayer mp : mpSet) {
            if (mp != null) {
                mp.stop();
                mp.release();
            }
        }
        mpSet.clear();
    }
}

