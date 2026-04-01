import {CanActivateFn, Router} from '@angular/router';
import {inject} from '@angular/core';
import {AuthUtils} from './auth.utils';

export const authGuard: CanActivateFn = async (route) => {
    const router = inject(Router);

    if (!AuthUtils.isLoggedIn()) {
        try {
            await router.navigate(['/login']);
        } catch (err) {
            console.error(err);
        }
        return false;
    }

    const requiredRoles = route.data['roles'] as string[] | undefined;
    if (requiredRoles && !AuthUtils.hasAnyRole(requiredRoles)) {
        try {
            await router.navigate(['/app/dashboard']);
        } catch (err) {
            console.error(err);
        }
        return false;
    }

    return true;
};
