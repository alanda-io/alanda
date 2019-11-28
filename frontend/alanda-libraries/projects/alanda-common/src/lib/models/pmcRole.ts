import { PmcPermission } from "./pmcPermission";

export class PmcRole {
  public guid?: number;
  public idName?: string;
  public displayName?: string;
  public source?: string;
  public name?: string;
  public permissions?: PmcPermission[];
}