import { AlandaTask } from "../interfaces/alanda-task.interface";
import { Project } from "../../../models/project";
import { PmcTask } from "../../../models/pmcTask";
import { Component } from "@angular/core";
import { FormsRegisterService } from "../../../services/forms-register.service";

@Component({
    providers: [FormsRegisterService]
})
export abstract class AlandaTaskTemplate implements AlandaTask {

    constructor(public formsRegisterService: FormsRegisterService){}

    project: Project;
    task: PmcTask;

    submitTask() {
        this.formsRegisterService.submit(this.task);
    }

}