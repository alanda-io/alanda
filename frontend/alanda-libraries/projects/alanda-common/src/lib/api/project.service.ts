import { Injectable, Inject } from "@angular/core";
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from "rxjs";
import { ExceptionHandlingService } from "../services/exception-handling.service";
import { catchError } from "rxjs/operators";
import { APP_CONFIG, AppSettings } from "../models/appSettings";
import { Project } from "../models/project";
import { ListResult } from "../models/listResult";
import { ServerOptions } from "../components/project-monitor/project-monitor.component";
import { Process } from "../models/process";
import { ProjectType } from "../models/projectType";

@Injectable({
    providedIn: 'root'
})
export class ProjectServiceNg extends ExceptionHandlingService{

    private endpoint: string;

    constructor(private http: HttpClient, @Inject(APP_CONFIG) config: AppSettings) {
        super();
        this.endpoint = config.API_ENDPOINT + '/project';
    };

    public getProjectByGuid(guid: number): Observable<Project> {
        return this.http.get<Project>(`${this.endpoint}/guid/${guid}`).pipe(catchError(this.handleError<Project>('getProjectByGuid')));
    }

    public getProjectByProjectId(id: string): Observable<Project> {
        return this.http.get<Project>(`${this.endpoint}/${id}`).pipe(catchError(this.handleError<Project>('getProjectByProjectId')));
    }

    public loadProjects(serverOptions: ServerOptions): Observable<ListResult<any>> {
        return this.http.post<ListResult<any>>(`${this.endpoint}/projectsel`,serverOptions).pipe(catchError(this.handleError<ListResult<any>>('loadProjects')));
    }

    public updateProject(project): Observable<Project> {
        return this.http.put<Project>(`${this.endpoint}/${project.projectId}`,project).pipe(catchError(this.handleError<Project>('updateProject')));
    }

    public getProjectMainProcess(projectGuid: number): Observable<Process> {
        return this.http.get<Process>(`${this.endpoint}/project/${projectGuid}/mainprocess`).pipe(catchError(this.handleError<Process>('getProjectMainProcess')));
    }

    public searchCreateAbleProjectType(searchTerm?: string): Observable<ProjectType[]> {
        let params = new HttpParams();
        if (searchTerm) {
          params = params.set('search', searchTerm);
        } else {
            params = params.set('search', '');
        }
        return this.http.get<ProjectType[]>(`${this.endpoint}/createabletype`, {params: params}).pipe(catchError(this.handleError('searchCreateableProjectType', [])));
    }

    public getProjectTypeByName(name): Observable<ProjectType> {
        return this.http.get<ProjectType>(`${this.endpoint}/project-type-by-name/${name}`).pipe(catchError(this.handleError('getProjectTypeByName', null)));
    }

    public createProject(project: Project): Observable<Project> {
        return this.http.post<Project>(`${this.endpoint}/create`, project).pipe(catchError(this.handleError<Project>('createProject')));
    }

    public getProjectTreeByGuid(guid: number): Observable<Project> {
        return this.http.get<Project>(`${this.endpoint}/guid/${guid}?tree=true`).pipe(catchError(this.handleError<Project>('getProjectTreeByGuid')));
    }

    public getProcessesAndTasksForProject(guid: number): Observable<Map<string, any>> {
        return this.http.get<Map<string, any>>(`${this.endpoint}/project/${guid}/processes-and-tasks`);
    }
}


