package com.transcend.plugins.drmnativeaudio;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.PlaybackParameters;
import androidx.media3.common.Player;
import androidx.media3.common.util.Util;
import androidx.media3.exoplayer.ExoPlaybackException;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.drm.DrmSessionManager;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.pallycon.widevine.exception.PallyConException;
import com.pallycon.widevine.exception.PallyConLicenseServerException;
import com.pallycon.widevine.model.ContentData;
import com.pallycon.widevine.model.PallyConDrmConfigration;
import com.pallycon.widevine.model.PallyConEventListener;
import com.pallycon.widevine.sdk.PallyConWvSDK;



@CapacitorPlugin(name = "AudioDRM")

public class AudioDRMPlugin extends Plugin {

    MediaSource mediaSource = null;
    @SuppressLint("UnsafeOptInUsageError")
    private DefaultTrackSelector trackSelector;
    private Handler handler;
    private Runnable runnable;
    private AudioDRM implementation = new AudioDRM();
    PallyConWvSDK WVMAgent = null;
    private ExoPlayer player;
    //private PlaybackStateCompat.Builder stateBuilder;
    private NotificationManager notificationManager;
    private static final String CHANNEL_ID = "media_playback_channel";


    private PallyConEventListener drmListener = new PallyConEventListener() {
        @Override
        public void onFailed(@NonNull ContentData contentData, @Nullable PallyConLicenseServerException e) {
            JSObject ret = new JSObject();
            ret.put("message",e.message());
            notifyListeners("playerError",ret);
        }

        @Override
        public void onFailed(@NonNull ContentData contentData, @Nullable PallyConException e) {
            JSObject ret = new JSObject();
            ret.put("message",e.message());
            notifyListeners("playerError",ret);
        }

        @Override
        public void onPaused(@NonNull ContentData contentData) {

        }

        @Override
        public void onRemoved(@NonNull ContentData contentData) {

        }

        @Override
        public void onRestarting(@NonNull ContentData contentData) {

        }

        @Override
        public void onStopped(@NonNull ContentData contentData) {

        }

        @Override
        public void onProgress(@NonNull ContentData contentData, float v, long l) {

        }

        @Override
        public void onCompleted(@NonNull ContentData contentData) {

        }
    };

    @SuppressLint("UnsafeOptInUsageError")
    Player.Listener playerEventListener = new Player.Listener() {

        JSObject ret = new JSObject();
        @Override
        public void onPlayerError(PlaybackException error) {
            if (error.errorCode == ExoPlaybackException.TYPE_RENDERER) {
                ret.put("message",error.getLocalizedMessage());
                notifyListeners("playerError",ret);
            } else if (error.errorCode == ExoPlaybackException.TYPE_SOURCE) {
                ret.put("message",error.getLocalizedMessage());
                notifyListeners("playerError",ret);
            } else if (error.errorCode == ExoPlaybackException.TYPE_UNEXPECTED) {
                ret.put("message",error.getLocalizedMessage());
                notifyListeners("playerError",ret);
            } else {
                ret.put("message",error.getLocalizedMessage());
                notifyListeners("playerError",ret);
            }
        }


        public  void onPlaybackStateChanged(int playbackState)
        {
            JSObject ret = new JSObject();
            if(playbackState == Player.STATE_ENDED)
            {
                notifyListeners("soundEnded",null);
            }else if (playbackState == Player.STATE_BUFFERING)
            {
                notifyListeners("isBuffering",null);
            }else  if(playbackState == Player.STATE_READY)
            {
                ret.put("soundDuration",player.getDuration()/1000);
                notifyListeners("audioLoaded",ret);

            }
        }

    };

    @PluginMethod
    public void echo(PluginCall call) {
        String value = call.getString("value");

        JSObject ret = new JSObject();
        ret.put("value", implementation.echo(value));
        call.resolve(ret);
    }

    public void startPlaybackCheck()
    {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (player != null) {
                    if (!player.isPlaying() && player.getPlaybackState() == Player.STATE_READY) {
                        notifyListeners("isAudioPause", null);
                    }else if(player.isPlaying())
                    {
                        notifyListeners("isAudioPlaying", null);
                    }
                }
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(runnable);
    }

    @SuppressLint("UnsafeOptInUsageError")
    @PluginMethod
    public void loadPallyconSound(PluginCall call)
    {
        String audioUrl = call.getString("audioURL");
        String token = call.getString("token");

        if (Util.SDK_INT > 23) {

            try {
                PallyConDrmConfigration config = new PallyConDrmConfigration("USE5", "eyJkcm1fdHlwZSI6IldpZGV2aW5lIiwic2l0ZV9pZCI6IlVTRTUiLCJ1c2VyX2lkIjoie1wiY3RcIjpcInVvNmJQY05UMkdtbkVOMmMyNldmUUdVb015OTNxSmVIKzFBRFpQcThxdkk9XCIsXCJpdlwiOlwiOTA4ZWU2YWYwMGNkMGNkMjBiMzUyZWIzZmU4OWZkZTlcIixcInNcIjpcIjQxNDBkZWIxYmMyNzM2NjBcIn0iLCJjaWQiOiIxIiwicG9saWN5IjoiR2ZOZkpQbE1lZ1FoOTR4MlV6SUJEUkx4cjRhMklDOUJoWDFQRENHVU1QSHlXbmRGKzJ0VStKbG1HUVwvRXNBejdubUhXS2Y3U1V0MURpeTFON2huOXh3PT0iLCJ0aW1lc3RhbXAiOiIyMDI0LTA3LTE1VDA4OjQ1OjU0WiIsImhhc2giOiJcL0FhMGtoeEhMN2ROZzB1enpNVXhubWU5aElOUUxydDVOaDlCdXRRNjFJWT0iLCJyZXNwb25zZV9mb3JtYXQiOiJvcmlnaW5hbCIsImtleV9yb3RhdGlvbiI6ZmFsc2V9");
                ContentData content = new ContentData(audioUrl, config);
                WVMAgent = PallyConWvSDK.createPallyConWvSDK(getContext(),content);
                DrmSessionManager manager = WVMAgent.getDrmSessionManager();
                WVMAgent.setPallyConEventListener(drmListener);
                try {
                    mediaSource = WVMAgent.getMediaSource(manager);
                    trackSelector = new DefaultTrackSelector(getContext());
                } catch (PallyConException.ContentDataException e) {
                    e.printStackTrace();
                    return;
                } catch (PallyConException.DetectedDeviceTimeModifiedException e) {
                    e.printStackTrace();
                    return;
                }

                player = new ExoPlayer.Builder( getContext())
                        .setTrackSelector(trackSelector)
                        .build();
                player.setMediaSource(mediaSource);
                player.addListener(playerEventListener);
                player.setPlayWhenReady(true);

                player.prepare();
                startPlaybackCheck();
            }catch (Exception ex)
            {
                Toast.makeText(getActivity(),ex.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }


        }

    }

    @SuppressLint("UnsafeOptInUsageError")
    @PluginMethod
    public void pauseAudio(PluginCall call)
    {
        if(player !=null && player.isPlaying())
        {
            player.pause();
        }
        call.resolve();
    }

    @PluginMethod
    public void setAudioPlaybackRate(PluginCall call)
    {
        float speed = call.getFloat("speed");
        if(player != null && String.valueOf(speed) != null )
        {
            PlaybackParameters playbackParameters = new PlaybackParameters(speed);
            player.setPlaybackParameters(playbackParameters);
        }else {
            Toast.makeText(getContext(),"Cannot set playback rate",Toast.LENGTH_LONG).show();
        }
    }

    @PluginMethod
    public void playAudio(PluginCall call)
    {
        if(player !=null && !player.isPlaying())
        {
            player.play();
        }

        call.resolve();
    }

    @PluginMethod
    public void seekToTime(PluginCall call)
    {
        var speed = call.getInt("seekTime");
        if(player != null && String.valueOf(speed) != null)
        {
            player.seekTo(player.getCurrentMediaItemIndex(),speed.longValue() * 1000);
            call.resolve();
        }else
        {
            Toast.makeText(getContext(),"Seek failed due to internal error",Toast.LENGTH_LONG).show();
        }
    }

    @PluginMethod
    public void stopCurrentAudio(PluginCall call)
    {
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
        if (player != null) {
            player.release();
        }
    }

    @PluginMethod
    public void getCurrentTime(PluginCall call)
    {
      JSObject ret = new JSObject();
      ret.put("time",player.getCurrentPosition()/1000);
      call.resolve(ret);
    }

    @PluginMethod
    public void getPaused(PluginCall call)
    {
        JSObject ret = new JSObject();
        if(player != null)
        {
            if(player.isPlaying())
            {
                ret.put("paused",false);
                call.resolve(ret);
            }else
            {
                ret.put("paused",true);
                call.resolve(ret);
            }
        }else
        {
            //Error
        }
    }

}
