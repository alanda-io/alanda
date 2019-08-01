import { PipeTransform, Pipe } from "@angular/core";

@Pipe({ name: 'monitorValues' })
export class MonitorValuesPipe implements PipeTransform{
    transform(obj: any, path: string): any {
        if(path === undefined) return '';
        let ref = path.split(".").reduce((a,v) =>{
            if(a[v] == null) return ''; 
            return (a = a[v],a)
        },obj);
        return ref;
    }
}