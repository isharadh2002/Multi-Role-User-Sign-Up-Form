'use client';

import {useState, useEffect} from 'react';
import {useRouter} from 'next/navigation';
import {api} from '@/lib/api';
import {auth} from '@/lib/auth';
import {validateEmail, validateRequired, validatePhone} from '@/lib/validation';
import {Button} from '@/components/ui/Button';
import {Input} from '@/components/ui/Input';
import {Alert} from '@/components/ui/Alert';
import {COUNTRIES} from '@/constants';

interface User {
    user_id: number;
    first_name: string;
    last_name: string;
    email: string;
    phone_number?: string;
    country: string;
    roles: string[];
    created_at?: string;
}

interface Role {
    role_id: number;
    name: string;
    description: string;
    user_count?: number;
}

export default function DashboardPage() {
    const router = useRouter();
    const [user, setUser] = useState<User | null>(null);
    const [roles, setRoles] = useState<Role[]>([]);
    const [loading, setLoading] = useState(true);
    const [isEditing, setIsEditing] = useState(false);
    const [showPasswordChange, setShowPasswordChange] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    const [editForm, setEditForm] = useState({
        firstName: '',
        lastName: '',
        email: '',
        phoneNumber: '',
        country: '',
        roles: [] as string[]
    });

    const [passwordForm, setPasswordForm] = useState({
        currentPassword: '',
        newPassword: '',
        confirmNewPassword: ''
    });

    const [errors, setErrors] = useState<Record<string, string>>({});

    useEffect(() => {
        if (!auth.isLoggedIn()) {
            router.push('/login');
            return;
        }

        loadData();
    }, [router]);

    const loadData = async () => {
        try {
            const [userResponse, rolesResponse] = await Promise.all([
                api.get<User>('/api/v1/profile'),
                api.get<Role[]>('/api/v1/roles')
            ]);

            if (userResponse.success && userResponse.data) {
                setUser(userResponse.data);
                setEditForm({
                    firstName: userResponse.data.first_name,
                    lastName: userResponse.data.last_name,
                    email: userResponse.data.email,
                    phoneNumber: userResponse.data.phone_number || '',
                    country: userResponse.data.country,
                    roles: userResponse.data.roles
                });
            }

            if (rolesResponse.success) {
                setRoles(rolesResponse.data || []);
            }
        } catch (error) {
            console.error('Failed to load data:', error);
            setError('Failed to load profile data');
        } finally {
            setLoading(false);
        }
    };

    const handleLogout = () => {
        auth.logout();
        router.push('/');
    };

    const validateEditForm = () => {
        const newErrors: Record<string, string> = {};

        const firstNameError = validateRequired(editForm.firstName, 'First name');
        if (firstNameError) newErrors.firstName = firstNameError;

        const lastNameError = validateRequired(editForm.lastName, 'Last name');
        if (lastNameError) newErrors.lastName = lastNameError;

        const emailError = validateEmail(editForm.email);
        if (emailError) newErrors.email = emailError;

        const phoneError = validatePhone(editForm.phoneNumber);
        if (phoneError) newErrors.phoneNumber = phoneError;

        const countryError = validateRequired(editForm.country, 'Country');
        if (countryError) newErrors.country = countryError;

        if (editForm.roles.length === 0) {
            newErrors.roles = 'Please select at least one role';
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleUpdateProfile = async (e: React.FormEvent) => {
        e.preventDefault();
        setError('');
        setSuccess('');

        if (!validateEditForm()) return;

        try {
            const response = await api.put<User>('/api/v1/profile', editForm);

            if (response.success && response.data) {
                setUser(response.data);
                setIsEditing(false);
                setSuccess('Profile updated successfully!');

                // Update localStorage
                localStorage.setItem('email', response.data.email);
                localStorage.setItem('firstName', response.data.first_name);
                localStorage.setItem('lastName', response.data.last_name);
                localStorage.setItem('roles', JSON.stringify(response.data.roles));
            } else {
                if (response.errors) {
                    const fieldErrors: Record<string, string> = {};
                    response.errors.forEach(err => {
                        fieldErrors[err.field] = err.message;
                    });
                    setErrors(fieldErrors);
                } else {
                    setError(response.message || 'Update failed');
                }
            }
        } catch (error) {
            setError('Network error. Please try again.');
        }
    };

    const handlePasswordChange = async (e: React.FormEvent) => {
        e.preventDefault();
        setError('Password change feature is not implemented yet in the backend');
    };

    const handleRoleChange = (roleName: string) => {
        setEditForm(prev => ({
            ...prev,
            roles: prev.roles.includes(roleName)
                ? prev.roles.filter(r => r !== roleName)
                : [...prev.roles, roleName]
        }));
    };

    const getCountryName = (code: string) => {
        const country = COUNTRIES.find(c => c.code === code);
        return country ? country.name : code;
    };

    if (loading) {
        return (
            <div className="min-h-screen bg-gray-50 flex items-center justify-center">
                <div className="text-center">
                    <div className="animate-spin rounded-full h-16 w-16 border-b-2 border-blue-600 mx-auto"></div>
                    <p className="mt-4 text-gray-600">Loading...</p>
                </div>
            </div>
        );
    }

    const isAdmin = auth.isAdmin();

    return (
        <div className="min-h-screen bg-gray-50">
            {/* Header */}
            <header className="bg-white shadow-sm border-b">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="flex justify-between items-center h-16">
                        <h1 className="text-xl font-semibold text-gray-900">Dashboard</h1>
                        <div className="flex items-center space-x-4">
              <span className="text-sm text-gray-700">
                Welcome, {user?.first_name} {user?.last_name}
              </span>
                            {isAdmin && (
                                <Button
                                    variant="danger"
                                    onClick={() => router.push('/admin')}
                                    className="text-sm px-4 py-2"
                                >
                                    Admin Panel
                                </Button>
                            )}
                            <Button
                                variant="secondary"
                                onClick={handleLogout}
                                className="text-sm px-4 py-2"
                            >
                                Logout
                            </Button>
                        </div>
                    </div>
                </div>
            </header>

            <main className="max-w-4xl mx-auto py-8 px-4 sm:px-6 lg:px-8">
                {error && <Alert type="error" className="mb-6">{error}</Alert>}
                {success && <Alert type="success" className="mb-6">{success}</Alert>}

                {/* Profile Card */}
                <div className="bg-white rounded-2xl shadow-xl overflow-hidden">
                    <div className="bg-gradient-to-r from-blue-600 to-indigo-600 px-6 py-8">
                        <div className="flex items-center">
                            <div
                                className="w-20 h-20 bg-white rounded-full flex items-center justify-center text-2xl font-bold text-blue-600">
                                {user?.first_name?.[0]}{user?.last_name?.[0]}
                            </div>
                            <div className="ml-6">
                                <h2 className="text-2xl font-bold text-white">
                                    {user?.first_name} {user?.last_name}
                                </h2>
                                <p className="text-blue-100">{user?.email}</p>
                                <div className="flex flex-wrap gap-2 mt-2">
                                    {user?.roles.map(role => (
                                        <span
                                            key={role}
                                            className="bg-blue-500 text-white px-3 py-1 rounded-full text-xs font-medium"
                                        >
                      {role}
                    </span>
                                    ))}
                                </div>
                            </div>
                        </div>
                    </div>

                    <div className="p-6">
                        {!isEditing ? (
                            <>
                                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                                    <div>
                                        <label className="block text-sm font-medium text-gray-700 mb-1">Phone
                                            Number</label>
                                        <p className="text-gray-900">{user?.phone_number || 'Not provided'}</p>
                                    </div>
                                    <div>
                                        <label className="block text-sm font-medium text-gray-700 mb-1">Country</label>
                                        <p className="text-gray-900">{getCountryName(user?.country || '')}</p>
                                    </div>
                                    <div>
                                        <label className="block text-sm font-medium text-gray-700 mb-1">Member
                                            Since</label>
                                        <p className="text-gray-900">
                                            {user?.created_at ? new Date(user.created_at).toLocaleDateString() : 'Unknown'}
                                        </p>
                                    </div>
                                    <div>
                                        <label className="block text-sm font-medium text-gray-700 mb-1">User ID</label>
                                        <p className="text-gray-900">{user?.user_id}</p>
                                    </div>
                                </div>

                                <div className="flex gap-4 mt-6 pt-6 border-t">
                                    <Button onClick={() => setIsEditing(true)}>
                                        Edit Profile
                                    </Button>
                                    <Button variant="secondary" onClick={() => setShowPasswordChange(true)}>
                                        Change Password
                                    </Button>
                                </div>
                            </>
                        ) : (
                            <form onSubmit={handleUpdateProfile} className="space-y-6">
                                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                                    <Input
                                        label="First Name"
                                        value={editForm.firstName}
                                        onChange={(e) => setEditForm(prev => ({...prev, firstName: e.target.value}))}
                                        error={errors.firstName}
                                        required
                                    />

                                    <Input
                                        label="Last Name"
                                        value={editForm.lastName}
                                        onChange={(e) => setEditForm(prev => ({...prev, lastName: e.target.value}))}
                                        error={errors.lastName}
                                        required
                                    />

                                    <Input
                                        label="Email Address"
                                        type="email"
                                        value={editForm.email}
                                        onChange={(e) => setEditForm(prev => ({...prev, email: e.target.value}))}
                                        error={errors.email}
                                        required
                                    />

                                    <Input
                                        label="Phone Number"
                                        type="tel"
                                        value={editForm.phoneNumber}
                                        onChange={(e) => setEditForm(prev => ({...prev, phoneNumber: e.target.value}))}
                                        error={errors.phoneNumber}
                                        placeholder="+1234567890"
                                    />

                                    <div className="md:col-span-2">
                                        <label className="block text-sm font-medium text-gray-700 mb-2">
                                            Country <span className="text-red-500">*</span>
                                        </label>
                                        <select
                                            value={editForm.country}
                                            onChange={(e) => setEditForm(prev => ({...prev, country: e.target.value}))}
                                            className={`w-full px-4 py-3 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-colors ${
                                                errors.country ? 'border-red-300' : 'border-gray-300'
                                            }`}
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
                                        Roles <span className="text-red-500">*</span> (Choose 1-3)
                                    </label>
                                    <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                                        {roles.map(role => (
                                            <label
                                                key={role.role_id}
                                                className="flex items-center p-3 border rounded-lg cursor-pointer hover:bg-gray-50"
                                            >
                                                <input
                                                    type="checkbox"
                                                    checked={editForm.roles.includes(role.name)}
                                                    onChange={() => handleRoleChange(role.name)}
                                                    className="w-4 h-4 text-blue-600 border-gray-300 rounded"
                                                />
                                                <div className="ml-3">
                                                    <span
                                                        className="text-sm font-medium text-gray-900">{role.name}</span>
                                                    {role.description && (
                                                        <p className="text-xs text-gray-500 mt-1">{role.description}</p>
                                                    )}
                                                </div>
                                            </label>
                                        ))}
                                    </div>
                                    {errors.roles && <p className="text-sm text-red-600">{errors.roles}</p>}
                                </div>

                                <div className="flex gap-4">
                                    <Button type="submit">Save Changes</Button>
                                    <Button
                                        type="button"
                                        variant="secondary"
                                        onClick={() => {
                                            setIsEditing(false);
                                            setErrors({});
                                            setEditForm({
                                                firstName: user?.first_name || '',
                                                lastName: user?.last_name || '',
                                                email: user?.email || '',
                                                phoneNumber: user?.phone_number || '',
                                                country: user?.country || '',
                                                roles: user?.roles || []
                                            });
                                        }}
                                    >
                                        Cancel
                                    </Button>
                                </div>
                            </form>
                        )}
                    </div>
                </div>

                {/* Password Change Modal */}
                {showPasswordChange && (
                    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
                        <div className="bg-white rounded-2xl shadow-xl max-w-md w-full p-6">
                            <h2 className="text-xl font-bold text-gray-900 mb-6">Change Password</h2>

                            <form onSubmit={handlePasswordChange} className="space-y-4">
                                <Input
                                    label="Current Password"
                                    type="password"
                                    value={passwordForm.currentPassword}
                                    onChange={(e) => setPasswordForm(prev => ({
                                        ...prev,
                                        currentPassword: e.target.value
                                    }))}
                                    required
                                />

                                <Input
                                    label="New Password"
                                    type="password"
                                    value={passwordForm.newPassword}
                                    onChange={(e) => setPasswordForm(prev => ({...prev, newPassword: e.target.value}))}
                                    required
                                />

                                <Input
                                    label="Confirm New Password"
                                    type="password"
                                    value={passwordForm.confirmNewPassword}
                                    onChange={(e) => setPasswordForm(prev => ({
                                        ...prev,
                                        confirmNewPassword: e.target.value
                                    }))}
                                    required
                                />

                                <div className="flex gap-4 pt-4">
                                    <Button type="submit" className="flex-1">
                                        Change Password
                                    </Button>
                                    <Button
                                        type="button"
                                        variant="secondary"
                                        onClick={() => {
                                            setShowPasswordChange(false);
                                            setPasswordForm({
                                                currentPassword: '',
                                                newPassword: '',
                                                confirmNewPassword: ''
                                            });
                                        }}
                                        className="flex-1"
                                    >
                                        Cancel
                                    </Button>
                                </div>
                            </form>
                        </div>
                    </div>
                )}
            </main>
        </div>
    );
}