import { AlandaCommentTag } from './alandaCommentTag';

export interface AlandaComment {
  guid?: number;
  text?: string;
  createDate?: Date;
  createUser?: number;
  updateDate?: string;
  updateUser?: number;
  taskId?: string;
  procInstId?: string;
  replyTo?: any;
  commentKey?: string;
  subject?: string;
  authorName?: string;
  taskName?: string;
  procDefKey?: string;
  processName?: string;
  siteIdName?: string;
  saIdName?: string;
  reconstructionId?: any;
  replies?: AlandaComment[];
  fulltext?: string;
  replyText?: string;
  tagList?: AlandaCommentTag[];
  textDate?: string;
  isExpanded?: boolean;
}
