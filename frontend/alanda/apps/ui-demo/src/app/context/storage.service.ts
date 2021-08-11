import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { isEmpty } from 'lodash';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class StorageService {
  constructor() {}

  /**
   * Base64 decode function
   *
   * @param val the string value to be decoded
   * @returns the decoded object
   */
  decode(val: string): object {
    return JSON.parse(atob(val));
  }

  /**
   * Base64 encode function
   *
   * @param val the object to be encoded
   * @returns the encoded value as string
   */
  encode(val: object): string {
    return btoa(JSON.stringify(val));
  }

  /**
   * Loads storage entry by its key. The loaded value is
   * decoded and provided as object.
   *
   * @param key string
   * @returns an observable of object
   */
  loadFromStorage(key: string): Observable<object> {
    const data: string = localStorage.getItem(key);
    return of(data).pipe(
      map((val) => (!isEmpty(val) ? this.decode(val as string) : {})),
    );
  }

  /**
   * Saves an entry to the local storage.
   *
   * @param key the key of the entry
   * @param data the value of the entry as an object
   * @returns an observable of boolean
   */
  saveToStorage(key: string, data: object): Observable<boolean> {
    localStorage.setItem(key, this.encode(data));
    return of(true);
  }
}
