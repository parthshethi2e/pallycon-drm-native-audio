import {  WebPlugin } from '@capacitor/core';
import type { AudioDRMPlugin } from './definitions';

export class AudioDRMWeb extends WebPlugin implements AudioDRMPlugin {
  
  constructor() {
    super({
      name: 'AudioDRM',
      platforms: ['web'],
    });
  }
  async getPaused(): Promise<{ paused: boolean }> {
    console.warn('Library not supported to web');
    console.warn('Library not supported to web');
    return { paused:false }
  }
  async stopCurrentAudio(): Promise<void> {
    console.warn('Library not supported to web');
  }
  async seekToTime(_options: { seekTime: number; }): Promise<void> {
    console.warn('Library not supported to web');
  }
  async setAudioPlaybackRate(_options: { speed: number; }): Promise<void> {
    console.warn('Library not supported to web');
  }
  async loadAzureDRMSoundURL(_options: { audioURL: String; token: String; }): Promise<void> {
    console.warn('Library not supported to web');
  }
  async playAudio(): Promise<void> {
    console.warn('Library not supported to web');
  }


  async pauseAudio(): Promise<void> 
  {
    console.warn('Library not supported to web');
  }


}

  
 

