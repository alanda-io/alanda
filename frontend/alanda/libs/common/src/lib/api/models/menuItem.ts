import { MenuItem } from 'primeng/api/menuitem';

export interface AlandaMenuItem extends MenuItem {
  items?: AlandaMenuItem[];
  permissions?: string[];
}
