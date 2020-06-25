import { InjectionToken, Injectable } from '@angular/core';

@Injectable()
export class AppSettings {
  API_ENDPOINT: string;
  DATE_FORMAT_STR?: string;
  DATE_FORMAT_STR_PRIME?: string;
}

export const APP_CONFIG = new InjectionToken<AppSettings>('AppConfig');
