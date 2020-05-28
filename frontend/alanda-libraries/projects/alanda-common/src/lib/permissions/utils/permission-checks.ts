import {
  AccessLevels,
  PART_DIVIDER_TOKEN,
  PERMISSION_PLACEHOLDER,
  SUBPART_DIVIDER_TOKEN,
  WILDCARD_TOKEN
} from '../interfaces-and-types';
import {AlandaUser} from '../../api/models/user';

/**
 *
 * @description
 * Receives a permission string in the format provided by the [apache shiro](https://shiro.apache.org/permissions.html) standard.
 * Returns an array of tokens
 *
 * @example
 * @TODO
 *
 * @param tokenizedString: string - the string to parse
 *
 * @returns tokens { string[] }  - string array of tokens
 */
export function resolveTokens(tokenizedString: string): string[] {
  const parts = [];
  if (tokenizedString) {
    tokenizedString = tokenizedString.trim();
    const tokens: string[] = tokenizedString.split(PART_DIVIDER_TOKEN);
    tokens.forEach((token) => {
      parts.push(resolveSubParts(token));
    });
    return parts;
  }
}

/**
 * @TODO considerRenaming to hasRole?
 * hasPmcRole
 *
 * @description
 * Checks if a given user has a given role
 *
 * @example
 * const hasAdminRole: boolean = hasPmcRole('admin', user);
 *
 * @param role - the role to check for
 * @param user - the user to check against
 *
 * @returns hasPermissions { boolean } - true if user has given role, false if not
 */
export function hasPmcRole(role: string, user: AlandaUser): boolean {
  return user.roles.indexOf(role) !== -1;
}

/**
 * @description
 * Checks if a given user has granted rights for a specified entity and accessLevel
 *
 * @example
 * @TODO
 *
 * @param entityIdentifier: string - entity to check rights for
 * @param accessLevel: AccessLevels - Level of access
 * @param currentUser: AlandaUser - user to check rights for
 *
 * @returns isGranted {boolean} - true if rights are granted, false if not
 */
export function hasPermission(entityIdentifier: string, accessLevel?: AccessLevels, currentUser?: AlandaUser): boolean {
  // @TODO Why making the param optional anyway if a user is required
  if (!currentUser) {
    return false;
  }
  // @TODO => logging should be (if even kept in code) only on in development
  // console.log('currentUser has no valid stringPermissions property -> no access');
  // If the user has no valid permission strings no access is granted
  if (!currentUser.stringPermissions || !Array.isArray(currentUser.stringPermissions)) {
    return false;
  }
  const permissions = currentUser.stringPermissions;
  let requestedPermission;
  if (accessLevel) {
    requestedPermission = entityIdentifier.replace(PERMISSION_PLACEHOLDER, accessLevel);
  } else {
    requestedPermission = entityIdentifier;
  }
  return permissions.some(permission => implies(requestedPermission, permission));
}

/**
 * implies
 *
 * @description
 *
 * @param requestedPerm - permissions to check for
 * @param userPerm - user permissions to check against
 */
function implies(requestedPerm: string, userPerm: string): boolean {
  let isImplied = true;
  const requestedParts: string[] = resolveTokens(requestedPerm);
  const userParts: string[] = resolveTokens(userPerm);
  // @TODO needs docs or refactoring
  for (let i = 0; i < requestedParts.length; i++) {
    const userPart: string = userParts[i];
    if (i < requestedParts.length) {
      const requestedPart: string = requestedParts[i];
      if (!containsWildCardToken(userPart) && !containsAll(userPart, requestedPart)) {
        isImplied = false;
        break;
      }
    } else {
      if (!containsWildCardToken(userPart)) {
        isImplied = false;
        break;
      }
    }
    return isImplied;
  }
}

/**
 * containsWildCardToken
 *
 * @description
 * Checks if a given permissionStringPart contains a wildcard character `*`
 *
 * @param permissionStringPart - the string to check
 *
 * @returns containsWildCardToken{ boolean } - true if the given string contains a wild card token, false if not
 */
function containsWildCardToken(permissionStringPart: string): boolean {
  return permissionStringPart.indexOf(WILDCARD_TOKEN) > -1;
}

/**
 * containsAll
 *
 * @TODO mention when to use it, or what for?
 *
 * @TODO is the typing correct here? are we really comparing characters?
 * @param userPermissionsString - ???
 * @param permissionsStringToCheck - ???
 */
function containsAll(userPermissionsString: string, permissionsStringToCheck: string): boolean {
  let contains = true;
  // @TODO use Array.some
  for (let i = 0; i < permissionsStringToCheck.length; i++) {
    if (userPermissionsString.indexOf(permissionsStringToCheck[i]) === -1) {
      contains = false;
      break;
    }
  }
  return contains;
}

/**
 * resolveSubParts
 *
 * @description
 * receives a sub part (@TODO explain what the sub part is) of a permission string
 * in the format provided by the [apache shiro](https://shiro.apache.org/permissions.html) standard,
 * and returns the contained tokens as string array.
 *
 * @example
 * @TODO
 *
 * @param tokenizedString: string - the string to parse
 *
 * @returns tokens { string[] }  - string array of tokens
 */
function resolveSubParts(permissionStringSubPart: string): string[] {
  const tokensOfSubParts: string[] = [];
  const tokens = permissionStringSubPart.split(SUBPART_DIVIDER_TOKEN);
  tokens.forEach((token, i) => {
    token = token.trim();
    tokensOfSubParts[i] = token;
  });
  return tokensOfSubParts;
}
