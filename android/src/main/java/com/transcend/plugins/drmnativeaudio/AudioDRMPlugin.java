package com.transcend.plugins.drmnativeaudio;

import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
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

@UnstableApi @CapacitorPlugin(name = "AudioDRM")
public class AudioDRMPlugin extends Plugin {

    private AudioDRM implementation = new AudioDRM();
    private ExoPlayer player;
    private DefaultTrackSelector trackSelector;
    Player.Listener playerEventListener = new Player.Listener() {
        @Override
        public void onPlayerError(PlaybackException error) {
            String errorString;
            if (error.errorCode == ExoPlaybackException.TYPE_RENDERER) {
                errorString = error.getLocalizedMessage();
            } else if (error.errorCode == ExoPlaybackException.TYPE_SOURCE) {
                errorString = error.getLocalizedMessage();
            } else if (error.errorCode == ExoPlaybackException.TYPE_UNEXPECTED) {
                errorString = error.getLocalizedMessage();
            } else {
                errorString = error.getLocalizedMessage();
            }

            Toast.makeText(getContext(), errorString, Toast.LENGTH_LONG).show();
        }
    };
    PallyConWvSDK WVMAgent = null;
    public PallyConEventListener drmListener = new PallyConEventListener() {
        @Override
        public void onFailed(@Nullable String currentUrl, @Nullable PallyConException e) {
            Toast.makeText(getContext(), e.getMsg(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onFailed(@Nullable String currentUrl, @Nullable PallyConLicenseServerException e) {
            String message = String.format("%d, %s", e.errorCode(), e.body());
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
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

    @PluginMethod
    public void loadPallyconSound(PluginCall call)
    {
        String audioURL = call.getString("audioURL");
        String token = call.getString("token");
        Toast.makeText(getContext(),audioURL,Toast.LENGTH_LONG).show();
        initializePlayer(audioURL,token);
    }


    public void initializePlayer(String audioURL,String token)
    {
        PallyConDrmConfigration configuration = new PallyConDrmConfigration("USE5",token);
        ContentData content = new ContentData(audioURL,"",configuration);
        WVMAgent = PallyConWvSDK.createPallyConWvSDK(getContext(),content);
        WVMAgent.setPallyConEventListener(drmListener);
        MediaSource mediaSource = null;
        try {
            mediaSource = WVMAgent.getMediaSource();
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
    }


}
