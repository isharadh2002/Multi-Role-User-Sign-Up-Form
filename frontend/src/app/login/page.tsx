// app/login/page.tsx - Fixed Login Page
'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { api } from '@/lib/api';
import { validateEmail, validateRequired } from '@/lib/validation';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { Alert } from '@/components/ui/Alert';

interface LoginData {
    email: string;
    password: string;
}

interface LoginResponse {
    token: string;
    tokenType: string;
    user_id: number;
    email: string;
    first_name: string;
    last_name: string;
    roles: string[];
}

export default function LoginPage() {
    const router = useRouter();
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    const [formData, setFormData] = useState<LoginData>({
        email: '',
        password: ''
    });

    const [errors, setErrors] = useState<Record<string, string>>({});

    const validateForm = () => {
        const newErrors: Record<string, string> = {};

        const emailError = validateEmail(formData.email);
        if (emailError) newErrors.email = emailError;

        const passwordError = validateRequired(formData.password, 'Password');
        if (passwordError) newErrors.password = passwordError;

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError('');

        if (!validateForm()) return;

        setLoading(true);
        try {
            const response = await api.post<LoginResponse>('/api/v1/auth/login', formData);

            if (response.success && response.data) {
                const loginData = response.data;

                // Store user data in localStorage
                localStorage.setItem('token', loginData.token);
                localStorage.setItem('userId', loginData.user_id.toString());
                localStorage.setItem('email', loginData.email);
                localStorage.setItem('firstName', loginData.first_name);
                localStorage.setItem('lastName', loginData.last_name);
                localStorage.setItem('roles', JSON.stringify(loginData.roles));

                // Redirect to dashboard
                router.push('/dashboard');
            } else {
                setError(response.message || 'Login failed');
            }
        } catch (error) {
            setError('Network error. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    const handleInputChange = (name: keyof LoginData, value: string) => {
        setFormData(prev => ({ ...prev, [name]: value }));
        if (errors[name]) {
            setErrors(prev => ({ ...prev, [name]: '' }));
        }
    };

    return (
        <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center p-4">
            <div className="max-w-md w-full bg-white rounded-2xl shadow-xl p-8">
                <div className="text-center mb-8">
                    <h1 className="text-3xl font-bold text-gray-900 mb-2">Welcome Back</h1>
                    <p className="text-gray-600">Sign in to your account</p>
                </div>

                {error && <Alert type="error" className="mb-6">{error}</Alert>}

                <form onSubmit={handleSubmit} className="space-y-6">
                    <Input
                        label="Email Address"
                        type="email"
                        value={formData.email}
                        onChange={(e) => handleInputChange('email', e.target.value)}
                        error={errors.email}
                        placeholder="john@example.com"
                        required
                    />

                    <Input
                        label="Password"
                        type="password"
                        value={formData.password}
                        onChange={(e) => handleInputChange('password', e.target.value)}
                        error={errors.password}
                        placeholder="••••••••"
                        required
                    />

                    <Button type="submit" loading={loading} className="w-full">
                        Sign In
                    </Button>

                    <div className="text-center">
                        <p className="text-sm text-gray-600">
                            Don't have an account?{' '}
                            <button
                                type="button"
                                onClick={() => router.push('/register')}
                                className="text-blue-600 hover:text-blue-700 font-medium"
                            >
                                Create one here
                            </button>
                        </p>
                    </div>
                </form>
            </div>
        </div>
    );
}