export class PmcUser {
  public guid?: number;
  public externalGuid?: number;
  public firstName?: string;
  public surname?: string;
  public source?: string;
  public company?: string;
  public displayName?: string;
  public loginName?: string;
  public email?: string;
  public mobile?: string;
  public groups?: string[];
  public roles?: string[];
  public sso?: boolean;
  public stringPermissions?: string[];
  public locked?: boolean;
  public pmcDepartment?: number;
  public runAs?: string;
}