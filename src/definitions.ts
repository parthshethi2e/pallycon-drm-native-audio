import { PluginListenerHandle } from "@capacitor/core";

export interface AudioDRMPlugin {
  loadAzureDRMSoundURL(options: { audioURL:String,token:String,notificationThumbnail:String,title:String, }):Promise<void>;
  pauseAudio(options: { value: string }): Promise<{ value: string }>;
  setAudioPlaybackRate(options: { speed: number }): Promise<void>;
  seekToTime(options: { seekTime: number }): Promise<void>;
  stopCurrentAudio(): Promise<void>;
  addListener(eventName: 'soundEnded', listenerFunc: () => void): Promise<PluginListenerHandle>;
  addListener(eventName: 'isBuffering', listenerFunc: () => void): Promise<PluginListenerHandle>;
  addListener(eventName: 'audioLoaded', listenerFunc: (soundDuration: number) => void): Promise<PluginListenerHandle>;
  addListener(eventName: 'NotificationPreviousCalled', listenerFunc: () => void): Promise<PluginListenerHandle>;
  addListener(eventName: 'NotificationNextCalled', listenerFunc: () => void): Promise<PluginListenerHandle>;
  addListener(eventName: 'timeUpdate', listenerFunc: (currentTime: number) => void): Promise<PluginListenerHandle>;
  addListener(eventName: 'playerError', listenerFunc: (error: any) => void): Promise<PluginListenerHandle>;
  playAudio(): Promise<void>;
  // getSoundDuration(): Promise<{duration: number}>;

}



