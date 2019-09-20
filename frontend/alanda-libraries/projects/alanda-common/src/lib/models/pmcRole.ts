import { PmcPermission } from "./pmcPermission";

export class PmcRole {
  public guid: number;
  public source: string;
  public name: string;
  public permissions: PmcPermission[];
}