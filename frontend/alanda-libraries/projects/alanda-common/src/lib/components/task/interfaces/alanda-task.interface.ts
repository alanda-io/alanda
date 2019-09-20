import { PmcTask } from "../../../models/pmcTask";
import { Project } from "../../../models/project";

export interface AlandaTask {
    task: PmcTask;
    project: Project;
    submitTask();
}