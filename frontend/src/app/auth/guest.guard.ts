import {CanActivateFn, Router} from '@angular/router';
import {inject} from '@angular/core';
import {AuthUtils} from './auth.utils';

export const guestGuard: CanActivateFn = async () => {
    const router = inject(Router);

    if (AuthUtils.isLoggedIn()) {
        try {
            await router.navigate(['/app/dashboard']);
        } catch (err) {
            console.error(err);
        }
        return false;
    }

    return true;
};
