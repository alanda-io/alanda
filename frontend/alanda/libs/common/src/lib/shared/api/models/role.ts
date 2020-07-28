import { AlandaPermission } from './permission';

export interface AlandaRole {
  guid?: number;
  idName?: string;
  displayName?: string;
  source?: string;
  name?: string;
  permissions?: AlandaPermission[];
}
