import { CommentTag } from "./commentTag.model";

export class PmcComment {
  guid: number;
  text: string;
  createDate: Date;
  createUser: number;
  updateDate: string;
  updateUser: number;
  taskId: string;
  procInstId: string;
  replyTo: any;
  commentKey: string;
  subject: string;
  authorName: string;
  taskName: string;
  procDefKey: string;
  processName: string;
  siteIdName: string;
  saIdName: string;
  reconstructionId: any;
  replies: PmcComment[];

  fulltext?: string;
  replyText?: string;
  tagList?: CommentTag[];
  textDate?: string;
  isExpanded?: boolean;
}