import { Component, OnInit, Output, EventEmitter, Input } from '@angular/core';
import { SimpleDocument } from '../models/simple-document';
import { DocumentServiceNg } from '../../../core/api/document.service';
import { DomSanitizer } from '@angular/platform-browser';

@Component({
  selector: 'attachments-list',
  templateUrl: './attachments-list.component.html',
})
export class AttachmentsListComponent implements OnInit {

  @Input() currentFiles: SimpleDocument[];
  @Input() data: any;
  @Output() onDeleteFile = new EventEmitter<void>();

  selectionValue: SimpleDocument;
  fileColumns: any[];
  previewExtensions = ['jpg', 'jpeg', 'gif', 'png', 'pdf'];
  previewContent: {id: string, pdf: boolean};

  constructor(
    private documentService: DocumentServiceNg,
    public sanitizer: DomSanitizer
  ) {}

  ngOnInit() {
    this.fileColumns = [
      {field: 'name', header: 'Name', sort: true},
      {field: 'lastModified', header: 'Last Modified', prio: "ui-p-5", sort: true},
      {field: 'size', header: 'Size', sort: true},
      {field: 'action', header: 'Action', sort: false}
    ];
  }

  setPreview(file){
    this.previewContent = {id: null, pdf: false};
    this.previewContent.id = file.guid;
    if(file.name.endsWith('.pdf')){
      this.previewContent.pdf = true;
    }
  }

  download(fileId: number): string{
    return this.documentService.getDownloadUrl(this.data.refObjectType,this.data.guid,this.data.selectedNode.id, fileId, true, this.data.selectedNode.mapping);
  }

  previewAllowed(fileName: string): boolean{
    let ext = fileName.split(".").pop().toLowerCase()
    return this.previewExtensions.indexOf(ext) != -1;
  }

  deleteFile(file: SimpleDocument){
    this.documentService.deleteFile(this.data.refObjectType,this.data.guid,this.data.selectedNode.id, +file.guid,this.data.selectedNode.mapping).subscribe(
      (res) => {
        this.currentFiles.splice(this.currentFiles.indexOf(file),1)
        this.onDeleteFile.emit();
      }
    );
  }
}
