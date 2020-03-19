import { Injectable, Inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { APP_CONFIG, AppSettings } from '../models/appSettings';
import { AlandaProject } from './models/project';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { ServerOptions } from '../models/serverOptions';
import { AlandaListResult } from './models/listResult';
import { AlandaProcess } from './models/process';
import { AlandaProjectType } from './models/projectType';
import { AlandaExceptionHandlingService } from '../services/exceptionHandling.service';

@Injectable()
export class AlandaProjectApiService extends AlandaExceptionHandlingService {

    private endpoint: string;

    constructor(private http: HttpClient, @Inject(APP_CONFIG) config: AppSettings) {
        super();
        this.endpoint = config.API_ENDPOINT + '/project';
    }

    public getProjectByGuid(guid: number): Observable<AlandaProject> {
        return this.http.get<AlandaProject>(`${this.endpoint}/guid/${guid}`)
        .pipe(catchError(this.handleError<AlandaProject>('getProjectByGuid')));
    }

    public getProjectByProjectId(id: string): Observable<AlandaProject> {
        return this.http.get<AlandaProject>(`${this.endpoint}/${id}`)
        .pipe(catchError(this.handleError<AlandaProject>('getProjectByProjectId')));
    }

    public loadProjects(serverOptions: ServerOptions): Observable<AlandaListResult<AlandaProject>> {
        return this.http.post<AlandaListResult<AlandaProject>>(`${this.endpoint}/projectsel`, serverOptions)
        .pipe(catchError(this.handleError<AlandaListResult<AlandaProject>>('loadProjects')));
    }

    public updateProject(project): Observable<AlandaProject> {
        return this.http.put<AlandaProject>(`${this.endpoint}/${project.projectId}`, project)
        .pipe(catchError(this.handleError<AlandaProject>('updateProject')));
    }

    public getProjectMainProcess(projectGuid: number): Observable<AlandaProcess> {
        return this.http.get<AlandaProcess>(`${this.endpoint}/project/${projectGuid}/mainprocess`)
        .pipe(catchError(this.handleError<AlandaProcess>('getProjectMainProcess')));
    }

    public searchCreateAbleProjectType(searchTerm?: string): Observable<AlandaProjectType[]> {
        let params = new HttpParams();
        if (searchTerm) {
          params = params.set('search', searchTerm);
        } else {
            params = params.set('search', '');
        }
        return this.http.get<AlandaProjectType[]>(`${this.endpoint}/createabletype`, {params: params})
        .pipe(catchError(this.handleError('searchCreateableProjectType', [])));
    }

    public getProjectTypeByName(name): Observable<AlandaProjectType> {
        return this.http.get<AlandaProjectType>(`${this.endpoint}/project-type-by-name/${name}`)
        .pipe(catchError(this.handleError('getProjectTypeByName', null)));
    }

    public createProject(project: AlandaProject): Observable<AlandaProject> {
        return this.http.post<AlandaProject>(`${this.endpoint}/create`, project)
        .pipe(catchError(this.handleError<AlandaProject>('createProject')));
    }

    public getProjectTreeByGuid(guid: number): Observable<AlandaProject> {
        return this.http.get<AlandaProject>(`${this.endpoint}/guid/${guid}?tree=true`)
        .pipe(catchError(this.handleError<AlandaProject>('getProjectTreeByGuid')));
    }

    public getProcessesAndTasksForProject(guid: number): Observable<Map<string, any>> {
        return this.http.get<Map<string, any>>(`${this.endpoint}/project/${guid}/processes-and-tasks`)
        .pipe(catchError(this.handleError<Map<string, any>>('getProcessesAndTasksForProject')));
    }
}


