package com.transcend.plugins.drmnativeaudio;

import android.annotation.SuppressLint;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import java.util.List;


@CapacitorPlugin(name = "AudioDRM")

public class AudioDRMPlugin extends Plugin {

    @SuppressLint("UnsafeOptInUsageError")


    @PluginMethod
    public void echo(PluginCall call) {
        String value = call.getString("value");

        JSObject ret = new JSObject();
        ret.put("value", "");
        call.resolve(ret);
    }

    @SuppressLint("UnsafeOptInUsageError")
    @PluginMethod
    public void loadPallyconSound(PluginCall call)
    {

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


}
