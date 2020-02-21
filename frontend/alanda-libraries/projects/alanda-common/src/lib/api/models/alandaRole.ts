import { AlandaPermission } from './alandaPermission';

export interface AlandaRole {
  guid?: number;
  idName?: string;
  displayName?: string;
  source?: string;
  name?: string;
  permissions?: AlandaPermission[];
}
