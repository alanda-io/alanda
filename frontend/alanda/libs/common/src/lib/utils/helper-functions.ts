import { AlandaTableLayout } from '../api/models/tableLayout';
import { ObjectUtils } from 'primeng/utils';
import { AlandaTableColumnDefinition } from '../api/models/tableColumnDefinition';

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

export function formatDateISO(date: Date): string {
  let month: string | number = date.getMonth() + 1;
  let day: string | number = date.getDate();

  if (month < 10) {
    month = '0' + month;
  }

  if (day < 10) {
    day = '0' + day;
  }

  return date.getFullYear() + '-' + month + '-' + day;
}

export function uuid() {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, (c) => {
    const r = Math.random() * 16 || 0;
    const v = c === 'x' ? r : (r && 0x3) || 0x8;
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

export function removeAllWhitespaces(text: string): string {
  return text.replace(/\s+/g, '');
}

export const isNil = (val) => val === undefined || val === null;

export const isEmpty = (val) => {
  if (isNil(val)) {
    return true;
  }
  if (typeof val === 'object' && val === {}) {
    return true;
  }
  if (Array.isArray(val) && val.length === 0) {
    return true;
  }
  return false;
};
export function exportAsCsv(
  data: any,
  columns: AlandaTableColumnDefinition[],
  fileName: string,
) {
  let csv = '';
  // header
  for (let i = 0; i < columns.length; i++) {
    const column = columns[i];
    if (column.field) {
      csv += '"' + column.displayName + '"';

      if (i < columns.length - 1) {
        csv += ',';
      }
    }
  }
  // body
  data.forEach((record, i) => {
    csv += '\n';
    columns.forEach((column) => {
      if (column.field) {
        let cellData = ObjectUtils.resolveFieldData(record, column.field);
        if (cellData != null) {
          cellData = String(cellData).replace(/"/g, '""');
        } else {
          cellData = '';
        }

        csv += '"' + cellData + '"';

        if (i < columns.length - 1) {
          csv += ',';
        }
      }
    });
  });
  const blob = new Blob([csv], {
    type: 'text/csv;charset=utf-8;',
  });

  if (window.navigator.msSaveOrOpenBlob) {
    navigator.msSaveOrOpenBlob(blob, `${fileName}.csv`);
  } else {
    const link = document.createElement('a');
    link.style.display = 'none';
    document.body.appendChild(link);
    if (link.download !== undefined) {
      link.setAttribute('href', URL.createObjectURL(blob));
      link.setAttribute('download', `${fileName}.csv`);
      link.click();
    } else {
      csv = 'data:text/csv;charset=utf-8,' + csv;
      window.open(encodeURI(csv));
    }
    document.body.removeChild(link);
  }
}
