import { Injectable, Inject } from "@angular/core";
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from "rxjs";
import { ExceptionHandlingService } from "../services/exception-handling.service";
import { catchError } from "rxjs/operators";
import { APP_CONFIG, AppSettings } from "../../models/appSettings";
import { Project } from "../../models/project";
import { ServerOptions } from "../../components/project-monitor/project-monitor.component";
import { ProjectType } from "../../models/projectType";

@Injectable({
    providedIn: 'root'
})
export class ProjectServiceNg extends ExceptionHandlingService{

    private endpoint: string;

    constructor(private http: HttpClient, @Inject(APP_CONFIG) config: AppSettings) { 
        super();
        this.endpoint = config.API_ENDPOINT + '/project';
    };

    getProjectByGuid(guid: number): Observable<Project> {
        return this.http.get<any>(`${this.endpoint}/guid/${guid}`).pipe(catchError(this.handleError('getProjectByGuid')));
    }

    getProjectByProjectId(id: string): Observable<Project> {
        return this.http.get<any>(`${this.endpoint}/${id}`).pipe(catchError(this.handleError('getProjectByProjectId')));
    }
    
    loadProjects(serverOptions: ServerOptions): Observable<Project[]> {
        return this.http.post<any[]>(`${this.endpoint}/projectsel`,serverOptions).pipe(catchError(this.handleError('loadProjects', [])));
    }

    updateProject(project): Observable<any> {
        return this.http.put<any>(`${this.endpoint}/${project.projectId}`,project).pipe(catchError(this.handleError('updateProject')));
    }

    getProjectMainProcess(projectGuid: number): Observable<any> {
        return this.http.get<any>(`${this.endpoint}/project/${projectGuid}/mainprocess`).pipe(catchError(this.handleError('getProjectMainProcess')));
    }

    searchCreateAbleProjectType(searchTerm?: string): Observable<ProjectType[]> {
        let params = new HttpParams();
        if (searchTerm) {
          params = params.set('search', searchTerm);
        } else {
            params = params.set('search', '');
        }
        return this.http.get<ProjectType[]>(`${this.endpoint}/createabletype`, {params: params}).pipe(catchError(this.handleError('searchCreateableProjectType', [])));
    }

    getProjectTypeByName(name): Observable<ProjectType> {
        return this.http.get<ProjectType>(`${this.endpoint}/project-type-by-name/${name}`).pipe(catchError(this.handleError('getProjectTypeByName', null)));
    }

    createProject(project: Project): Observable<any> {
        return this.http.post(`${this.endpoint}/create`, project).pipe(catchError(this.handleError('createProject')));
    }

    getProjectTreeByGuid(guid: number): Observable<any> {
        return this.http.get<any>(`${this.endpoint}/guid/${guid}?tree=true`).pipe(catchError(this.handleError('getProjectTreeByGuid')));
    }
}


