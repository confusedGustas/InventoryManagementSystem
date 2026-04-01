import {DatePipe} from '@angular/common';
import {Component, input} from '@angular/core';
import {InitialComponent} from '../../../shared/initial/initial.component';

@Component({
    selector: 'app-user-banner',
    standalone: true,
    imports: [DatePipe, InitialComponent],
    templateUrl: './user-banner.component.html'
})
export class UserBannerComponent {

    firstName = input.required<string>();
    lastName = input.required<string>();
    createdOn = input.required<string>();

}
