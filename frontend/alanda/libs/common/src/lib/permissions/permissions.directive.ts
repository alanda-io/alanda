import { Directive, ElementRef, Input } from '@angular/core';
import { RxState } from '@rx-angular/state';
import { AlandaUserApiService } from '../api/userApi.service';
import { AlandaUser } from '../api/models/user';
import { combineLatest } from 'rxjs';
import { Authorizations } from './utils/permission-checks';
import {
  ElementManager,
  getManagersByElementRef,
} from './utils/element-manager';

/**
 *
 * @description
 * Takes a permission string and applies specific behavior for the host element.
 * Can be used on any dom element or component.
 *
 * @example
 * <field-set alandaPermissions="ms:write">
 *   ...
 * </field-set>
 */
@Directive({
  selector: '[alandaPermissions]',
})
export class AlandaPermissionsDirective extends RxState<{
  user: AlandaUser;
  permissionString: string;
}> {
  hostElementManagers: ElementManager[] = getManagersByElementRef(
    this.hostElement,
  );

  @Input('alandaPermissions')
  set rights(permissionString: string) {
    this.set({
      permissionString,
    });
  }

  constructor(
    public hostElement: ElementRef,
    private readonly userService: AlandaUserApiService,
  ) {
    super();
    this.connect('user', this.userService.user$);

    this.hold(
      combineLatest([this.select('user'), this.select('permissionString')]),
      ([user, permissionString]) => {
        const tokens: string[][] = Authorizations.resolveTokens(
          permissionString,
        );
        const accessLevel = tokens[1];

        if (user === null) {
          this.forbidAll(permissionString);
        }

        const permissionsGranted = Authorizations.hasPermission(
          user,
          permissionString,
        );

        this.hostElementManagers.forEach((manager) => {
          if (permissionsGranted) {
            manager.applyGrantedBehavior(accessLevel);
          } else {
            manager.applyForbiddenBehavior(accessLevel);
          }
        });
      },
    );
  }

  forbidAll(accessLevel): void {
    this.hostElementManagers.forEach((manager) => {
      manager.applyForbiddenBehavior(accessLevel);
    });
  }
}
