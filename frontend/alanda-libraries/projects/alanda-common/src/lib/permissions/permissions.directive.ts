import {Directive, ElementRef, Input} from '@angular/core';
import {RxState} from '@rx-angular/state';
import {AlandaUserApiService} from '../api/userApi.service';
import {AlandaUser} from '../api/models/user';
import {combineLatest} from 'rxjs';
import {AccessLevels} from './interfaces-and-types';
import {hasPermission, resolveTokens} from './utils/permission-checks';
import {ElementManager, getManagersByElementRef} from './utils/element-manager';

@Directive({
  // tslint:disable-next-line:directive-selector
  selector: '[permissions]'
})
export class PermissionsDirective extends RxState<{
  user: AlandaUser,
  currentPermissionTokens: string[]
}> {

  hostElementManagers: ElementManager[] = getManagersByElementRef(this.hostElement);


  @Input('permissions')
  set rights(permissionString: string) {
    this.set({currentPermissionTokens: resolveTokens(permissionString)});
  }

  constructor(
    public hostElement: ElementRef,
    private userService: AlandaUserApiService
  ) {
    super();
    this.connect('user', this.userService.user$);
    this.hold(this.select(), console.log);

    this.hold(
      combineLatest([
        this.select('user'),
        this.select('currentPermissionTokens')
      ]),
      ([user, tokens]) => {

        // @TODO-Michael what token is what?? 0 1 right??
        const accessLevel = tokens[1] as AccessLevels;
        const entityIdentifier = tokens[0];

        if (user === null) {
          this.forbidAll(accessLevel);
        }

        const permissionsGranted = hasPermission(entityIdentifier, accessLevel, user);

        this.hostElementManagers.forEach((manager) => {
          if (permissionsGranted) {
            manager.applyGrantedBehavior(accessLevel);
          } else {
            manager.applyForbiddenBehavior(accessLevel);
          }
        });
      });
  }

  forbidAll(accessLevel) {
    this.hostElementManagers.forEach((manager) => {
      console.log('manager', manager);
      manager.applyForbiddenBehavior(accessLevel);
    });
  }

}
