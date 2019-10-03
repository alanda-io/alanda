import { Injectable, Inject } from "@angular/core";
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from "rxjs";
import { ExceptionHandlingService } from "../services/exception-handling.service";
import { catchError } from "rxjs/operators";
import { APP_CONFIG, AppSettings } from "../../models/appSettings";
import { Project } from "../../models/project";
import { ServerOptions } from "../../components/project-monitor/project-monitor.component";
import { ProjectType } from "../../models/projectType";
import { Process } from "../../models/process";
import { ListResult } from "../../models/listResult";

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
        return this.http.get<Project>(`${this.endpoint}/guid/${guid}`).pipe(catchError(this.handleError('getProjectByGuid')));
    }

    public getProjectByProjectId(id: string): Observable<Project> {
        return this.http.get<Project>(`${this.endpoint}/${id}`).pipe(catchError(this.handleError('getProjectByProjectId')));
    }
    
    public loadProjects(serverOptions: ServerOptions): Observable<ListResult<Project>> {
        return this.http.post<ListResult<Project>>(`${this.endpoint}/projectsel`,serverOptions).pipe(catchError(this.handleError('loadProjects')));
    }

    public updateProject(project): Observable<Project> {
        return this.http.put<Project>(`${this.endpoint}/${project.projectId}`,project).pipe(catchError(this.handleError('updateProject')));
    }

    public getProjectMainProcess(projectGuid: number): Observable<Process> {
        return this.http.get<Process>(`${this.endpoint}/project/${projectGuid}/mainprocess`).pipe(catchError(this.handleError('getProjectMainProcess')));
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
        return this.http.post<Project>(`${this.endpoint}/create`, project).pipe(catchError(this.handleError('createProject')));
    }

    public getProjectTreeByGuid(guid: number): Observable<Project> {
        return this.http.get<Project>(`${this.endpoint}/guid/${guid}?tree=true`).pipe(catchError(this.handleError('getProjectTreeByGuid')));
    }

    public getProcessesAndTasksForProject(guid: number): Observable<Map<string, any>> {
        return this.http.get<Map<string, any>>(`${this.endpoint}/project/${guid}/processes-and-tasks`);
    }
}


