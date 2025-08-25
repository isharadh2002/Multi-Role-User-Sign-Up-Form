'use client';

import {useState, useEffect} from 'react';
import {useRouter} from 'next/navigation';
import {api} from '@/lib/api';
import {auth} from '@/lib/auth';
import {validateEmail, validateRequired, validatePhone, validatePassword} from '@/lib/validation';
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

interface PasswordChangeData {
    currentPassword: string;
    newPassword: string;
    confirmNewPassword: string;
}

export default function DashboardPage() {
    const router = useRouter();
    const [user, setUser] = useState<User | null>(null);
    const [roles, setRoles] = useState<Role[]>([]);
    const [loading, setLoading] = useState(true);
    const [isEditing, setIsEditing] = useState(false);
    const [showPasswordChange, setShowPasswordChange] = useState(false);
    const [passwordChangeLoading, setPasswordChangeLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);

    useEffect(() => {
        document.title = 'UserHub - Dashboard';
    }, []);

    useEffect(() => {
        if (error || success) {
            const timer = setTimeout(() => {
                setError('');
                setSuccess('');
            }, 3000);

            return () => clearTimeout(timer);
        }
    }, [error, success]);

    const [editForm, setEditForm] = useState({
        firstName: '',
        lastName: '',
        email: '',
        phoneNumber: '',
        country: '',
        roles: [] as string[]
    });

    const [passwordForm, setPasswordForm] = useState<PasswordChangeData>({
        currentPassword: '',
        newPassword: '',
        confirmNewPassword: ''
    });

    const [errors, setErrors] = useState<Record<string, string>>({});
    const [passwordErrors, setPasswordErrors] = useState<Record<string, string>>({});

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

    const toggleMobileMenu = () => {
        setIsMobileMenuOpen(!isMobileMenuOpen);
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

    const validatePasswordForm = () => {
        const newErrors: Record<string, string> = {};

        const currentPasswordError = validateRequired(passwordForm.currentPassword, 'Current password');
        if (currentPasswordError) newErrors.currentPassword = currentPasswordError;

        const newPasswordError = validatePassword(passwordForm.newPassword);
        if (newPasswordError) newErrors.newPassword = newPasswordError;

        const confirmPasswordError = validateRequired(passwordForm.confirmNewPassword, 'Confirm new password');
        if (confirmPasswordError) {
            newErrors.confirmNewPassword = confirmPasswordError;
        } else if (passwordForm.newPassword !== passwordForm.confirmNewPassword) {
            newErrors.confirmNewPassword = 'Passwords do not match';
        }

        if (passwordForm.currentPassword === passwordForm.newPassword) {
            newErrors.newPassword = 'New password must be different from current password';
        }

        setPasswordErrors(newErrors);
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
        setError('');
        setSuccess('');

        if (!validatePasswordForm()) return;

        setPasswordChangeLoading(true);
        try {
            const response = await api.put('/api/v1/profile/change-password', passwordForm);

            if (response.success) {
                setSuccess('Password changed successfully!');
                setTimeout(() => {
                    setShowPasswordChange(false);
                    setPasswordForm({
                        currentPassword: '',
                        newPassword: '',
                        confirmNewPassword: ''
                    });
                    setPasswordErrors({});
                }, 2000);
            } else {
                if (response.errors && response.errors.length > 0) {
                    const fieldErrors: Record<string, string> = {};
                    response.errors.forEach(err => {
                        fieldErrors[err.field] = err.message;
                    });
                    setPasswordErrors(fieldErrors);
                } else {
                    setError(response.message || 'Password change failed');
                }
            }
        } catch (error) {
            setError('Network error. Please try again.');
        } finally {
            setPasswordChangeLoading(false);
        }
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

    const closePasswordModal = () => {
        setShowPasswordChange(false);
        setPasswordForm({
            currentPassword: '',
            newPassword: '',
            confirmNewPassword: ''
        });
        setPasswordErrors({});
        setError('');
        setSuccess('');
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

                        {/* Desktop Navigation */}
                        <div className="hidden md:flex items-center space-x-4">
                            <span className="text-sm text-gray-700 truncate max-w-48">
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

                        {/* Mobile menu button */}
                        <div className="md:hidden">
                            <button
                                onClick={toggleMobileMenu}
                                className="inline-flex items-center justify-center p-2 rounded-md text-gray-500 hover:text-gray-900 hover:bg-gray-100 focus:outline-none focus:ring-2 focus:ring-inset focus:ring-blue-500"
                            >
                                <span className="sr-only">Open menu</span>
                                <svg
                                    className={`${isMobileMenuOpen ? 'hidden' : 'block'} h-6 w-6`}
                                    xmlns="http://www.w3.org/2000/svg"
                                    fill="none"
                                    viewBox="0 0 24 24"
                                    stroke="currentColor"
                                >
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 12h16M4 18h16" />
                                </svg>
                                <svg
                                    className={`${isMobileMenuOpen ? 'block' : 'hidden'} h-6 w-6`}
                                    xmlns="http://www.w3.org/2000/svg"
                                    fill="none"
                                    viewBox="0 0 24 24"
                                    stroke="currentColor"
                                >
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                                </svg>
                            </button>
                        </div>
                    </div>

                    {/* Mobile Navigation Menu */}
                    <div className={`md:hidden ${isMobileMenuOpen ? 'block' : 'hidden'}`}>
                        <div className="px-2 pt-2 pb-3 space-y-1 border-t border-gray-200 bg-white">
                            <div className="px-3 py-2 text-sm text-gray-700 border-b border-gray-100">
                                Welcome, {user?.first_name} {user?.last_name}
                            </div>
                            {isAdmin && (
                                <button
                                    onClick={() => {
                                        router.push('/admin');
                                        setIsMobileMenuOpen(false);
                                    }}
                                    className="block w-full text-left px-3 py-2 rounded-md text-base font-medium text-white bg-red-600 hover:bg-red-700 transition-colors"
                                >
                                    Admin Panel
                                </button>
                            )}
                            <button
                                onClick={() => {
                                    handleLogout();
                                    setIsMobileMenuOpen(false);
                                }}
                                className="block w-full text-left px-3 py-2 rounded-md text-base font-medium text-gray-700 hover:text-gray-900 hover:bg-gray-50 transition-colors"
                            >
                                Logout
                            </button>
                        </div>
                    </div>
                </div>
            </header>

            <main className="max-w-4xl mx-auto py-6 sm:py-8 px-4 sm:px-6 lg:px-8">
                {error && <Alert type="error" className="mb-6">{error}</Alert>}
                {success && <Alert type="success" className="mb-6">{success}</Alert>}

                {/* Profile Card */}
                <div className="bg-white rounded-2xl shadow-xl overflow-hidden">
                    <div className="bg-gradient-to-r from-blue-600 to-indigo-600 px-4 sm:px-6 py-6 sm:py-8">
                        <div className="flex flex-col sm:flex-row items-center sm:items-start">
                            <div className="w-16 h-16 sm:w-20 sm:h-20 bg-white rounded-full flex items-center justify-center text-xl sm:text-2xl font-bold text-blue-600 mb-4 sm:mb-0">
                                {user?.first_name?.[0]}{user?.last_name?.[0]}
                            </div>
                            <div className="sm:ml-6 text-center sm:text-left">
                                <h2 className="text-xl sm:text-2xl font-bold text-white">
                                    {user?.first_name} {user?.last_name}
                                </h2>
                                <p className="text-blue-100 break-all">{user?.email}</p>
                                <div className="flex flex-wrap justify-center sm:justify-start gap-2 mt-2">
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

                    <div className="p-4 sm:p-6">
                        {!isEditing ? (
                            <>
                                <div className="grid grid-cols-1 md:grid-cols-2 gap-4 sm:gap-6">
                                    <div>
                                        <label className="block text-sm font-medium text-gray-700 mb-1">Phone Number</label>
                                        <p className="text-gray-900 break-all">{user?.phone_number || 'Not provided'}</p>
                                    </div>
                                    <div>
                                        <label className="block text-sm font-medium text-gray-700 mb-1">Country</label>
                                        <p className="text-gray-900">{getCountryName(user?.country || '')}</p>
                                    </div>
                                    <div>
                                        <label className="block text-sm font-medium text-gray-700 mb-1">Member Since</label>
                                        <p className="text-gray-900">
                                            {user?.created_at ? new Date(user.created_at).toLocaleDateString() : 'Unknown'}
                                        </p>
                                    </div>
                                    <div>
                                        <label className="block text-sm font-medium text-gray-700 mb-1">User ID</label>
                                        <p className="text-gray-900">{user?.user_id}</p>
                                    </div>
                                </div>

                                <div className="flex flex-col sm:flex-row gap-4 mt-6 pt-6 border-t">
                                    <Button onClick={() => setIsEditing(true)} className="w-full sm:w-auto">
                                        Edit Profile
                                    </Button>
                                    <Button variant="secondary" onClick={() => setShowPasswordChange(true)} className="w-full sm:w-auto">
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

                                <div className="flex flex-col sm:flex-row gap-4">
                                    <Button type="submit" className="w-full sm:w-auto">Save Changes</Button>
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
                                        className="w-full sm:w-auto"
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
                    <div className="fixed inset-0 bg-black/50 flex items-center justify-center p-4 z-50">
                        <div className="bg-white rounded-2xl shadow-xl max-w-md w-full p-4 sm:p-6 max-h-[90vh] overflow-y-auto">
                            <h2 className="text-xl font-bold text-gray-900 mb-4 sm:mb-6">Change Password</h2>

                            {error && <Alert type="error" className="mb-4">{error}</Alert>}
                            {success && <Alert type="success" className="mb-4">{success}</Alert>}

                            <form onSubmit={handlePasswordChange} className="space-y-4">
                                <Input
                                    label="Current Password"
                                    type="password"
                                    value={passwordForm.currentPassword}
                                    onChange={(e) => {
                                        setPasswordForm(prev => ({
                                            ...prev,
                                            currentPassword: e.target.value
                                        }));
                                        if (passwordErrors.currentPassword) {
                                            setPasswordErrors(prev => ({ ...prev, currentPassword: '' }));
                                        }
                                        if (error) setError('');
                                    }}
                                    error={passwordErrors.currentPassword}
                                    required
                                />

                                <Input
                                    label="New Password"
                                    type="password"
                                    value={passwordForm.newPassword}
                                    onChange={(e) => {
                                        setPasswordForm(prev => ({...prev, newPassword: e.target.value}));
                                        if (passwordErrors.newPassword) {
                                            setPasswordErrors(prev => ({ ...prev, newPassword: '' }));
                                        }
                                        if (error) setError('');
                                    }}
                                    error={passwordErrors.newPassword}
                                    required
                                />

                                <Input
                                    label="Confirm New Password"
                                    type="password"
                                    value={passwordForm.confirmNewPassword}
                                    onChange={(e) => {
                                        setPasswordForm(prev => ({
                                            ...prev,
                                            confirmNewPassword: e.target.value
                                        }));
                                        if (passwordErrors.confirmNewPassword) {
                                            setPasswordErrors(prev => ({ ...prev, confirmNewPassword: '' }));
                                        }
                                        if (error) setError('');
                                    }}
                                    error={passwordErrors.confirmNewPassword}
                                    required
                                />

                                <div className="flex flex-col sm:flex-row gap-4 pt-4">
                                    <Button
                                        type="submit"
                                        className="w-full sm:flex-1"
                                        loading={passwordChangeLoading}
                                        disabled={success ? true : false}
                                    >
                                        Change Password
                                    </Button>
                                    <Button
                                        type="button"
                                        variant="secondary"
                                        onClick={closePasswordModal}
                                        className="w-full sm:flex-1"
                                        disabled={passwordChangeLoading}
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