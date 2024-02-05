import { WebPlugin } from '@capacitor/core';

import type { AudioDRMPlugin } from './definitions';

export class AudioDRMWeb extends WebPlugin implements AudioDRMPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
