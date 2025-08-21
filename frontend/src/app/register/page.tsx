'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { api } from '@/lib/api';
import { validateEmail, validatePassword, validateRequired, validatePhone } from '@/lib/validation';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { Alert } from '@/components/ui/Alert';
import { COUNTRIES } from '@/constants';
import { RegistrationData, Role } from '@/types';

export default function RegisterPage() {
    const router = useRouter();
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [roles, setRoles] = useState<Role[]>([]);

    const [formData, setFormData] = useState<RegistrationData>({
        firstName: '',
        lastName: '',
        email: '',
        password: '',
        confirmPassword: '',
        phoneNumber: '',
        country: '',
        roles: []
    });

    const [errors, setErrors] = useState<Record<string, string>>({});

    useEffect(() => {
        fetchRoles();
    }, []);

    const fetchRoles = async () => {
        try {
            const response = await api.get<Role[]>('/api/v1/roles');
            if (response.success) {
                setRoles(response.data || []);
            }
        } catch (error) {
            console.error('Failed to fetch roles:', error);
        }
    };

    const validateForm = () => {
        const newErrors: Record<string, string> = {};

        const firstNameError = validateRequired(formData.firstName, 'First name');
        if (firstNameError) newErrors.firstName = firstNameError;

        const lastNameError = validateRequired(formData.lastName, 'Last name');
        if (lastNameError) newErrors.lastName = lastNameError;

        const emailError = validateEmail(formData.email);
        if (emailError) newErrors.email = emailError;

        const passwordError = validatePassword(formData.password);
        if (passwordError) newErrors.password = passwordError;

        if (!formData.confirmPassword) {
            newErrors.confirmPassword = 'Please confirm your password';
        } else if (formData.password !== formData.confirmPassword) {
            newErrors.confirmPassword = 'Passwords do not match';
        }

        const phoneError = validatePhone(formData.phoneNumber);
        if (phoneError) newErrors.phoneNumber = phoneError;

        const countryError = validateRequired(formData.country, 'Country');
        if (countryError) newErrors.country = countryError;

        if (formData.roles.length === 0) {
            newErrors.roles = 'Please select at least one role';
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError('');
        setSuccess('');

        if (!validateForm()) return;

        setLoading(true);
        try {
            const response = await api.post('/api/v1/auth/register', formData);

            if (response.success) {
                setSuccess('Registration successful! Redirecting to login...');
                setTimeout(() => router.push('/login'), 2000);
            } else {
                if (response.errors) {
                    const fieldErrors: Record<string, string> = {};
                    response.errors.forEach(err => {
                        fieldErrors[err.field] = err.message;
                    });
                    setErrors(fieldErrors);
                } else {
                    setError(response.message || 'Registration failed');
                }
            }
        } catch (error) {
            setError('Network error. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    const handleInputChange = (name: keyof RegistrationData, value: string) => {
        setFormData(prev => ({ ...prev, [name]: value }));
        if (errors[name]) {
            setErrors(prev => ({ ...prev, [name]: '' }));
        }
    };

    const handleRoleChange = (roleName: string) => {
        setFormData(prev => ({
            ...prev,
            roles: prev.roles.includes(roleName)
                ? prev.roles.filter(r => r !== roleName)
                : [...prev.roles, roleName]
        }));
        if (errors.roles) {
            setErrors(prev => ({ ...prev, roles: '' }));
        }
    };

    return (
        <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center p-4">
            <div className="max-w-2xl w-full bg-white rounded-2xl shadow-xl p-8">
                <div className="text-center mb-8">
                    <h1 className="text-3xl font-bold text-gray-900 mb-2">Create Account</h1>
                    <p className="text-gray-600">Join us today and get started</p>
                </div>

                {error && <Alert type="error" className="mb-6">{error}</Alert>}
                {success && <Alert type="success" className="mb-6">{success}</Alert>}

                <form onSubmit={handleSubmit} className="space-y-6">
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                        <Input
                            label="First Name"
                            value={formData.firstName}
                            onChange={(e) => handleInputChange('firstName', e.target.value)}
                            error={errors.firstName}
                            placeholder="John"
                            required
                        />

                        <Input
                            label="Last Name"
                            value={formData.lastName}
                            onChange={(e) => handleInputChange('lastName', e.target.value)}
                            error={errors.lastName}
                            placeholder="Doe"
                            required
                        />
                    </div>

                    <Input
                        label="Email Address"
                        type="email"
                        value={formData.email}
                        onChange={(e) => handleInputChange('email', e.target.value)}
                        error={errors.email}
                        placeholder="john@example.com"
                        required
                    />

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                        <Input
                            label="Password"
                            type="password"
                            value={formData.password}
                            onChange={(e) => handleInputChange('password', e.target.value)}
                            error={errors.password}
                            placeholder="••••••••"
                            required
                        />

                        <Input
                            label="Confirm Password"
                            type="password"
                            value={formData.confirmPassword}
                            onChange={(e) => handleInputChange('confirmPassword', e.target.value)}
                            error={errors.confirmPassword}
                            placeholder="••••••••"
                            required
                        />
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                        <Input
                            label="Phone Number"
                            type="tel"
                            value={formData.phoneNumber}
                            onChange={(e) => handleInputChange('phoneNumber', e.target.value)}
                            error={errors.phoneNumber}
                            placeholder="+1234567890"
                        />

                        <div className="space-y-2">
                            <label className="block text-sm font-medium text-gray-700">
                                Country <span className="text-red-500">*</span>
                            </label>
                            <select
                                value={formData.country}
                                onChange={(e) => handleInputChange('country', e.target.value)}
                                className={`w-full px-4 py-3 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-colors ${
                                    errors.country ? 'border-red-300' : 'border-gray-300'
                                } text-gray-900`}
                                required
                            >
                                <option value="">Select a country</option>
                                {COUNTRIES.map(country => (
                                    <option key={country.code} value={country.code}>
                                        {country.name}
                                    </option>
                                ))}
                            </select>
                            {errors.country && <p className="text-sm text-red-600">{errors.country}</p>}
                        </div>
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-3">
                            Select Roles <span className="text-red-500">*</span> (Choose 1-3)
                        </label>
                        <div className="space-y-2">
                            {roles.map(role => (
                                <label
                                    key={role.role_id}
                                    className="flex items-center p-3 border rounded-lg cursor-pointer hover:bg-gray-50"
                                >
                                    <input
                                        type="checkbox"
                                        checked={formData.roles.includes(role.name)}
                                        onChange={() => handleRoleChange(role.name)}
                                        className="w-4 h-4 text-blue-600 border-gray-300 rounded"
                                    />
                                    <div className="ml-3">
                                        <span className="text-sm font-medium text-gray-900">{role.name}</span>
                                        {role.description && (
                                            <p className="text-xs text-gray-500 mt-1">{role.description}</p>
                                        )}
                                    </div>
                                </label>
                            ))}
                        </div>
                        {errors.roles && <p className="text-sm text-red-600">{errors.roles}</p>}
                    </div>

                    <Button
                        type="submit"
                        loading={loading}
                        className="w-full"
                    >
                        Create Account
                    </Button>

                    <div className="text-center">
                        <p className="text-sm text-gray-600">
                            Already have an account?{' '}
                            <button
                                type="button"
                                onClick={() => router.push('/login')}
                                className="text-blue-600 hover:text-blue-700 font-medium"
                            >
                                Sign in here
                            </button>
                        </p>
                    </div>
                </form>
            </div>
        </div>
    );
}