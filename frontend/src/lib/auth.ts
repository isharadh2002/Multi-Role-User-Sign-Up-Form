export const auth = {
    isLoggedIn(): boolean {
        return !!localStorage.getItem('token');
    },

    isAdmin(): boolean {
        const roles = localStorage.getItem('roles');
        if (!roles) return false;
        try {
            const userRoles = JSON.parse(roles);
            return userRoles.includes('Admin');
        } catch {
            return false;
        }
    },

    logout(): void {
        localStorage.clear();
    },

    getCurrentUser(): any {
        return {
            userId: localStorage.getItem('userId'),
            email: localStorage.getItem('email'),
            firstName: localStorage.getItem('firstName'),
            lastName: localStorage.getItem('lastName'),
            roles: JSON.parse(localStorage.getItem('roles') || '[]'),
        };
    },
};