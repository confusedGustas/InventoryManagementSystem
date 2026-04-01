import {Component} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {ToastComponent} from './shared/toast/toast.component';

@Component({
    selector: 'app-root',
    imports: [
        RouterOutlet,
        ToastComponent
    ],
    standalone: true,
    templateUrl: 'app.component.html'
})
export class AppComponent {}
