import { Component, Input, EventEmitter, Output, OnInit } from '@angular/core';
import { TreeNode } from 'primeng/api';

@Component({
  selector: 'alanda-attachments-tree',
  templateUrl: './attachments-tree.component.html',
})
export class AttachmentsTreeComponent implements OnInit {
  @Input() treeNodes: TreeNode[];
  @Input() selectedNode: TreeNode;

  @Output() selectedNodeChanged = new EventEmitter<TreeNode>();

  constructor() {}

  ngOnInit() {
    if (
      this.treeNodes?.length > 0 &&
      (this.selectedNode === null || this.selectedNode === undefined)
    ) {
      this.selectedNode = this.treeNodes[0];
    }
  }

  nodeSelected(event) {
    this.selectedNode = event.node;
    this.selectedNodeChanged.emit(event.node);
  }
}
