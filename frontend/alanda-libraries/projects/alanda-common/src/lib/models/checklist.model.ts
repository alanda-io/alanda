export type CheckListItemDefinition = {
  custom: boolean,
  key: string,
  displayText: string,
  required: boolean,
};

export type CheckListItem = {
  itemDefinition: CheckListItemDefinition,
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




