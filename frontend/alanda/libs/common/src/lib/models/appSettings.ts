import { InjectionToken, Injectable } from '@angular/core';
import { LocaleSettings } from 'primeng/calendar';

@Injectable()
export class AppSettings {
  API_ENDPOINT: string;
  AVATAR_EXT: string;
  AVATAR_BASE_PATH: string;
  DATE_FORMAT: string;
  LOCALE_PRIME: LocaleSettings;
  APP_NAME?: string;
  CLOSE_AFTER_COMPLETE: boolean;
}

export const APP_CONFIG = new InjectionToken<AppSettings>('AppConfig');
