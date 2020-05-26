export interface AlandaUser {
  guid?: number
  externalGuid?: number
  firstName?: string
  surname?: string
  source?: string
  company?: string
  displayName?: string
  loginName?: string
  email?: string
  mobile?: string
  groups?: string[]
  roles?: string[]
  sso?: boolean
  stringPermissions?: string[]
  locked?: boolean
  pmcDepartment?: number
  runAs?: string
}
