import { Injectable } from '@angular/core';
import { RxState, selectSlice } from '@rx-angular/state';
import { EMPTY, Observable, Subject } from 'rxjs';
import {
  catchError,
  debounceTime,
  distinctUntilChanged,
  filter,
  map,
  switchMap,
  tap,
} from 'rxjs/operators';
import { clone, isEmpty, isEqual, set } from 'lodash';
import { UserStoreImpl } from '../store/user/user.store';
import { StorageService } from './storage.service';
import { ContextStorage } from './contextStorage.interface';
import { ContextEntry } from './contextEntry.interface';

const DEBOUNCE_TIME = 500;
const initState = {
  data: {},
};

@Injectable({
  providedIn: 'root',
})
export class ContextService {
  readonly contextData$ = this.state.select('data');
  needPersist$ = new Subject<ContextStorage>();

  /**
   * Once the current user is available in the store, it's local storage
   * will be loaded to the context.
   *
   * @param user AlandaUser
   * @returns data { data object, userKey: string }
   */
  loadStorage$ = this.userStore.currentUser$.pipe(
    filter((user) => !isEmpty(user)),
    map(({ guid }) => String(guid)),
    // tap((guid) => this.state.set({ userKey: guid })),
    switchMap((userKey) =>
      this.storageService.loadFromStorage(userKey).pipe(
        map((data) => ({ data, userKey })),
        catchError(() => {
          return EMPTY;
        }),
      ),
    ),
  );

  /**
   * Persists data from context to the local storage once
   * need persist event is triggered
   *
   * @param contextStorage ContextStorage
   */
  persistDataToLocalStorage$ = this.needPersist$.pipe(
    debounceTime(DEBOUNCE_TIME),
    distinctUntilChanged(isEqual),
    switchMap(({ userKey, data }) =>
      this.storageService.saveToStorage(userKey, data).pipe(
        catchError(() => {
          return EMPTY;
        }),
      ),
    ),
  );

  constructor(
    private state: RxState<ContextStorage>,
    private userStore: UserStoreImpl,
    private storageService: StorageService,
  ) {
    this.state.set(initState);
    this.state.connect(this.loadStorage$);
    this.state.hold(this.needPersist$);
    this.state.hold(this.persistDataToLocalStorage$);
  }

  /** Listens to specific keys in the context state */
  listen(keys: string[]): Observable<ContextEntry[]> {
    return this.state.select('data').pipe(
      selectSlice([...keys] as never[]),
      map((dataSlice) => keys.map((key) => ({ key, value: dataSlice[key] }))),
    );
  }

  /** Tries to set a value to the context state */
  set(key: string, value: any): void {
    if (isEmpty(key)) {
      throw new Error('Context entry key was not provided');
    }

    const { data, userKey } = this.state.get();

    if (isEmpty(userKey)) {
      throw new Error('No context is available');
    }

    const clonedData = clone(data);
    set(clonedData, key, value);
    this.state.set({ data: clonedData });

    this.needPersist$.next({ userKey, data: clonedData });
  }
}
