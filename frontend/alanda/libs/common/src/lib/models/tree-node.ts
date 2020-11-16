import { TreeNode } from 'primeng/api';

export interface ExtendedTreeNode extends TreeNode {
  label?: string;
  data?: any;
  icon?: string;
  expandedIcon?: string;
  collapsedIcon?: string;
  leaf?: boolean;
  children?: ExtendedTreeNode[];
  expanded?: boolean;
  type?: string;
  parent?: TreeNode;
  partialSelected?: boolean;
  styleClass?: string;
  draggable?: boolean;
  droppable?: boolean;
  selectable?: boolean;
  id?: number;
  virtual?: boolean;
  permissions?: string;
  mapping?: string;
  files?: number;
  name?: string;
}
