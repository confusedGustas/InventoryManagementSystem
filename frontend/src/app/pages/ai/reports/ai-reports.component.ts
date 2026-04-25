import {DatePipe} from '@angular/common';
import {Component, input, output} from '@angular/core';
import {AiReportDto} from '../ai.service';

@Component({
    selector: 'app-ai-reports',
    standalone: true,
    imports: [DatePipe],
    templateUrl: './ai-reports.component.html'
})
export class AiReportsComponent {

    readonly reports = input.required<AiReportDto[]>();
    readonly loading = input.required<boolean>();

    readonly reportDownloaded = output<string>();

}
