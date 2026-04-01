import {Component, input, OnInit} from '@angular/core';

@Component({
    selector: 'app-icon',
    imports: [],
    standalone: true,
    templateUrl: './icon.component.html'
})
export class IconComponent implements OnInit {

    name = input.required<string>();

    ngOnInit(): void {
        if (!document.querySelector('#material-icons')) {
            const link = document.createElement('link');

            link.id = 'material-icons';
            link.rel = 'stylesheet';
            link.href = 'https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined';

            document.head.appendChild(link);
        }
    }

}
