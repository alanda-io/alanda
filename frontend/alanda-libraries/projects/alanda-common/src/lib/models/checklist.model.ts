export type CheckListItemDefinition = {
  key: string,
  displayText: string,
  required: boolean,
  sortOrder: number
};

export type CheckListItem = {
  customDefinition: boolean,
  key: string,
  displayText: string,
  required: boolean,
  sortOrder: number,
  status: boolean
};

export type CheckListItemBackend = 'DB' | 'CAMUNDA';

export type CheckListTemplate = {
  id: number,
  name: string,
  itemBackend: CheckListItemBackend,
  userTasks: string[],
  itemDefinitions: CheckListItemDefinition[]
};

export type CheckList = {
  id: number,
  name: string,
  checkListItems: CheckListItem[]
};




