package com.transcend.plugins.drmnativeaudio;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.os.Handler;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.PlaybackParameters;
import androidx.media3.common.Player;
import androidx.media3.common.util.Util;
import androidx.media3.exoplayer.ExoPlaybackException;
import androidx.media3.exoplayer.ExoPlayer;
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
        public void onFailed(@Nullable String currentUrl, @Nullable PallyConException e) {
            JSObject ret = new JSObject();
            ret.put("message",e.message());
            notifyListeners("playerError",ret);
        }

        @Override
        public void onFailed(@Nullable String currentUrl, @Nullable PallyConLicenseServerException e) {

            JSObject ret = new JSObject();
            ret.put("message",e.message());
            notifyListeners("playerError",ret);
            //System.out.println("45:" + e.message());
        }

        @Override
        public void onPaused(@Nullable String currentUrl) {

        }

        @Override
        public void onRemoved(@Nullable String currentUrl) {

        }

        @Override
        public void onRestarting(@Nullable String currentUrl) {

        }

        @Override
        public void onStopped(@Nullable String currentUrl) {

        }

        @Override
        public void onProgress(@Nullable String currentUrl, float percent, long downloadedBytes) {

        }

        @Override
        public void onCompleted(@Nullable String currentUrl) {

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
                PallyConDrmConfigration config = new PallyConDrmConfigration("USE5", token);

                ContentData content = new ContentData(audioUrl, "", config);
                WVMAgent = PallyConWvSDK.createPallyConWvSDK(getContext(),content);
                WVMAgent.setPallyConEventListener(drmListener);
                try {
                    mediaSource = WVMAgent.getMediaSource();
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
