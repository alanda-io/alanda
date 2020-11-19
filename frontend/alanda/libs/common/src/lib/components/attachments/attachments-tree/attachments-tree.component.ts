import { Component, Input, EventEmitter, Output } from '@angular/core';
import { TreeNode } from 'primeng/api';

@Component({
  selector: 'alanda-attachments-tree',
  templateUrl: './attachments-tree.component.html',
})
export class AttachmentsTreeComponent {
  @Input() set treeNode(treeNode: TreeNode[]) {
    this.treeNodes = treeNode;
    this.selectedNode = treeNode[0];
  }
  @Output() nodeChangedEvent = new EventEmitter<TreeNode>();

  treeNodes: TreeNode[];
  selectedNode: TreeNode;

  constructor() {}

  nodeSelected(event) {
    this.nodeChangedEvent.emit(event.node);
    this.selectedNode = event.node;
  }
}
