import { AppSettings } from '@alanda/common';
import { SelectItem } from 'primeng/api';

export const ALANDA_CONFIG: {
  LOCALE_PRIME: {
    dateFormat: string;
    firstDayOfWeek: number;
    today: string;
    clear: string;
    dayNames: string[];
    dayNamesMin: string[];
    dayNamesShort: string[];
    monthNamesShort: string[];
    monthNames: string[];
    weekHeader: string;
  };
  AVATAR_BASE_PATH: string;
  DATE_FORMAT: string;
  PRIORITIES: SelectItem[];
  API_ENDPOINT: string;
  CLOSE_AFTER_COMPLETE: boolean;
  AVATAR_EXT: string;
} = {
  API_ENDPOINT: '/alanda-rest/app',
  DATE_FORMAT: 'yyyy-MM-dd',
  LOCALE_PRIME: {
    firstDayOfWeek: 1,
    dayNames: [
      'Sunday',
      'Monday',
      'Tuesday',
      'Wednesday',
      'Thursday',
      'Friday',
      'Saturday',
    ],
    dayNamesShort: ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'],
    dayNamesMin: ['Su', 'Mo', 'Tu', 'We', 'Th', 'Fr', 'Sa'],
    monthNames: [
      'January',
      'February',
      'March',
      'April',
      'May',
      'June',
      'July',
      'August',
      'September',
      'October',
      'November',
      'December',
    ],
    monthNamesShort: [
      'Jan',
      'Feb',
      'Mar',
      'Apr',
      'May',
      'Jun',
      'Jul',
      'Aug',
      'Sep',
      'Oct',
      'Nov',
      'Dec',
    ],
    today: 'Today',
    clear: 'Clear',
    dateFormat: 'yy-mm-dd',
    weekHeader: 'Wk',
  },
  AVATAR_BASE_PATH: 'src/app/assets',
  AVATAR_EXT: 'jpg',
  CLOSE_AFTER_COMPLETE: true,
  PRIORITIES: AppSettings.ALANDA_DEFAULT_PRIORITIES,
  /* WEBSOCKET_ENDPOINT : 'ws://localhost:8080/pmc-rest/websockets', */
};
