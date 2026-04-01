import {Component, input} from '@angular/core';
import {UserDto} from '../settings.service';

@Component({
    selector: 'app-profile-details',
    standalone: true,
    templateUrl: './profile-details.component.html'
})
export class ProfileDetailsComponent {

    profile = input.required<UserDto>();

}
