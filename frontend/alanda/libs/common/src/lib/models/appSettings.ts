import { InjectionToken, Injectable } from '@angular/core';

@Injectable()
export class AppSettings {
  API_ENDPOINT: string;
  DATE_FORMAT_STR?: string;
  DATE_FORMAT_STR_PRIME?: string;
  AVATAR_BASE_PATH: string;
  AVATAR_EXT: string;
  APP_NAME?: string;
}

export const APP_CONFIG = new InjectionToken<AppSettings>('AppConfig');
