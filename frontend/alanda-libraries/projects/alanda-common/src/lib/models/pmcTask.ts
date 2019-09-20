import { ProjectState } from "../enums/project-status.enum";

export class PmcTask{
    public assignee: string;
    public created: string;
    public due: string;
    public formKey: string;
    public priority: number;
    public description: string;
    public comment: string;
    public pmcProjectGuid: number;
    public candidateGroups: string[];
    public candidateGroupIds: number[];
    public state: ProjectState;
    public task_id: string;
    public task_type: string;
    public task_name: string;
    public object_name: string;
    public object_id: number;
    public assignee_id: string;
    public execution_id: string;
    public follow_up: string;
    public process_definition_id: string;
    public process_instance_id: string;
    public process_name: string;
    public process_definition_key: string;
    public process_package_key: string;
    public suspension_state: boolean;
}