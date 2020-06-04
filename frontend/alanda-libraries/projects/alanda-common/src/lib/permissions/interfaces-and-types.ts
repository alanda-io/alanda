export interface PermissionObject {
  entityIdentifier: string;
  accessLevel: string;
}

export enum AccessLevels {
  read = 'read',
  write = 'write',
  start = 'start',
  cancel = 'cancel',
  create = 'create'
}

export const WILDCARD_TOKEN = '*';
export const PART_DIVIDER_TOKEN = ':';
export const SUBPART_DIVIDER_TOKEN = ',';
export const PERMISSION_PLACEHOLDER = '#{permissions}';

