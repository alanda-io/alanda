import {
  Component,
  OnInit,
  Input,
  Output,
  EventEmitter,
  ViewChild,
} from '@angular/core';
import { ExtendedTreeNode } from '../../models/tree-node';
import { SimpleDocument } from '../../api/models/simpleDocument';
import { AlandaDocumentApiService } from '../../api/documentApi.service';
import { removeAllWhitespaces } from '../../utils/helper-functions';
import { MessageService, TreeNode } from 'primeng/api';
import { HttpClient, HttpEvent, HttpEventType } from '@angular/common/http';
import { FileUpload } from 'primeng/fileupload';

@Component({
  selector: 'alanda-attachments',
  templateUrl: './attachments.component.html',
  styleUrls: ['./attachments.component.scss'],
})
export class AlandaAttachmentsComponent implements OnInit {
  @Input() mappings: string | string[];
  @Input() project?: any;
  @Input() task?: any;
  @Input() pid?: string;
  @Input() collapsed = true;
  @Input() selectedFolderName: string;
  @Output() upload = new EventEmitter<any>();

  @ViewChild('fileUploader') uploader: FileUpload;

  panelShown = false;
  uploaderUrl: string;
  showUpload: boolean;
  downloadAllUrl: string;
  fileCount: number;
  data: any = {};
  loadingInProgress: boolean;
  treeNodes: ExtendedTreeNode[] = [];
  currentFiles: SimpleDocument[]; // passed to attachments-list

  constructor(
    private readonly documentService: AlandaDocumentApiService,
    private messageService: MessageService,
    private http: HttpClient,
  ) {}

  ngOnInit() {
    if (!this.mappings) {
      this.mappings = [];
    }

    if (Array.isArray(this.mappings) && this.mappings.length) {
      this.mappings = this.mappings.join(',');
    }

    if (typeof this.mappings === 'string') {
      this.mappings = removeAllWhitespaces(this.mappings);
    }

    this.fileCount = 0;
    this.data.mappings = this.mappings;
    if (this.project) {
      this.data.refObjectType = 'project';
      this.data.guid = this.project.guid;
      this.data.idName = this.project.projectId;
    } else if (this.task) {
      this.data.refObjectType = this.task.task_type;
      this.data.idName = this.task.object_name;
      this.data.guid = this.task.object_id;
    } else if (this.pid) {
      this.data.refObjectType = 'process';
      this.data.guid = this.pid;
    }

    this.loadTreeConfig();
  }

  loadTreeConfig() {
    this.documentService
      .loadTree(
        this.data.refObjectType,
        this.data.guid,
        true,
        this.data.mappings,
      )
      .subscribe((res) => {
        res.name = this.data.idName;
        let temp = [];
        if (res.virtual) {
          temp = res.children;
        } else {
          temp = [res];
        }
        this.data.selectedNode = temp[0];
        this.refreshUrls();
        this.fileCount = this.checkFileCount(temp);
        this.treeNodes = res.children;
        for (const node of this.treeNodes) {
          this.setupTreeNode(node);
        }

        this.loadFolderContent();
      });
  }

  setupTreeNode(node: ExtendedTreeNode) {
    if (
      this.selectedFolderName != null &&
      node.label === this.selectedFolderName
    ) {
      this.data.selectedNode = node;
    }
    node.expanded = false;
    node.collapsedIcon = 'pi pi-folder';
    node.expandedIcon = 'pi pi-folder-open';
    node.name = node.label;
    node.label = node.name + ' (' + node.files + ')';
    for (const child of node.children) {
      this.setupTreeNode(child);
    }
  }

  checkFileCount(nodeList): number {
    let fileCount = 0;
    for (const node of nodeList) {
      fileCount += node.files;
      fileCount += this.checkFileCount(node.children);
    }
    return fileCount;
  }

  downloadAll(): void {
    location.href = this.downloadAllUrl;
  }

  loadFolderContent(): void {
    const selectedNodeId = this.data.selectedNode.id;
    this.documentService
      .loadFolderContent(
        this.data.refObjectType,
        this.data.guid,
        selectedNodeId,
        null,
        this.data.selectedNode.mapping,
      )
      .subscribe((res) => {
        if (selectedNodeId === this.data.selectedNode.id) {
          this.currentFiles = res;
          this.data.selectedNode.files = res.length;
          this.data.selectedNode.label =
            this.data.selectedNode.name + ' (' + res.length + ')';
          this.fileCount = this.checkFileCount(this.treeNodes);
        }
      });
  }

  refreshUrls(event?: any): void {
    this.downloadAllUrl = this.documentService.getDownloadAllUrl(
      this.data.refObjectType,
      this.data.guid,
      this.data.selectedNode.id,
      this.data.selectedNode.mapping,
    );
    this.uploaderUrl = this.documentService.getFolderUrl(
      this.data.refObjectType,
      this.data.guid,
      this.data.selectedNode.id,
      this.data.selectedNode.mapping,
    );
  }

  onUpload(event: any): void {
    this.upload.emit(event);
    this.loadFolderContent();
  }

  myUploader(evt: any): void {
    const formData: FormData = new FormData();

    for (let i = 0; i < evt.files.length; i++) {
      formData.append(
        'file',
        evt.files[i],
        encodeURIComponent(evt.files[i].name),
      );
    }

    this.http['post'](this.uploaderUrl, formData, {
      reportProgress: true,
      observe: 'events',
    }).subscribe(
      (event: HttpEvent<any>) => {
        switch (event.type) {
          case HttpEventType.Sent:
            this.uploader.onSend.emit({
              originalEvent: event,
              formData: formData,
            });
            break;
          case HttpEventType.Response:
            this.uploader.uploading = false;
            this.uploader.progress = 0;
            if (event['status'] >= 200 && event['status'] < 300) {
              if (this.uploader.fileLimit) {
                this.uploader.uploadedFileCount += this.uploader.files.length;
              }
              this.uploader.onUpload.emit({
                originalEvent: event,
                files: evt.files,
              });
            } else {
              this.messageService.add({
                key: 'center',
                severity: 'error',
                summary: 'File upload failed',
                detail: `${event.statusText}`,
              });
              this.uploader.onError.emit({ files: evt.files });
            }
            this.uploader.clear();
            break;
          case HttpEventType.UploadProgress: {
            if (event['loaded']) {
              this.uploader.progress = Math.round(
                (event['loaded'] * 100) / event['total'],
              );
            }

            this.uploader.onProgress.emit({
              originalEvent: event,
              progress: this.uploader.progress,
            });
            break;
          }
        }
        this.uploader.cd.markForCheck();
      },
      (error) => {
        this.uploader.uploading = false;
        this.messageService.add({
          key: 'center',
          severity: 'error',
          summary: 'File upload failed',
          detail: `${error.statusText}`,
        });
        this.uploader.onError.emit({
          files: this.uploader.files,
          error: error,
        });
        this.uploader.clear();
      },
    );
  }

  togglePanel() {
    this.panelShown = !this.panelShown;
    if (!this.panelShown) {
      this.loadTreeConfig();
    }
  }

  onSelectedNodeChange(treeNode: TreeNode) {
    this.data.selectedNode = treeNode;
    this.refreshUrls();
    this.loadFolderContent();
  }

  onDeleteFile() {
    this.loadFolderContent();
  }
}
