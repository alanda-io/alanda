import { Directive, ElementRef, Input } from '@angular/core';
import { RxState } from '@rx-angular/state';
import { UserAdapter } from '../services/user.adapter';
import { AlandaUser } from '../api/models/user';
import { combineLatest } from 'rxjs';
import { Authorizations } from './utils/permission-checks';
import {
  ElementManager,
  getManagersByElementRef,
} from './utils/element-manager';

interface AlandaPermissionsDirectiveState {
  user: AlandaUser;
  permissionString: string;
}

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
  providers: [RxState],
})
export class AlandaPermissionsDirective {
  hostElementManagers: ElementManager[] = getManagersByElementRef(
    this.hostElement,
  );

  @Input('alandaPermissions')
  set permission(permissionString: string) {
    this.rxState.set({ permissionString });
  }

  constructor(
    public rxState: RxState<AlandaPermissionsDirectiveState>,
    public hostElement: ElementRef,
    private userAdapter: UserAdapter,
  ) {
    this.rxState.connect('user', this.userAdapter.currentUser$);

    this.rxState.hold(
      combineLatest([
        this.rxState.select('user'),
        this.rxState.select('permissionString'),
      ]),
      ([user, permissionString]) => {
        const tokens: string[][] = Authorizations.resolveTokens(
          permissionString,
        );
        const accessLevel = tokens[1];

        if (user === null) {
          this.forbidAll(permissionString);
          console.warn('Forbid all: No user provided!');
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
