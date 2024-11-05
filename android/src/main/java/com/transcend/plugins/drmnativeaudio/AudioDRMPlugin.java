package com.transcend.plugins.drmnativeaudio;

import static android.media.AudioAttributes.ALLOW_CAPTURE_BY_NONE;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.AudioAttributes;
import androidx.media3.common.C;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.PlaybackParameters;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.common.util.Util;
import androidx.media3.datasource.DataSource;
import androidx.media3.datasource.DefaultDataSource;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.datasource.HttpDataSource;
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
import java.util.List;


@CapacitorPlugin(name = "AudioDRM")

public class AudioDRMPlugin extends Plugin {

    @SuppressLint("UnsafeOptInUsageError")
    private DefaultTrackSelector trackSelector;
    private Handler handler;
    private Runnable runnable;
    private final AudioDRM implementation = new AudioDRM();
    PallyConWvSDK WVMAgent = null;
    private ExoPlayer player;
    private String userAgent;
    private DataSource.Factory mediaDataSourceFactory;



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

    @SuppressLint("UnsafeOptInUsageError")
    @Override
    public void load() {
        super.load();
        AppCompatActivity activity = getActivity();
        activity.runOnUiThread(() -> {
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
            StrictMode.ThreadPolicy pol = new StrictMode.ThreadPolicy.Builder().permitNetwork().build();
            StrictMode.setThreadPolicy(pol);

            userAgent = Util.getUserAgent(getContext(), "Transcend");
            mediaDataSourceFactory = buildDataSourceFactory();

        });
    }

    @PluginMethod
    public void echo(PluginCall call) {
        String value = call.getString("value");

        JSObject ret = new JSObject();
        ret.put("value", "");
        call.resolve(ret);
    }

    public void startPlaybackCheck()
    {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                checkForScreenRecordingApps(getContext());
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
                PallyConDrmConfigration config = new PallyConDrmConfigration("USE5", "eyJkcm1fdHlwZSI6IldpZGV2aW5lIiwic2l0ZV9pZCI6IlVTRTUiLCJ1c2VyX2lkIjoie1wiY3RcIjpcIjNpclNsXC9vTkVMNmU2OFptaVdXT2xxaDBTVVhWeEtTQXErTWxiaUk3dDlnPVwiLFwiaXZcIjpcImNlMmYwNWRlMWEwYzVlYzlhZDY2NTFhOThjNDQ2MDQyXCIsXCJzXCI6XCI1ZTVmYjY5OThiNWY2NjBhXCJ9IiwiY2lkIjoiMSIsInBvbGljeSI6IkdmTmZKUGxNZWdRaDk0eDJVeklCRFJMeHI0YTJJQzlCaFgxUERDR1VNUEh5V25kRisydFUrSmxtR1FcL0VzQXo3bm1IV0tmN1NVdDFEaXkxTjdobjl4dz09IiwidGltZXN0YW1wIjoiMjAyNC0wNy0yNFQwNTo1MDowMloiLCJoYXNoIjoiNlhvdXMwQVpIbDJmcVlJZlBnRDZvWFFTVlUwK0JKNzY1eERjTWYwbWl0dz0iLCJyZXNwb25zZV9mb3JtYXQiOiJvcmlnaW5hbCIsImtleV9yb3RhdGlvbiI6ZmFsc2V9");
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

                AudioManager audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    audioManager.setAllowedCapturePolicy(ALLOW_CAPTURE_BY_NONE);
                }

                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setUsage(C.USAGE_MEDIA)
                        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                        .setAllowedCapturePolicy(C.ALLOW_CAPTURE_BY_NONE)
                        .build();



                player = new ExoPlayer.Builder( getContext())
                        .setTrackSelector(trackSelector)
                        .build();
                player.setMediaSource(mediaSource);
                player.addListener(playerEventListener);
                player.setAudioAttributes(audioAttributes,true);
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

    }

    @PluginMethod
    public void setAudioPlaybackRate(PluginCall call)
    {

    }

    @PluginMethod
    public void playAudio(PluginCall call)
    {

    }

    @PluginMethod
    public void seekToTime(PluginCall call)
    {

    }

    @PluginMethod
    public void stopCurrentAudio(PluginCall call)
    {

    }

    @PluginMethod
    public void getCurrentTime(PluginCall call)
    {

    }

    @PluginMethod
    public void getPaused(PluginCall call)
    {

    }

    private DataSource.Factory buildDataSourceFactory() {
        HttpDataSource.Factory httpDataSourceFactory = buildHttpDataSourceFactory();

        return new DefaultDataSource.Factory(getContext(), httpDataSourceFactory);
    }

    @OptIn(markerClass = UnstableApi.class)
    private HttpDataSource.Factory buildHttpDataSourceFactory() {
        return new DefaultHttpDataSource.Factory()
                .setUserAgent(Util.getUserAgent(getContext(), "Transcend"));
    }

    private boolean isScreenRecordingAppRunning(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processes = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo process : processes) {
            if (process.processName.equals("com.google.android.media.effects")) {
                return true;
            }
        }
        return false;
    }

    private void checkForScreenRecordingApps(Context context) {
        if (isScreenRecordingAppRunning(context)) {
            Toast.makeText(context, "Screen recording is detected and not allowed", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }
    }
}
