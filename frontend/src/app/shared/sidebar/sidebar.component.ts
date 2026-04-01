import {Component, signal} from '@angular/core';
import {Router, RouterLink, RouterLinkActive} from '@angular/router';
import {IconComponent} from '../icon/icon.component';
import {LayoutComponent} from '../../layout/layout.component';
import {InitialComponent} from '../initial/initial.component';
import {AuthUtils} from '../../auth/auth.utils';

@Component({
    selector: 'app-sidebar',
    imports: [
        IconComponent,
        RouterLink,
        RouterLinkActive,
        InitialComponent
    ],
    standalone: true,
    templateUrl: './sidebar.component.html'
})
export class SidebarComponent {

    sidebarItems = signal<{ path: string; label: string; icon: string }[]>([]);

    protected readonly AuthUtils = AuthUtils;

    constructor(private router: Router) {
        const layoutRoute = this.router.config.find(route => route.component === LayoutComponent);
        const basePath = layoutRoute?.path ? `/${layoutRoute.path}` : '';
        const currentRole = AuthUtils.getRole();

        const items = layoutRoute?.children!
            .filter(route => !!route.data)
            .filter(route => {
                const roles = route.data!['roles'] as string[] | undefined;
                return !roles || roles.includes(currentRole);
            })
            .map(route => ({
                path: route.path ? `${basePath}/${route.path}` : basePath,
                label: route.data!['label'] as string,
                icon: route.data!['icon'] as string,
            })) ?? [];

        this.sidebarItems.set(items);
    }

}
