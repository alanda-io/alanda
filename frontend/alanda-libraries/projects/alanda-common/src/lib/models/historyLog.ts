import { HistoryLogAction } from "../enums/history-log-action.enum";

export class HistoryLog {
    public guid: number;
    public refObjectId: number;
    public refObjectType: string;
    public refObjectIdName: string;
    public type: string;
    public action: HistoryLogAction;
    public fieldName: string;
    public fieldRef: string;
    public fieldId: string;
    public oldValue: string;
    public newValue: string;
    public text: string;
    public userGuid: number;
    public user: string;
    public modDate: Date;
    public logDate: Date;
    public pmcProjectGuid: number;
    public projectId: string;
}