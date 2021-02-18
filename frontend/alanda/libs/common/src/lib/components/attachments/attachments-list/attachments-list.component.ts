import { Component, Output, EventEmitter, Input, Inject } from '@angular/core';
import { SimpleDocument } from '../../../api/models/simpleDocument';
import { DomSanitizer } from '@angular/platform-browser';
import { AlandaDocumentApiService } from '../../../api/documentApi.service';
import { MessageService } from 'primeng/api';
import { RxState } from '@rx-angular/state';
import { Subject } from 'rxjs';
import { map } from 'rxjs/operators';
import { APP_CONFIG, AppSettings } from '../../../models/appSettings';

interface AttachmentsListState {
  files: SimpleDocument[];
  changedFiles: SimpleDocument[];
  previewFile: SimpleDocument;
}

@Component({
  selector: 'alanda-attachments-list',
  templateUrl: './attachments-list.component.html',
  styleUrls: ['./attachments-list.component.scss'],
})
export class AttachmentsListComponent {
  @Input() set currentFiles(files: SimpleDocument[]) {
    this.state.set({ files });
  }
  @Input() data: any;
  @Output() fileDeleted = new EventEmitter<SimpleDocument>();
  @Output() fileRenamed = new EventEmitter<SimpleDocument>();

  state$ = this.state.select();
  changedFileName$ = new Subject<{ file: SimpleDocument; value: string }>();
  previewExtensions = ['jpg', 'jpeg', 'gif', 'png', 'pdf'];
  showFilePreview = false;
  dateFormat: string;

  changedFiles$ = this.changedFileName$.pipe(
    map((event) => {
      const changedFiles = this.state.get().changedFiles;
      if (changedFiles.length) {
        const index = changedFiles.findIndex(
          (f: SimpleDocument) => f.guid === event.file.guid,
        );
        if (index >= 0) {
          changedFiles[index].name = event.value;
        } else {
          const changedFile = event.file;
          changedFile.name = event.value;
          changedFiles.push(changedFile);
        }
        return changedFiles;
      } else {
        const file = event.file;
        file.name = event.value;
        return [file];
      }
    }),
  );

  constructor(
    private readonly documentService: AlandaDocumentApiService,
    private readonly messageService: MessageService,
    public sanitizer: DomSanitizer,
    public state: RxState<AttachmentsListState>,
    @Inject(APP_CONFIG) config: AppSettings,
  ) {
    this.state.connect('changedFiles', this.changedFiles$);
    this.state.set({
      files: [],
      changedFiles: [],
    });
    this.dateFormat = config.DATE_FORMAT;
  }

  triggerDownload(file: SimpleDocument): void {
    location.href = this.downloadUrl(file, false);
  }

  downloadUrl(file: SimpleDocument, inline: boolean = true): string {
    const refObject = this.determineRefObject(this.data);
    return this.documentService.getDownloadUrl(
      refObject.type,
      refObject.id,
      this.data.selectedNode.id,
      file.guid,
      inline,
      this.data.selectedNode.mapping,
    );
  }

  previewAllowed(file: SimpleDocument): boolean {
    const ext = file.name.split('.').pop().toLowerCase();
    return this.previewExtensions.includes(ext);
  }

  onDeleteFile(file: SimpleDocument) {
    const refObject = this.determineRefObject(this.data);
    this.documentService
      .deleteFile(
        refObject.type,
        refObject.id,
        this.data.selectedNode.id,
        +file.guid,
        this.data.selectedNode.mapping,
      )
      .subscribe((res) => {
        const files = this.state.get().files;
        files.splice(files.indexOf(file), 1);
        this.state.set({ files: files });
        this.fileDeleted.emit(file);
        this.messageService.add({
          severity: 'success',
          summary: 'File removed',
          detail: 'The file was successfully removed',
        });
      });
  }

  openFilePreview(file: SimpleDocument): void {
    this.showFilePreview = !this.showFilePreview;
    this.state.set({ previewFile: file });
  }

  openFilePreviewFullscreen(file: SimpleDocument): void {
    const url = this.downloadUrl(file, true);
    window.open(url, '_blank');
  }

  hasUnsavedFileName(file: SimpleDocument): boolean {
    const index = this.state
      .get()
      .changedFiles.findIndex((changedFile) => changedFile.guid === file.guid);
    return index >= 0;
  }

  editFileName(file: SimpleDocument): void {
    this.changedFileName$.next({ file: file, value: file.name });
  }

  renameFile(file: SimpleDocument): void {
    const updatedFiles = this.state.get().changedFiles;
    const updatedFileIndex = updatedFiles.findIndex(
      (changedFile) => changedFile.guid === file.guid,
    );
    const refObject = this.determineRefObject(this.data);
    this.documentService
      .renameFile(
        refObject.type,
        refObject.id,
        this.data.selectedNode.id,
        +updatedFiles[updatedFileIndex].guid,
        updatedFiles[updatedFileIndex].name,
        this.data.selectedNode.mapping,
      )
      .subscribe(
        (res) => {
          const files = this.state.get().files.map((f) => {
            if (f.guid === updatedFiles[updatedFileIndex].guid) {
              f.name = updatedFiles[updatedFileIndex].name;
            }
            return f;
          });

          const changedFiles = updatedFiles;
          changedFiles.splice(updatedFileIndex, 1);

          this.fileRenamed.emit(file);

          this.state.set({ files, changedFiles });
          this.messageService.add({
            severity: 'success',
            summary: 'File renamed',
            detail: 'The file was successfully renamed',
          });
        },
        (error) => {
          this.messageService.add({
            severity: 'error',
            summary: 'File rename failed',
            detail: error.message,
          });
        },
      );
  }

  determineRefObject(data: any): { type: string; id: number } {
    const type =
      data.selectedNode?.refObjectType !== null
        ? data.selectedNode.refObjectType
        : data.refObjectType;
    const id =
      data.selectedNode?.refObjectId !== null
        ? data.selectedNode.refObjectId
        : data.guid;
    return { type, id };
  }
}
