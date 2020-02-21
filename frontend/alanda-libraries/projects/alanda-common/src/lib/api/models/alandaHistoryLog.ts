import { HistoryLogAction } from '../../enums/historyLogAction.enum';

export interface AlandaHistoryLog {
    guid?: number;
    refObjectId?: number;
    refObjectType?: string;
    refObjectIdName?: string;
    type?: string;
    action?: HistoryLogAction;
    fieldName?: string;
    fieldRef?: string;
    fieldId?: string;
    oldValue?: string;
    newValue?: string;
    text?: string;
    userGuid?: number;
    user?: string;
    modDate?: Date;
    logDate?: Date;
    pmcProjectGuid?: number;
    projectId?: string;
}
