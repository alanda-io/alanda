import { Pipe, PipeTransform } from '@angular/core';
import { AlandaComment } from '../api/models/comment';

@Pipe({
  name: 'tagFilter'
})
export class TagFilterPipe implements PipeTransform {
  transform(comments: AlandaComment[], allowedTags: string[], filterEnabled: boolean): AlandaComment[] {
    if (!filterEnabled) {
      return comments;
    }
    const newComments: AlandaComment[] = [];
    comments.forEach((comment) => {
      comment.tagList.forEach((tag) => {
        if (allowedTags.includes(tag.name)) {
          newComments.push(comment);
        }
      });
    });
    return newComments;
  }
}
