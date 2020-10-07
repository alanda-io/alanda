import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { FormGroup, FormBuilder, AbstractControl } from '@angular/forms';
import { AlandaPropertyApiService } from '../../../api/propertyApi.service';
import { AlandaProject } from '../../../api/models/project';
import { RxState } from '@rx-angular/state';
import { combineLatest, merge, of, Subject, zip } from 'rxjs';
import {
  concatMap,
  debounceTime,
  distinctUntilChanged,
  map,
  retry,
  switchMap,
  tap,
} from 'rxjs/operators';
import { SelectItem } from 'primeng/api';

const SELECTOR = 'alanda-prop-autocomplete-eager';
const NOT_EMMITABLE = { emitEvent: false };

interface PropState {
  project: AlandaProject;
  propertyName: string;
  type: string;
  selected: SelectItem;
  options: SelectItem[];
  filteredOptions: SelectItem[];
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
export class AlandaPropAutocompleteEagerComponent {
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

  @Output() completeMethod = new EventEmitter<string>();

  @Input()
  set rootFormGroup(rootFormGroup: FormGroup) {
    if (rootFormGroup) {
      rootFormGroup.addControl(
        `${SELECTOR}-${this.propertyName}`,
        this.selectForm,
      );
    }
  }

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
      // console.log('pro', project.guid, propertyName, options);
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
      console.log('prop', prop);
      this.selected.patchValue(prop, NOT_EMMITABLE);
    }),
    map((prop) => ({ selected: prop })),
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
    map(([voidResult, newProp]) => ({ selected: newProp })),
  );

  searchProps$ = this.searchPropEvent$.pipe(
    distinctUntilChanged(),
    debounceTime(300),
    concatMap((searchTerm) => {
      return of(
        this.state
          .get('options')
          .filter((option) =>
            option.label.toLowerCase().startsWith(searchTerm.toLowerCase()),
          ),
      );
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

  get selected(): AbstractControl {
    return this.selectForm.get('selected');
  }
}
