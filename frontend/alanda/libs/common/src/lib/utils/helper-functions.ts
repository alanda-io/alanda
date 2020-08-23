import { AlandaTableLayout } from '../api/models/tableLayout';

export function convertUTCDate(date: Date): Date {
  return new Date(
    Date.UTC(
      date.getFullYear(),
      date.getMonth(),
      date.getDate(),
      date.getHours(),
    ),
  );
}

export function uuid() {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
    var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
    return v.toString(16);
  });
}

export function getTableDefaultLayout(layouts: AlandaTableLayout[]) {
  let defaultLayout = 0;
  const layoutAllIndex = layouts.findIndex(
    (layout) => layout.name.toLowerCase() === 'all',
  );
  if (layoutAllIndex !== -1) {
    defaultLayout = layoutAllIndex;
  }
  return defaultLayout;
}
