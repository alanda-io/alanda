import { AlandaTask } from "../interfaces/alanda-task.interface";
import { Project } from "../../../models/project";
import { PmcTask } from "../../../models/pmcTask";
import { FormsRegisterService } from "../../../core/services/forms-register.service";
import { Component } from "@angular/core";

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