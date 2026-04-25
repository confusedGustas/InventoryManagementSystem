import {DatePipe} from '@angular/common';
import {Component, input, output} from '@angular/core';
import {AiAnalysisDto} from '../ai.service';

@Component({
    selector: 'app-ai-analysis-result',
    standalone: true,
    imports: [DatePipe],
    templateUrl: './ai-analysis-result.component.html'
})
export class AiAnalysisResultComponent {

    readonly analysis = input.required<AiAnalysisDto | null>();
    readonly loading = input.required<boolean>();

    readonly pdfRequested = output<string>();

}
