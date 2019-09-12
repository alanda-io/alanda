import { PmcPermission } from "./PmcPermission";

export class PmcRole {
  guid: number;
  name: string;
  permissions: PmcPermission[];
}