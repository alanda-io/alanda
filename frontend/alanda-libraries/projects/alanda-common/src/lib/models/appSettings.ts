import { InjectionToken, Injectable } from "@angular/core";

@Injectable()
export class AppSettings {
  API_ENDPOINT : string;
  /* WEBSOCKET_ENDPOINT: string; */
}

export const APP_CONFIG = new InjectionToken<AppSettings>("AppConfig");