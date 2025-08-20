export interface User {
    userId: number;
    first_name: string;
    last_name: string;
    email: string;
    phoneNumber?: string;
    country: string;
    roles: string[];
    createdAt?: string;
}

export interface Role {
    role_id: number;
    name: string;
    description: string;
    user_count?: number;
}

export interface RegistrationData {
    firstName: string;
    lastName: string;
    email: string;
    password: string;
    confirmPassword: string;
    phoneNumber: string;
    country: string;
    roles: string[];
}

export interface LoginData {
    email: string;
    password: string;
}