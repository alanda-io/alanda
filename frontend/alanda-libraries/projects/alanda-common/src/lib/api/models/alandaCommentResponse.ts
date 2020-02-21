import { AlandaComment } from './alandaComment';

export interface AlandaCommentResponse {
  comments: AlandaComment[];
  filterByRefObject: boolean;
  refObjectIdName: string;
}
