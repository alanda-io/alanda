import { Component, Input, OnInit, EventEmitter, Output } from "@angular/core";
import { Tree } from 'primeng/tree';
import { TreeNode } from "primeng/api";
import { ExtendedTreeNode } from "../shared/tree-node.model";

@Component({
    selector: 'attachments-tree',
    templateUrl: './attachments-tree.component.html'
  })
  export class AttachmentsTreeComponent{
  
    @Input() treeNode: TreeNode[];
    @Output() nodeChangedEvent = new EventEmitter<string>();

    selectedFile: TreeNode[];
    
    nodeSelect(event){
      this.nodeChangedEvent.emit(event.node);
    } 
    
    constructor() {}
  
  }