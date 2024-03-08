import { Component, OnInit } from '@angular/core';
import { AudioDRM } from 'drm-native-audio';
import { timer } from 'rxjs';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {

  currentTime = 0;
  soundDuration = 0;
  title = 'example';

  constructor()
  {
    AudioDRM.addListener('soundEnded', () => {
      console.log('Sound playback ended');
    });


  }

  ngOnInit(): void {

  }

  playDRMAudio():void
  {
    AudioDRM.loadPallyconSound(
      {
        audioURL:"https://bbttranscendstorage.blob.core.windows.net/pallycon-audio/3/hls/master.m3u8",
        token:"eyJkcm1fdHlwZSI6IkZhaXJwbGF5Iiwic2l0ZV9pZCI6IlVTRTUiLCJ1c2VyX2lkIjoidHJhbnNjZW5kIiwiY2lkIjoiMSIsInBvbGljeSI6IkdmTmZKUGxNZWdRaDk0eDJVeklCRFJMeHI0YTJJQzlCaFgxUERDR1VNUEh5V25kRisydFUrSmxtR1EvRXNBejdubUhXS2Y3U1V0MURpeTFON2huOXh3PT0iLCJ0aW1lc3RhbXAiOiIyMDI0LTAzLTA4VDEwOjEwOjE4WiIsImhhc2giOiJBZzR0TUtvSmMzZThBdjliTlV1NEo3RHhCdXlxdGpCMWVYUGU5ZW9tMWpjPSIsInJlc3BvbnNlX2Zvcm1hdCI6Im9yaWdpbmFsIiwia2V5X3JvdGF0aW9uIjpmYWxzZX0=",
        notificationThumbnail: "https://picsum.photos/200/300",
        title:"Bhagvad Gita",
        seekTime:60,
        contentId:"1",
        author:"Transcend"
      })

      AudioDRM.addListener('soundEnded', () => {
        console.log('Sound playback ended');
      });



      AudioDRM.addListener('notificationPreviousCalled', () => {
        console.log('NotificationPreviousCalled called ');
      });

      AudioDRM.addListener('notificationNextCalled', () => {
        console.log('NotificationNextCalled called ');
      });


        AudioDRM.addListener('timeUpdate', (info: any) => {
          this.currentTime = info.time;
        //  console.log('Time Update:', info.time);
      });

      AudioDRM.addListener('audioLoaded', (info: any) => {
        this.soundDuration = info.duration;
        console.log('Duration:', info.duration);
    });

      AudioDRM.addListener('isAudioPause',() => {
        console.log("Event audio is paused")
      })

      AudioDRM.addListener('isAudioPlaying',() => {
        console.log("Event audio is played")
      })

      AudioDRM.addListener('playerError', (error) => {
        console.error('AVPlayer Error:', error);
      });

  }


  async getPaused()
  {
    const result = await AudioDRM.getPaused();

    console.log("Audio Paused:"+ result.paused)
  }

  stopAudio()
  {
    AudioDRM.stopCurrentAudio()
  }

  seekToTime()
  {
    AudioDRM.seekToTime({seekTime:60})
  }

  setPlaybackRate()
  {
    AudioDRM.setAudioPlaybackRate({speed:2.0})
  }

  onPlayPause(): void {


   AudioDRM.addListener('playerError', (error) => {
    console.error('AVPlayer Error:', error);
  });


    AudioDRM.addListener('timeUpdate', (info: any) => {
      this.currentTime = info.time;
      console.log('Time Update:', info.time);
  });
  }

  play(): void
  {
    AudioDRM.playAudio()
  }

  pause(): void {
    AudioDRM.pauseAudio()
  }







}
