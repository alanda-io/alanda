import { CommentTag } from "./commentTag";

export class PmcComment {
  public guid: number;
  public text: string;
  public createDate: Date;
  public createUser: number;
  public updateDate: string;
  public updateUser: number;
  public taskId: string;
  public procInstId: string;
  public replyTo: any;
  public commentKey: string;
  public subject: string;
  public authorName: string;
  public taskName: string;
  public procDefKey: string;
  public processName: string;
  public siteIdName: string;
  public saIdName: string;
  public reconstructionId: any;
  public replies: PmcComment[];
  public fulltext: string;
  public replyText: string;
  public tagList: CommentTag[];
  public textDate: string;
  public isExpanded: boolean;
}