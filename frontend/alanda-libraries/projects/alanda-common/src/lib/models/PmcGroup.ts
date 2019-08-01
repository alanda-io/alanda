import { PmcPermission } from "./PmcPermission";

export class PmcGroup {
  guid: number;
  groupName: string;
  longName: string;
  email: string;
  phone: string;
  groupSource: string;
  active: boolean;
  version: number;
  permissions: PmcPermission[];
  roles: any[];
}