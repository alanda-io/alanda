import { PmcPermission } from "./PmcPermission";

export class PmcUser {
  guid: number;
  externalGuid: number;
  firstName: string;
  surname: string;
  source: string;
  company: string;
  displayName: string;
  loginName: string;
  email: string;
  mobile: string;
  groups: any[];
  roles: any[];
  sso: boolean;
  stringPermissions?: string[];
  locked: boolean;
  pmcDepartment: number;
}