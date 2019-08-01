
import { Component, OnInit, Input } from "@angular/core";


@Component({
    selector: 'vacation-project-details-component',
    templateUrl: './vacation-project-details.component.html',
    styleUrls: [],
  })
  export class VacationProjectDetailsComponent implements OnInit{

    @Input() project: any;
    @Input() pid: string;

    constructor(){
    }

    ngOnInit(){
    }

  }