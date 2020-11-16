import { Component, Input, EventEmitter, Output } from '@angular/core';
import { TreeNode } from 'primeng/api';

@Component({
  selector: 'alanda-attachments-tree',
  templateUrl: './attachments-tree.component.html',
})
export class AttachmentsTreeComponent {
  @Input() treeNode: TreeNode[];
  @Output() nodeChangedEvent = new EventEmitter<string>();

  selectedFile: TreeNode[];

  nodeSelect(event) {
    this.nodeChangedEvent.emit(event.node);
  }

  constructor() {}
}
