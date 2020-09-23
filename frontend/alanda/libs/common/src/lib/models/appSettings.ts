import { InjectionToken, Injectable } from '@angular/core';

@Injectable()
export class AppSettings {
  API_ENDPOINT: string;
  AVATAR_EXT: string;
  AVATAR_BASE_PATH: string;
  DATE_FORMAT: string;
  DATE_FORMAT_PRIME: string;
  APP_NAME?: string;
}

export const APP_CONFIG = new InjectionToken<AppSettings>('AppConfig');
