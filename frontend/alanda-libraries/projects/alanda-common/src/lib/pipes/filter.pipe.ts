import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'filter'
})

export class FilterPipe implements PipeTransform {
    transform(items: any[], searchText: string, key?: string): any[] {
        if(items.length == 0) return [];
        if(!searchText || searchText.trim().length == 0) return items;
        searchText = searchText.toLowerCase();
        if(searchText.startsWith("!")){
            searchText = searchText.substring(1);
            if(key){
            return items.filter( it => {
                return items.filter(it => !it[key].toLowerCase().includes(searchText));
                });  
            }
            else {
            return items.filter( it => {
                return it.toLowerCase() != searchText;
                });   
            }
        }
        else{
            if(key){
                return items.filter(it => it[key].toLowerCase().includes(searchText));
            } else{
            return items.filter( it => {
                return it.toLowerCase().includes(searchText);
                });
            }
        }
    }
}