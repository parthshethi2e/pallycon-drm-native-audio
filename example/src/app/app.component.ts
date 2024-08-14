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
    // AudioDRM.addListener('soundEnded', () => {
    //   console.log('Sound playback ended');
    // });


  }

  ngOnInit(): void {
  }

  playDRMAudio():void
  {
    AudioDRM.loadPallyconSound(
      {
        audioURL:"https://transcendstoredrm.blob.core.windows.net/pallycon-audio/100/hls/master.m3u8",
        token:"eyJkcm1fdHlwZSI6IkZhaXJwbGF5Iiwic2l0ZV9pZCI6IlVTRTUiLCJ1c2VyX2lkIjoie1wiY3RcIjpcIjNpclNsXC9vTkVMNmU2OFptaVdXT2xxaDBTVVhWeEtTQXErTWxiaUk3dDlnPVwiLFwiaXZcIjpcImNlMmYwNWRlMWEwYzVlYzlhZDY2NTFhOThjNDQ2MDQyXCIsXCJzXCI6XCI1ZTVmYjY5OThiNWY2NjBhXCJ9IiwiY2lkIjoiMSIsInBvbGljeSI6IkdmTmZKUGxNZWdRaDk0eDJVeklCRFJMeHI0YTJJQzlCaFgxUERDR1VNUEh5V25kRisydFUrSmxtR1FcL0VzQXo3bm1IV0tmN1NVdDFEaXkxTjdobjl4dz09IiwidGltZXN0YW1wIjoiMjAyNC0wOC0xNFQwNToxNzo0MloiLCJoYXNoIjoibjZRajZoK1d0SzB1dzBBNWFaM1FnR0ZnKzhMcVZtdldmWmdzdzRNWHF6WT0iLCJyZXNwb25zZV9mb3JtYXQiOiJvcmlnaW5hbCIsImtleV9yb3RhdGlvbiI6ZmFsc2V9",
        notificationThumbnail: "https://picsum.photos/200/300",
        title:"Bhagvad Gita",
        seekTime:60,
        contentId:"1",
        author:"Transcend",
        isSampleAudio:true,
        email:"parth.sheth58@gmail.com"
      })

    AudioDRM.addListener('soundEnded', () => {
      console.log('Sound playback ended');
    });



    //   AudioDRM.addListener('notificationPreviousCalled', () => {
    //     console.log('NotificationPreviousCalled called ');
    //   });

    //   AudioDRM.addListener('notificationNextCalled', () => {
    //     console.log('NotificationNextCalled called ');
    //   });



      AudioDRM.addListener('audioLoaded', (info: any) => {
        console.log('Duration:', info.soundDuration);
    });

      AudioDRM.addListener('isAudioPause',() => {
        console.log("Event audio is paused")
      })

      AudioDRM.addListener('isAudioPlaying',() => {
        console.log("Event audio is played")
      })

      AudioDRM.addListener('playerError', (message) => {
        console.error('AVPlayer Error:', message);
      });


  }


  async getPaused()
  {
    const result = await AudioDRM.getPaused();

    console.log("Audio Paused:"+ result.paused)
  }

  stopAudio()
  {
   // AudioDRM.stopCurrentAudio()
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


  //  AudioDRM.addListener('playerError', (error) => {
  //   console.error('AVPlayer Error:', error);
  // });


  
  }

  play(): void
  {
    AudioDRM.playAudio()
  }

  pause(): void {
     AudioDRM.pauseAudio()
  }


  getCurrentTime(): void {
    AudioDRM.getCurrentTime()
 }

 



}
