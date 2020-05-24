import {Directive, ElementRef, Input} from '@angular/core';
import {RxState} from '@rx-angular/state';
import {AlandaUserApiService} from '../api/userApi.service';
import {AlandaUser} from '../api/models/user';
import {combineLatest} from 'rxjs';
import {AccessLevels} from './interfaces-and-types';
import {hasPermission, resolveTokens} from './utils/permission-checks';
import {getManagersByElementRef} from './utils/element-manager';

@Directive({
  // tslint:disable-next-line:directive-selector
  selector: '[permissions]'
})
export class PermissionsDirective extends RxState<{
  user: AlandaUser,
  currentPermissionTokens: string[]
}> {

  @Input('permissions')
  set rights(permissionString: string) {
    this.set({currentPermissionTokens: resolveTokens(permissionString)});
  }

  constructor(
    public hostElement: ElementRef,
    private userService: AlandaUserApiService
  ) {
    super();
    const hostElementManagers = getManagersByElementRef(this.hostElement);
    this.connect('user', this.userService.user$);

    this.hold(
      combineLatest([
        this.select('user'),
        this.select('currentPermissionTokens')
      ]),
      ([user, tokens]) => {
        // @TODO-Michael what token is what?? 0 1 right??
        const accessLevel = tokens[1] as AccessLevels;
        const entityIdentifier = tokens[0];
        const permissionsGranted = hasPermission(entityIdentifier, accessLevel, user);
        hostElementManagers.forEach((manager) => {
          if (permissionsGranted) {
            manager.applyGrantedBehavior(accessLevel);
          } else {
            manager.applyForbiddenBehavior(accessLevel);
          }
        });
      });
  }

}
