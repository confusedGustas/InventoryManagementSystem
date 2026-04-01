import {Component} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {SidebarComponent} from '../shared/sidebar/sidebar.component';

@Component({
    selector: 'app-layout',
    imports: [RouterOutlet, SidebarComponent],
    standalone: true,
    templateUrl: './layout.component.html'
})
export class LayoutComponent {}
