import {
  PART_DIVIDER_TOKEN,
  PERMISSION_PLACEHOLDER,
  SUBPART_DIVIDER_TOKEN,
  WILDCARD_TOKEN,
} from '../interfaces-and-types';
import { AlandaUser } from '../../api/models/user';
import { AlandaMenuItem } from '../../api/models/menuItem';
import { AlandaTableLayout } from '../../..';
import { TableType } from '../../enums/tableType.enum';

export class Authorizations {
  /**
   * @description
   * receives a sub part of a permission string
   * in the format provided by the [apache shiro](https://shiro.apache.org/permissions.html) standard,
   * and returns the contained tokens as string array.
   *
   * @example
   * const subTokens = resolveSubParts('write,read,delete'); // ['write','read','delete']
   *
   * @param permissionStringSubPart: string - the string to parse
   *
   * @returns tokens { string[] }  - string array of tokens
   */
  private static resolveSubParts(permissionStringSubPart: string): string[] {
    const tokensOfSubParts: string[] = [];
    const tokens = permissionStringSubPart.split(SUBPART_DIVIDER_TOKEN);
    tokens.forEach((token, i) => {
      token = token.trim();
      tokensOfSubParts[i] = token;
    });
    return tokensOfSubParts;
  }

  /**
   *
   * @description
   * Receives a permission string in the format provided by the [apache shiro](https://shiro.apache.org/permissions.html) standard.
   * Returns an array of token arrays.
   *
   * A permission string consists out of part which in turn can be divided in subparts.
   * Parts and sub parts are divided by delimiters. The parts delimiter is ':' and the sub part delimiter is ",".
   *
   * @example
   * const tokens = resolveTokens('ms:write,read:xyz') // [['ms'], ['write', 'read'], ['xyz']]
   *
   * @param tokenizedString: string - the string to parse
   *
   * @returns tokens { string[][] } - array of token arrays
   */
  static resolveTokens(tokenizedString: string): string[][] {
    const parts = [];
    if (tokenizedString) {
      tokenizedString = tokenizedString.trim();
      const tokens: string[] = tokenizedString.split(PART_DIVIDER_TOKEN);
      tokens.forEach((token) => {
        parts.push(Authorizations.resolveSubParts(token));
      });
      return parts;
    }
  }

  /**
   * @description
   * Checks if a given user has a given role
   *
   * @example
   * const hasAdminRole: boolean = hasRole('admin', user);
   *
   * @param role - the role to check for
   * @param user - the user to check against
   *
   * @returns hasPermissions { boolean } - true if user has given role, false if not
   */
  static hasRole(role: string, user: AlandaUser): boolean {
    return user.roles.includes(role);
  }

  /**
   * @description
   * Checks if a given user has granted rights for a specified entity and accessLevel
   *
   * @example
   * const isPermitted = hasPermission(user,'ms:write', 'write');
   *
   * @param user: AlandaUser - user to check rights for
   * @param permissionString: string - entity to check rights for
   * @param accessLevel: AccessLevels - Level of access
   *
   * @returns isGranted {boolean} - true if rights are granted, false if not
   */
  static hasPermission(
    user: AlandaUser,
    permissionString: string,
    accessLevel?: string,
  ): boolean {
    // If the user has no valid permission strings no access is granted
    if (!user?.stringPermissions || !Array.isArray(user.stringPermissions)) {
      return false;
    }
    const userPermissions: string[] = user.stringPermissions;
    let requestedPermission: string;
    if (accessLevel) {
      requestedPermission = permissionString.replace(
        PERMISSION_PLACEHOLDER,
        accessLevel,
      );
      console.log('requestedPermission: ', requestedPermission);
    } else {
      requestedPermission = permissionString;
    }
    return userPermissions.some((permission) => {
      const boolVal = Authorizations.implies(requestedPermission, permission);
      console.log(boolVal);
      return boolVal;
    });
  }

  /**
   *
   * @description
   * checks if the requested permission string is implied by given permissions of a user.
   *
   * @example
   * const isImplied = implies('ms:write', 'ms:write');
   *
   * @see http://shiro.apache.org/permissions.html#implication-not-equality
   *
   * @param requestedPermission - permission to check for
   * @param userPermission - user permission to check against
   */
  static implies(requestedPermission: string, userPermission: string): boolean {
    let isImplied = true;
    const requestedPermissionParts: string[][] = Authorizations.resolveTokens(
      requestedPermission,
    );
    const userPermissionParts: string[][] = Authorizations.resolveTokens(
      userPermission,
    );

    for (let i = 0; i < userPermissionParts.length; i++) {
      const userPart: string[] = userPermissionParts[i];
      if (i < requestedPermissionParts.length) {
        const requestedPart: string[] = requestedPermissionParts[i];
        if (
          !Authorizations.containsWildCardToken(userPart) &&
          !Authorizations.containsAll(userPart, requestedPart)
        ) {
          isImplied = false;
          break;
        }
      } else {
        if (!Authorizations.containsWildCardToken(userPart)) {
          isImplied = false;
          break;
        }
      }
    }
    return isImplied;
  }

  /**
   * containsWildCardToken
   *
   * @description
   * Checks if a given permissionStringPart contains a wildcard character `*`
   *
   * @example
   * const hasWildCardToken = containsWildCardToken('ms:write'); // false
   * const hasWildCardToken = containsWildCardToken('ms:write:*'); // true
   *
   * @param permissionStringPart - the string to check
   *
   * @returns containsWildCardToken{ boolean } - true if the given string contains a wild card token, false if not
   */
  private static containsWildCardToken(
    permissionStringPart: string[],
  ): boolean {
    return permissionStringPart.includes(WILDCARD_TOKEN);
  }

  /**
   *
   * @description
   * Checks if all tokens are contained
   *
   * @example
   * const allContained = containsAll(['ms','write'], ['ms']) // false
   * const allContained = containsAll(['ms','write'], ['ms','write']) // true
   *
   * @param userPermissionTokens - the permission tokens of the user
   * @param requestedPermissionTokens - the permission tokens to check
   *
   * @return true if all tokens are contained, false if not
   */
  private static containsAll(
    userPermissionTokens: string[],
    requestedPermissionTokens: string[],
  ): boolean {
    let contains = true;
    for (const token of requestedPermissionTokens) {
      if (!userPermissionTokens.includes(token)) {
        contains = false;
        break;
      }
    }
    return contains;
  }

  /**
   *
   * @description Checks if MenuItem has permissions removes them if not granted
   *
   * @param item - AlandaMenuItem to check
   * @param user - AlandaUser to heck against permissions
   * @param accessLevel: AccessLevels - Level of access
   */
  static hasPermissionForMenuItem(
    item: AlandaMenuItem,
    user: AlandaUser,
    accessLevel?: string,
  ): boolean {
    if (item.items) {
      item.items = item.items
        .map((i) => Object.assign({}, i))
        .filter((_item) =>
          Authorizations.hasPermissionForMenuItem(_item, user),
        );

      // remove item without routerLink and any child items
      if (!item.items.length && (!item.routerLink || !item.routerLink.length)) {
        return false;
      }
    }

    if (item.permissions) {
      return !item.permissions.find(
        (permission) =>
          !Authorizations.hasPermission(user, permission, accessLevel),
      );
    }

    return true;
  }

  static hasPermissionForTableLayout(
    layout: AlandaTableLayout,
    user: AlandaUser,
    type: TableType,
  ): boolean {
    return Authorizations.hasPermission(
      user,
      `layout:${type}:${layout.name}`,
      null,
    );
  }
}
