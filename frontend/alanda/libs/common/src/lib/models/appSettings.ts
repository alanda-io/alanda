import { InjectionToken, Injectable } from '@angular/core';
import { LocaleSettings } from 'primeng/calendar';
import { SelectItem } from 'primeng/api';

@Injectable()
export class AppSettings {
  public static ALANDA_PRIORITIES: SelectItem[] = [
    { value: -1, label: '-1 - Emergency plus' },
    { value: 0, label: '0 - Emergency' },
    { value: 1, label: '1 - Urgent' },
    { value: 2, label: '2 - Normal' },
  ];

  API_ENDPOINT: string;
  AVATAR_EXT: string;
  AVATAR_BASE_PATH: string;
  DATE_FORMAT: string;
  LOCALE_PRIME: LocaleSettings;
  APP_NAME?: string;
  CLOSE_AFTER_COMPLETE: boolean;
  PRIORITIES: SelectItem[];
}

export const APP_CONFIG = new InjectionToken<AppSettings>('AppConfig');
