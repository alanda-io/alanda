export function convertUTCDate(date: Date): Date {
  return new Date(Date.UTC(date.getFullYear(), date.getMonth(), date.getDate(), date.getHours()));
}

export function getStatusClass(status: string): string {
  switch (status) {
    case 'ACTIVE': return 'label-success';
    case 'COMPLETED': return 'label-success';
    case 'SUSPENDED': return 'label-success';
    case 'CANCELED': return 'label-success';
    case 'NEW': return 'label-success';
    case 'NOT REQUIRED': return 'label-success';
    case 'REQUIRED': return 'label-success';
    case 'NULL': return 'label-success';
    case 'FROZEN': return 'label-success';
    case 'ERROR': return 'label-success';
    case 'PREPARED': return 'label-success';
    case '': return '';
  }
}
