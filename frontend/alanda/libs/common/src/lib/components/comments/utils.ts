import { AlandaComment, AlandaCommentTag } from '@alanda/common';

export const toLowerCase = (text: string) => text.trim().toLowerCase();
export function trackByCommentId(comment: AlandaComment) {
  return comment.guid;
}

export function trackByTagId(tag: AlandaCommentTag) {
  return tag.name;
}

/**
 * Provides style class for tag if active or not
 */
export function tagClass(
  activeTagFilters: { [t: string]: boolean },
  tag: AlandaCommentTag,
): string {
  if (activeTagFilters[tag.name]) {
    return 'ui-button-success';
  }
  return 'ui-button-info';
}

/**
 * Toggles given tag state in active filter map
 */
export function toggleTagFilter(
  activeTagFilters: { [t: string]: boolean },
  tag: AlandaCommentTag,
): { [t: string]: boolean } {
  return {
    ...activeTagFilters,
    [tag.name]: !activeTagFilters[tag.name],
  };
}
