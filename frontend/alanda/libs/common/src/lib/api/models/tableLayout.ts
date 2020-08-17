import { AlandaTableColumnDefinition } from './tableColumnDefinition';

export interface AlandaTableLayout {
  name: string;
  displayName: string;
  filterOptions?: object;
  columnDefs: AlandaTableColumnDefinition[];
}
