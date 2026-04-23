import {Component, input} from '@angular/core';
import {DecimalPipe} from '@angular/common';
import {AnalyticsSummaryDto} from '../analytic.service';

@Component({
    selector: 'app-analytic-summary-cards',
    standalone: true,
    imports: [DecimalPipe],
    templateUrl: './analytic-summary-cards.component.html'
})
export class AnalyticSummaryCardsComponent {

    readonly summary = input.required<AnalyticsSummaryDto>();

}
