import { PmcPermission } from "./PmcPermission";

export class PmcRole {
  guid: number;
  idName: string;
  displayName: string;
  name: string;
  source: string;
  permissions: PmcPermission[];
}