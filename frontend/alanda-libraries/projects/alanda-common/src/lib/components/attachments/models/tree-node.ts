import { TreeNode } from "primeng/api";

export class ExtendedTreeNode implements TreeNode {
    public label?: string;
    public data?: any;
    public icon?: string;
    public expandedIcon?: string;
    public collapsedIcon?: string;
    public leaf?: boolean;
    public children?: ExtendedTreeNode[];
    public expanded?: boolean = true;
    public type?: string;
    public parent?: TreeNode;
    public partialSelected?: boolean;
    public styleClass?: string;
    public draggable?: boolean;
    public droppable?: boolean;
    public selectable?: boolean = true;

    public id?: number;
    public virtual?: boolean; 
    public permissions?: string; 
    public mapping?: string;
    public files?: number;
    public name?: string;

}