const API_BASE_URL = process.env.API_BASE_URL || 'http://localhost:8080';

interface ApiResponse<T> {
    success: boolean;
    message: string;
    data?: T;
    errors?: Array<{ field: string; message: string }>;
}

export const api = {
    async post<T>(endpoint: string, data: any): Promise<ApiResponse<T>> {
        const token = localStorage.getItem('token');
        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                ...(token && {Authorization: `Bearer ${token}`}),
            },
            body: JSON.stringify(data),
        });

        if (response.status === 401) {
            localStorage.clear();
            window.location.href = '/login';
            throw new Error('Authentication required');
        }

        return response.json();
    },

    async get<T>(endpoint: string): Promise<ApiResponse<T>> {
        const token = localStorage.getItem('token');
        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
            headers: {
                ...(token && {Authorization: `Bearer ${token}`}),
            },
        });

        if (response.status === 401) {
            localStorage.clear();
            window.location.href = '/login';
            throw new Error('Authentication required');
        }

        return response.json();
    },

    async put<T>(endpoint: string, data: any): Promise<ApiResponse<T>> {
        const token = localStorage.getItem('token');
        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                ...(token && {Authorization: `Bearer ${token}`}),
            },
            body: JSON.stringify(data),
        });

        if (response.status === 401) {
            localStorage.clear();
            window.location.href = '/login';
            throw new Error('Authentication required');
        }

        return response.json();
    },

    async delete<T>(endpoint: string): Promise<ApiResponse<T>> {
        const token = localStorage.getItem('token');
        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
            method: 'DELETE',
            headers: {
                ...(token && {Authorization: `Bearer ${token}`}),
            },
        });

        if (response.status === 401) {
            localStorage.clear();
            window.location.href = '/login';
            throw new Error('Authentication required');
        }

        return response.json();
    },
};