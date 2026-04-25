import {Component, input, output} from '@angular/core';

@Component({
    selector: 'app-ai-analysis-request',
    standalone: true,
    templateUrl: './ai-analysis-request.component.html'
})
export class AiAnalysisRequestComponent {

    readonly scopeLabel = input.required<string>();
    readonly loading = input.required<boolean>();

    readonly analyzeRequested = output<void>();

}
