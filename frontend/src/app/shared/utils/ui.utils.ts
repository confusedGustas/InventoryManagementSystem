export function getToggleButtonClass(isActive: boolean): string {
    return isActive
        ? 'border-rose-200 text-rose-700 hover:border-rose-300 hover:text-rose-800'
        : 'border-emerald-200 text-emerald-700 hover:border-emerald-300 hover:text-emerald-800';
}
