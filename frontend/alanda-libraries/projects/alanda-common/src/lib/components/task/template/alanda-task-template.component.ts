import { Project } from "../../../models/project";
import { PmcTask } from "../../../models/pmcTask";
import { Component } from "@angular/core";
import { FormsRegisterService } from "../../../services/alandaFormsRegister.service";

@Component({
    providers: [FormsRegisterService],
    template: '',
})
export class AlandaTaskTemplateComponent {

    constructor(public formsRegisterService: FormsRegisterService){}

    project: Project;
    task: PmcTask;

    submitTask() {
        this.formsRegisterService.submit(this.task);
    }

}
