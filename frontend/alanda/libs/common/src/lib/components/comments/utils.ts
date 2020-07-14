import { AlandaComment, AlandaCommentTag } from '@alanda/common';

export const toLowerCase = (text: string) => text.trim().toLowerCase();
export function trackByCommentId(comment: AlandaComment) {
  return comment.guid;
}

export function trackByTagId(tag: AlandaCommentTag) {
  return tag.name;
}

export interface Dictionary<T> {
  [key: string]: T;
}
