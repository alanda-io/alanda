import { Component, OnInit, Input, ViewChild } from '@angular/core';
import { ProjectServiceNg } from '../../services/rest/project.service';
import { TableAPIServiceNg } from '../../services/tableAPI.service';
import { LazyLoadEvent, MenuItem, MessageService } from 'primeng/api';
import { Table } from 'primeng/table';
export type ServerOptions = {
  pageNumber: number, 
  pageSize: number,
  filterOptions: any,
  sortOptions: any
}

@Component({
  selector: 'project-monitor-component',
  templateUrl: './project-monitor.component.html' ,
  styles: []
})
export class ProjectMonitorComponent implements OnInit {

  @Input() defaultLayout : string;

  projectsData: any = {};
  layouts: any[] = [];
  selectedLayout: any = {};
  selectedColumns: any = [];
  loading: boolean = true;
  serverOptions: ServerOptions;
  menuItems: MenuItem[];

  @ViewChild('tt') turboTable: Table;
 
  constructor(private projectService: ProjectServiceNg, private tableAPIServiceNg: TableAPIServiceNg, public messageService: MessageService) {
    this.serverOptions = {
      pageNumber: 1,
      pageSize: 15,
      filterOptions: {},
      sortOptions: {},
    };

    this.menuItems = [
      {label: 'Download CSV', icon: 'pi pi-fw pi-download', command: (onclick) => this.turboTable.exportCSV()},
      {label: 'Reset all filters', icon: 'pi pi-fw pi-times', command: (onclick) => this.turboTable.reset()},
    ];
  };

  ngOnInit() {
    const data = this.tableAPIServiceNg.getProjectMonitorLayouts();
    for(const k in data) {
      this.layouts.push(data[k]);
    }    
    this.layouts.sort((a, b) => a.displayName.localeCompare(b.displayName));

    this.selectedLayout = this.layouts.filter(layout => layout.name === 'all')[0];
  }

  loadProjects(serverOptions: ServerOptions){
    this.loading = true;
     this.projectService.loadProjects(serverOptions).subscribe(
      res => {
        this.projectsData = res;
        this.loading = false;
      }
    ); 
  }

  loadProjectsLazy(event: LazyLoadEvent){
    let sortOptions = {}
    sortOptions['project.projectId'] = {dir: 'desc', prio: 0};
    if(event.sortField){
      sortOptions = {}
      const dir = event.sortOrder == 1 ? "asc" : "desc";
      sortOptions[event.sortField] = {dir: dir, prio: 0}
    }
    this.serverOptions.sortOptions = sortOptions;
    this.serverOptions.filterOptions = {};
    for(let key of Object.keys(this.selectedLayout.filterOptions)){
      this.serverOptions.filterOptions[key] = this.selectedLayout.filterOptions[key];
    }    
    for(let key in event.filters){
      this.serverOptions.filterOptions[key] = event.filters[key].value;
    }
    
    this.serverOptions.pageNumber = event.first / this.serverOptions.pageSize + 1;
    this.loadProjects(this.serverOptions);
  }

  onChangeLayout(){
    this.serverOptions.pageNumber = 1;
    this.serverOptions.filterOptions = {};
    for(let key of Object.keys(this.selectedLayout.filterOptions)){
      this.serverOptions.filterOptions[key] = this.selectedLayout.filterOptions[key];
    }
    this.loadProjects(this.serverOptions);
  }

  public getCondition(obj, condition) {
    if(condition === undefined) return '';
    const props = Object.keys(obj).reduce( (acc, next) => `${acc} , ${next}`);
    const evalCon = new Function(` return function ({${props}})  { return ${condition}} `);
    return evalCon()(obj)
  }

  openProject(projectId: string) {
    return '/projectdetails/' + projectId;
  }

}
