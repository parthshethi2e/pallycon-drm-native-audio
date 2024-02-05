import { Component, OnInit } from '@angular/core';
import { AudioDRM } from 'drm-native-audio';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'example';

  ngOnInit(): void {
    AudioDRM.echo({value: 'print this'}).then(value => {
      console.log(value)
    })

  }
}
