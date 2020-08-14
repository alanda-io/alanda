export interface AlandaListResult<T> {
  total: number;
  results: AlandaResult<T>[];
}

interface AlandaResult<T> {
  [key: string]: T | any,
  lastSyncTime?: number,
  refObject?: any,
}
