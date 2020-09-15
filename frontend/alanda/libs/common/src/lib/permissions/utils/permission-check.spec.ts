import { Authorizations } from './permission-checks';
import { AlandaUser } from '../../api/models/user';
import { AlandaMenuItem } from '../../api/models/menuItem';

describe('Check Apache Shiro Authorizations', () => {
  const user: AlandaUser = {
    guid: 1,
    firstName: 'Max',
    surname: 'Tester',
    groups: ['admin', 'bar'],
    roles: ['admin', 'tester'],
    stringPermissions: ['foo:read,write:bar:*', 'test'],
  };

  it('Given permission string resolves into tokens', () => {
    const tokens = Authorizations.resolveTokens('ms:write,read:xyz');
    expect(tokens).toEqual([['ms'], ['write', 'read'], ['xyz']]);
  });

  it('Given user has role', () => {
    const hasAdminRole: boolean = Authorizations.hasRole('admin', user);
    expect(hasAdminRole).toBeTruthy();

    const hasNotFooRole: boolean = Authorizations.hasRole('foo', user);
    expect(hasNotFooRole).toBeFalsy();
  });

  it('Given user has permission', () => {
    const isPermitted = Authorizations.hasPermission(
      user,
      'foo:write:bar',
      'write',
    );
    expect(isPermitted).toBeTruthy();

    const isDenied = Authorizations.hasPermission(user, 'bar:write', 'write');
    expect(isDenied).toBeFalsy();
  });

  it('Requested permission string matches with user permission string', () => {
    const isImplied = Authorizations.implies('foo:bar', 'foo:bar');
    expect(isImplied).toBeTruthy();

    const isNotImplied = Authorizations.implies('bar:foo', 'foo:bar');
    expect(isNotImplied).toBeFalsy();
  });

  it('Filter menu item via permission', () => {
    const items: AlandaMenuItem[] = [
      {
        items: [
          {
            routerLink: '/foo',
            permissions: ['foo:bar'],
          },
        ],
      },
      {
        routerLink: '/bar',
        permissions: ['test'],
        items: [
          {
            routerLink: '/bar/foo',
            permissions: ['test:bar'],
          },
        ],
      },
    ];

    const item0 = Authorizations.hasPermissionForMenuItem(
      items[0],
      user,
      'write',
    );
    expect(item0).toBeFalsy();

    const item1 = Authorizations.hasPermissionForMenuItem(items[1], user);
    expect(item1).toBeTruthy();
  });
});
