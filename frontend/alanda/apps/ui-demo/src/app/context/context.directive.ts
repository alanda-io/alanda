import {
  Directive,
  ElementRef,
  Input,
  OnDestroy,
  OnInit,
  Output,
} from '@angular/core';
import { Subject, Subscription } from 'rxjs';
import { isEmpty } from 'lodash';
import { ContextService } from './context.service';
import { ContextEntry } from './contextEntry.interface';

@Directive({
  selector: '[alandaContextProvider]',
})
export class ContextDirective implements OnDestroy, OnInit {
  private _contextListner: Subscription;
  private _keyPrefix: string;
  private _listenTo: string[] = [];

  @Input() set saveEntry(entry: ContextEntry) {
    if (!isEmpty(entry) && !isEmpty(entry?.key)) {
      const { key, value } = entry;
      const selectorKey = `${this._keyPrefix}${key}`;
      this.contextService.set(selectorKey, value);
    }
  }
  @Input() set listenTo(keys: string[]) {
    this._listenTo = keys.map((key) => `${this._keyPrefix}${key}`);
  }
  @Output() entriesAvailable = new Subject<ContextEntry[]>();

  constructor(private _el: ElementRef, private contextService: ContextService) {
    this._keyPrefix = !isEmpty(this._el?.nativeElement?.tagName)
      ? `${this._el?.nativeElement?.tagName?.toLowerCase() || ''}-`
      : '';
  }

  ngOnDestroy(): void {
    this._contextListner?.unsubscribe();
  }

  ngOnInit(): void {
    this._contextListner = this.contextService
      .listen(this._listenTo)
      .subscribe((data) => this.entriesAvailable.next(data));
  }
}
