export function getTotalPages(totalItems: number, pageSize: number): number {
    const normalizedPageSize = Math.max(pageSize, 1);
    return Math.max(Math.ceil(totalItems / normalizedPageSize), 1);
}

export function paginateItems<T>(items: T[], page: number, pageSize: number): T[] {
    const totalPages = getTotalPages(items.length, pageSize);
    const normalizedPage = Math.min(Math.max(page, 1), totalPages);
    const startIndex = (normalizedPage - 1) * pageSize;
    return items.slice(startIndex, startIndex + pageSize);
}
