import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { FormGroup, FormBuilder, AbstractControl } from '@angular/forms';
import { AlandaPropertyApiService } from '../../../api/propertyApi.service';
import { AlandaProject } from '../../../api/models/project';
import { RxState } from '@rx-angular/state';
import { combineLatest, merge, of, Subject, zip } from 'rxjs';
import {
  concatMap,
  debounceTime,
  filter,
  map,
  switchMap,
  tap,
} from 'rxjs/operators';
import { SelectItem } from 'primeng/api';
import { Authorizations } from '../../../permissions';
import { AlandaUser } from '../../../api/models/user';

const SELECTOR = 'alanda-prop-autocomplete-eager';
const NOT_EMMITABLE = { emitEvent: false };

interface PropState {
  project: AlandaProject;
  propertyName: string;
  type: string;
  selected: SelectItem;
  options: SelectItem[];
  filteredOptions: SelectItem[];
  canWrite: boolean;
}

const initState = {
  type: 'STRING',
};
/**
 * A component that provides autocomplete functionality and retrieves and stores
 * the selection in the project properties store.
 * Eager means, that options need to be loaded beforehand and passed to the component as input.
 * Search is performed only in the provided options. Optinons needs to be a list of
 * <label, value> pairs as defined in the primeng SelectItem interface.
 */
@Component({
  selector: SELECTOR,
  templateUrl: './prop-autocomplete-eager.component.html',
  styleUrls: [],
  providers: [RxState],
})
export class AlandaPropAutocompleteEagerComponent implements OnInit {
  @Input() set options(options: SelectItem[]) {
    this.state.set({ options: options, filteredOptions: options });
  }
  @Input() set propertyName(propertyName: string) {
    this.state.set({ propertyName });
  }
  @Input() set project(project: AlandaProject) {
    this.state.set({ project });
  }
  @Input() label: string;
  @Input() set type(type: string) {
    this.state.set({ type });
  }
  @Input() user: AlandaUser;

  @Output() propertySelected = new EventEmitter<SelectItem>();

  @Input() rootFormGroup: FormGroup;

  selectPropEvent$ = new Subject<SelectItem>();
  searchPropEvent$ = new Subject<string>();

  selectForm = this.fb.group({
    selected: [null],
  });

  state$ = this.state.select();

  fetchProp$ = combineLatest([
    this.state.select('project'),
    this.state.select('propertyName'),
    this.state.select('options'),
  ]).pipe(
    switchMap(([project, propertyName]) => {
      return this.propertyService.get(null, null, project.guid, propertyName);
    }),
    map((prop) => {
      if (prop != null) {
        let selectedProp = this.state.get('options').find((option) => {
          return option.value == prop.value; // no triple check intentional!
        });
        if (selectedProp != null) {
          return selectedProp;
        }
      }
      return { label: '', value: '' } as SelectItem;
    }),
    tap((prop) => {
      this.selected.patchValue(prop, NOT_EMMITABLE);
    }),
    map((prop) => {
      const authStr = `prop:${
        this.state.get('project').authBase
      }:${this.state.get('propertyName')}`;
      return {
        selected: prop,
        canWrite: Authorizations.hasPermission(this.user, authStr, 'write'),
      };
    }),
  );

  saveProp$ = this.selectPropEvent$.pipe(
    switchMap((prop) => {
      const { project, propertyName, type } = this.state.get();
      return zip(
        this.propertyService.set(
          null,
          null,
          project.guid,
          propertyName,
          prop.value,
          type,
        ),
        of(prop),
      );
    }),
    tap(([voidResult, newProp]) => this.propertySelected.emit(newProp)),
    map(([voidResult, newProp]) => ({ selected: newProp })),
  );

  searchProps$ = merge(
    this.searchPropEvent$,
    this.selected.valueChanges.pipe(filter((input) => input === '')),
  ).pipe(
    debounceTime(300),
    concatMap((searchTerm) => {
      console.log(searchTerm);
      let res = this.state
        .get('options')
        .filter((option) =>
          option.label.toLowerCase().startsWith(searchTerm.toLowerCase()),
        );
      console.log('res', res);
      return of(res);
    }),
  );

  constructor(
    private state: RxState<PropState>,
    private readonly propertyService: AlandaPropertyApiService,
    private readonly fb: FormBuilder,
  ) {
    this.state.set(initState);
    this.state.connect(this.fetchProp$);
    this.state.connect(this.saveProp$);
    this.state.connect('filteredOptions', this.searchProps$);
  }

  ngOnInit(): void {
    if (this.rootFormGroup) {
      this.rootFormGroup.addControl(
        `${SELECTOR}-${this.state.get('propertyName')}`,
        this.selectForm,
      );
    }
  }

  get selected(): AbstractControl {
    return this.selectForm.get('selected');
  }
}
