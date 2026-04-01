import {Component, computed, input} from '@angular/core';

@Component({
  selector: 'app-initial',
  standalone: true,
  imports: [],
  templateUrl: './initial.component.html',
})
export class InitialComponent {

    firstName = input.required<string>();
    lastName = input.required<string>();

    initials = computed(() => {
        return `${this.firstName()[0]}${this.lastName()[0]}`.toUpperCase();
    });

}
