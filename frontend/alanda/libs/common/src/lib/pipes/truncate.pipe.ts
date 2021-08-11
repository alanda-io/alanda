/**
 * A pipe that slices a string based on provided limit and appends a provided trail.
 *
 * @param limit: number the max length of string to be shown
 * @param trail: string the appended trail when the limit is reached
 */
import { NgModule, Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'truncate',
})
export class TruncatePipe implements PipeTransform {
  transform(value: string, args?: any[]): string {
    const limit = args && args.length > 0 ? parseInt(args[0], 10) : 20;
    const trail = args && args.length > 1 ? args[1] : '...';
    return value && value.length > limit
      ? value.substring(0, limit) + trail
      : value;
  }
}

@NgModule({
  declarations: [TruncatePipe],
  exports: [TruncatePipe],
})
export class TruncatePipeModule {}
