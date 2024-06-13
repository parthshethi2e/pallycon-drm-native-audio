package com.transcend.plugins.drmnativeaudio;

import android.widget.Toast;

import androidx.media3.exoplayer.ExoPlayer;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.pallycon.widevine.sdk.PallyConWvSDK;


@CapacitorPlugin(name = "AudioDRM")

public class AudioDRMPlugin extends Plugin {

    private AudioDRM implementation = new AudioDRM();
    PallyConWvSDK WVMAgent = null;
    private ExoPlayer player;


    @PluginMethod
    public void echo(PluginCall call) {
        String value = call.getString("value");

        JSObject ret = new JSObject();
        ret.put("value", implementation.echo(value));
        call.resolve(ret);
    }

    @PluginMethod
    public void loadPallyconSound(PluginCall call)
    {
        System.out.println("Called");
        Toast.makeText(getContext(),"Called",Toast.LENGTH_LONG).show();
    }
}
