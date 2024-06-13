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
        audioURL:"https://cdn.transcendstore.com/pallycon-audio/22/dash/stream.mpd",
        token:"eyJkcm1fdHlwZSI6IldpZGV2aW5lIiwic2l0ZV9pZCI6IlVTRTUiLCJ1c2VyX2lkIjoie1wiY3RcIjpcIjNpclNsXC9vTkVMNmU2OFptaVdXT2xxaDBTVVhWeEtTQXErTWxiaUk3dDlnPVwiLFwiaXZcIjpcImNlMmYwNWRlMWEwYzVlYzlhZDY2NTFhOThjNDQ2MDQyXCIsXCJzXCI6XCI1ZTVmYjY5OThiNWY2NjBhXCJ9IiwiY2lkIjoiMSIsInBvbGljeSI6IkdmTmZKUGxNZWdRaDk0eDJVeklCRFJMeHI0YTJJQzlCaFgxUERDR1VNUEh5V25kRisydFUrSmxtR1FcL0VzQXo3bm1IV0tmN1NVdDFEaXkxTjdobjl4dz09IiwidGltZXN0YW1wIjoiMjAyNC0wNi0xM1QwNDo0MjozMVoiLCJoYXNoIjoiSVJ3dnFlNnpUclwvcDVRVXBKTUJnUzhJRWw2QVN3U2xWQ0xSZ0tvWWpJUjQ9IiwicmVzcG9uc2VfZm9ybWF0Ijoib3JpZ2luYWwiLCJrZXlfcm90YXRpb24iOmZhbHNlfQ==",
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
