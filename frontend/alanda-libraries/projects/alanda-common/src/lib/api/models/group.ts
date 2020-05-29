import { AlandaPermission } from './permission';

export interface AlandaGroup {
  guid?: number
  groupName?: string
  longName?: string
  email?: string
  phone?: string
  groupSource?: string
  active?: boolean
  version?: number
  permissions?: AlandaPermission[]
  roles?: any[]
}
