import { AlandaComment } from './comment';

export interface AlandaCommentResponse {
  comments: AlandaComment[];
  filterByRefObject: boolean;
  refObjectIdName: string;
}
