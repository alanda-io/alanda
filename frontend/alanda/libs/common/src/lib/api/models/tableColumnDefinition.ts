import { TableColumnTypeEnum } from '../../enums/tableColumnType.enum';

export interface AlandaTableColumnDefinition {
  displayName: string;
  name: string;
  field?: string;
  width?: number | string;
  maxWidth?: number | string;
  cellTemplate?: string;
  template?: string;
  filter?: string;
  type?: TableColumnTypeEnum;
}
