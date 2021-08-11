import { PipeTransform, Pipe, NgModule } from '@angular/core';

@Pipe({ name: 'nestedObject' })
export class MonitorValuesPipe implements PipeTransform {
  transform(obj: any, path: string): any {
    if (path === undefined) {
      return '';
    }
    return path.split('.').reduce((a, v) => {
      if (typeof a !== 'object' || a[v] === null) {
        return '';
      }
      return (a = a[v]), a;
    }, obj);
  }
}

@NgModule({
  declarations: [MonitorValuesPipe],
  exports: [MonitorValuesPipe],
})
export class MonitorValuesPipeModule {}
