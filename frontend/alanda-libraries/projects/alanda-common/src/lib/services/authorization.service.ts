import { Injectable } from "@angular/core";
import { PmcUser } from "../models/PmcUser";
import { ExceptionHandlingService } from "./exceptionHandling.service";

@Injectable()
export class AuthorizationServiceNg extends ExceptionHandlingService{

    accessLevel = {
        read: 'read',
        write: 'write',
        start: 'start',
        cancel: 'cancel',
        create: 'create'
    }

    WILDCARD_TOKEN: '*'
    PART_DIVIDER_TOKEN: ':'
    SUBPART_DIVIDER_TOKEN: ','
    PERMISSION_PLACEHOLDER: '#{permissions}'

    constructor(){super();}

    private resolveParts(wildcard: string): string[] {
        let parts = [];
        if(wildcard) {
            wildcard = wildcard.trim();
            let tokens: string[] = wildcard.split(this.PART_DIVIDER_TOKEN);
            tokens.forEach((token) =>{
                parts.push(this.resolveSubParts(token));
            });
            return parts;
        }
    }

    private resolveSubParts(part: string): string[] {
        let subParts: string[] = [];
        let tokens = part.split(this.SUBPART_DIVIDER_TOKEN);
        tokens.forEach((token, i) =>{
            token = token.trim();
            subParts[i] = token;
        });
        return subParts;
    }

    public isAuthorized(entityIdentifier: string, accessLevel?: string, currentUser?: PmcUser): boolean {
        if(!currentUser){
            return false;
        }
        if(!currentUser.stringPermissions || !Array.isArray(currentUser.stringPermissions)){
            console.log('currentUser has no valid stringPermissions property -> no access');
            return false;
        }
        let perm = currentUser.stringPermissions;
        let requestedPermission;
        if(accessLevel){
            requestedPermission = entityIdentifier.replace(this.PERMISSION_PLACEHOLDER, accessLevel);
        } else {
            requestedPermission = entityIdentifier;
        }
        let granted: boolean = false;
        for(let i = 0; i < perm.length; i++){
            granted = this.implies(requestedPermission, perm[i]);
            if(granted){
                break;
            }
        }
        return granted;
    }

    public implies(requestedPerm: string, userPerm: string): boolean {
        let implies: boolean = true;
        let requestedParts: string[] = this.resolveParts(requestedPerm);
        let userParts: string[] = this.resolveParts(userPerm);
        for(let i = 0; i < requestedParts.length; i++){
            let userPart: string = userParts[i];
            if(i < requestedParts.length) {
                let requestedPart: string = requestedParts[i];
                if(!this.containsWildCardToken(userPart) && !this.containsAll(userPart, requestedPart)){
                    implies = false;
                    break;
                }
            } else{
                if(!this.containsWildCardToken(userPart)){
                    implies = false;
                    break;
                }
            }
            return implies;
        }
    }

    private containsWildCardToken(part: string): boolean {
        return part.indexOf(this.WILDCARD_TOKEN) > -1;


    }

    private containsAll(userPart: string, requestedPart: string): boolean {
        let contains: boolean = true;
        for(let i = 0; i < requestedPart.length; i++) {
            if(userPart.indexOf(requestedPart[i]) == -1){
                contains = false;
                break;
            }
        }
        return contains;
    }

    public hasPmcRole(role: string, currentUser: PmcUser): boolean {
        return currentUser.roles.indexOf(role) != -1;
    }

}
