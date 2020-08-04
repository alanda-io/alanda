import { Injectable, Inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { APP_CONFIG, AppSettings } from '../models/appSettings';
import { ExtendedTreeNode } from '../models/tree-node';
import { SimpleDocument } from './models/simpleDocument';
import { catchError } from 'rxjs/operators';
import { AlandaExceptionHandlingService } from '../services/exceptionHandling.service';

@Injectable({
  providedIn: 'root',
})
export class AlandaDocumentApiService extends AlandaExceptionHandlingService {
  private readonly documentEndpointUrl: string;

  constructor(
    private readonly http: HttpClient,
    @Inject(APP_CONFIG) config: AppSettings,
  ) {
    super();
    this.documentEndpointUrl = config.API_ENDPOINT + '/document';
  }

  deleteFile(
    objectType: string,
    objectId: number,
    folderId: number,
    fileId: number,
    mappings?: string,
  ): Observable<any> {
    const params: HttpParams = new HttpParams().set('mappings', mappings);
    return this.http
      .delete<any>(
        `${this.documentEndpointUrl}/refObject/${objectType}/${objectId}/guid/${folderId}/${fileId}`,
        { params },
      )
      .pipe(catchError(this.handleError('deleteFile')));
  }

  getDownloadUrlForVersion(
    objectType: string,
    objectId: number,
    folderId: number,
    fileId: number,
    inline: boolean,
    mappings: string,
    version: number,
  ): string {
    let params = '?';
    if (inline) {
      params += `inline=${inline}&`;
    }
    if (mappings) {
      params += `mappings=${mappings}`;
    }
    return `${this.documentEndpointUrl}/refObject/${objectType}/${objectId}/guid/${folderId}/${fileId}/history/${version}${params}`;
  }

  getDownloadUrlByName(
    objectType: string,
    objectId: number,
    folderName: string,
    fileId: number,
    inline?: boolean,
    mappings?: string,
  ): string {
    let params = '?';
    if (inline) {
      params += `inline=${inline}&`;
    }
    if (mappings) {
      params += `mappings=${mappings}`;
    }
    return `${this.documentEndpointUrl}/refObject/${objectType}/${objectId}/by-name/${folderName}/${fileId}${params}`;
  }

  getFolderUrl(
    objectType: string,
    objectId: number,
    folderId: number,
    mappings?: string,
  ): string {
    let params = '?';
    if (mappings) {
      params += `mappings=${mappings}`;
    }
    return `${this.documentEndpointUrl}/refObject/${objectType}/${objectId}/guid/${folderId}${params}`;
  }

  getFolderUrlByName(
    objectType: string,
    objectId: number,
    folderName: string,
    mappings?: string,
  ): string {
    let params = '?';
    if (mappings) {
      params += `mappings=${mappings}`;
    }
    return `${this.documentEndpointUrl}/refObject/${objectType}/${objectId}/by-name/${folderName}${params}`;
  }

  getDownloadUrl(
    objectType: string,
    objectId: number,
    folderId: number,
    fileId: number,
    inline?: boolean,
    mappings?: string,
  ): string {
    let params = '?';
    if (inline) {
      params += `inline=${inline}&`;
    }
    if (mappings) {
      params += `mappings=${mappings}`;
    }
    return `${this.documentEndpointUrl}/refObject/${objectType}/${objectId}/guid/${folderId}/${fileId}${params}`;
  }

  getDownloadAllUrl(
    objectType: string,
    objectId: number,
    folderId: number,
    mappings?: string,
  ): string {
    let params = '?';
    if (mappings) {
      params += `mappings=${mappings}&`;
    }
    return `${this.documentEndpointUrl}/refObject/${objectType}/${objectId}/guid/${folderId}/download-all${params}`;
  }

  loadTree(
    objectType: string,
    objectId: number,
    includeFileCount?: boolean,
    mappings?: string,
  ): Observable<ExtendedTreeNode> {
    const params: any = {
      fileCount: String(includeFileCount),
      mappings,
    };
    return this.http
      .get<any>(
        `${this.documentEndpointUrl}/refObject/${objectType}/${objectId}`,
        { params },
      )
      .pipe(catchError(this.handleError('loadTree')));
  }

  renameFile(
    objectType: string,
    objectId: number,
    folderId: number,
    fileId: number,
    newName: string,
  ): Observable<any> {
    return this.http
      .put<any>(
        `${
          this.documentEndpointUrl
        }/refObject/${objectType}/${objectId}/guid/${folderId}/${fileId}/rename?newFilename=${encodeURIComponent(
          newName,
        )}`,
        { observe: 'response' },
      )
      .pipe(catchError(this.handleError('renameFile')));
  }

  loadFolderContent(
    objectType: string,
    objectId: number,
    folderId: number,
    fileMask: string,
    mappings?: string,
  ): Observable<SimpleDocument[]> {
    let params = new HttpParams();
    if (fileMask != null) {
      params = params.set('file-mask', fileMask);
    }
    if (mappings != null) {
      params = params.set('mappings', mappings);
    }
    return this.http
      .get<any>(
        `${this.documentEndpointUrl}/refObject/${objectType}/${objectId}/guid/${folderId}`,
        { params },
      )
      .pipe(catchError(this.handleError('loadFolderContent', [])));
  }

  loadFileHistory(
    objectType: string,
    objectId: number,
    folderId: number,
    fileId: number,
  ): Observable<any> {
    return this.http
      .get<any>(
        `${this.documentEndpointUrl}/refObject/${objectType}/guid/${folderId}/${fileId}/history`,
      )
      .pipe(catchError(this.handleError('loadFileHistory', null)));
  }

  loadFolderContentByName(
    objectType: string,
    objectId: number,
    folderName: string,
    fileMask: string,
    mappings: string,
  ): Observable<any> {
    const params = new HttpParams()
      .set('file-mask', fileMask)
      .set('mappings', mappings);
    return this.http
      .get<any>(
        `${this.documentEndpointUrl}/refObject/${objectType}/${objectId}/by-name/${folderName}`,
        { params },
      )
      .pipe(catchError(this.handleError('loadFolderContentByName', null)));
  }
}
