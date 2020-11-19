import { Component, Output, EventEmitter, Input } from '@angular/core';
import { SimpleDocument } from '../../../api/models/simpleDocument';
import { DomSanitizer } from '@angular/platform-browser';
import { AlandaDocumentApiService } from '../../../api/documentApi.service';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'alanda-attachments-list',
  templateUrl: './attachments-list.component.html',
  styleUrls: ['./attachments-list.component.scss'],
})
export class AttachmentsListComponent {
  @Input() currentFiles: SimpleDocument[];
  @Input() data: any;
  @Output() deleteFile = new EventEmitter<void>();

  previewExtensions = ['jpg', 'jpeg', 'gif', 'png', 'pdf'];
  previewContent: { id: string; pdf: boolean };
  showFilePreview = false;
  previewFile: SimpleDocument;

  constructor(
    private readonly documentService: AlandaDocumentApiService,
    private readonly messageService: MessageService,
    public sanitizer: DomSanitizer,
  ) {}

  setPreview(file) {
    this.previewContent = { id: null, pdf: false };
    this.previewContent.id = file.guid;
    if (file.name.endsWith('.pdf')) {
      this.previewContent.pdf = true;
    }
  }

  triggerDownload(fileId: string): void {
    location.href = this.downloadUrl(fileId, false);
  }

  downloadUrl(fileId: string, inline: boolean = true): string {
    return this.documentService.getDownloadUrl(
      this.data.refObjectType,
      this.data.guid,
      this.data.selectedNode.id,
      fileId,
      inline,
      this.data.selectedNode.mapping,
    );
  }

  previewAllowed(fileName: string): boolean {
    const ext = fileName.split('.').pop().toLowerCase();
    return this.previewExtensions.includes(ext);
  }

  onDeleteFile(file: SimpleDocument) {
    this.documentService
      .deleteFile(
        this.data.refObjectType,
        this.data.guid,
        this.data.selectedNode.id,
        +file.guid,
        this.data.selectedNode.mapping,
      )
      .subscribe((res) => {
        this.currentFiles.splice(this.currentFiles.indexOf(file), 1);
        this.deleteFile.emit();
        this.messageService.add({
          severity: 'success',
          summary: 'File removed',
          detail: 'The file was successfully removed',
        });
      });
  }

  openFilePreview(file: SimpleDocument): void {
    this.showFilePreview = !this.showFilePreview;
    this.previewFile = file;
  }
}
