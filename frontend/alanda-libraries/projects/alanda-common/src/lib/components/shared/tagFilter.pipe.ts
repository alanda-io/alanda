import { Pipe, PipeTransform } from '@angular/core';
import { PmcComment } from '../../models/PmcComment';

@Pipe({
  name: 'tagFilter'
})

export class TagFilterPipe implements PipeTransform {
    transform(comments: PmcComment[], allowedTags: string[], filterEnabled: boolean): PmcComment[] {
        if(!filterEnabled){
            return comments;
        }
        let newComments = [];
        comments.forEach((comment) => {
            comment.tagList.forEach((tag) => {
                if(allowedTags.indexOf(tag.name) !== -1){
                    newComments.push(comment);
                }
            });
        });
        return newComments;
    }
}