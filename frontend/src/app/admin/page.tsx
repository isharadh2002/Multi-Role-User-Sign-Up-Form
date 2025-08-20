'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { api } from '@/lib/api';
import { auth } from '@/lib/auth';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { Alert } from '@/components/ui/Alert';
import { User, Role } from '@/types';

export default function AdminPage() {
    const router = useRouter();
    const [users, setUsers] = useState<User[]>([]);
    const [roles, setRoles] = useState<Role[]>([]);
    const [loading, setLoading] = useState(true);
    const [activeTab, setActiveTab] = useState<'users' | 'roles'>('users');
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    const [showCreateRole, setShowCreateRole] = useState(false);
    const [newRole, setNewRole] = useState({ name: '', description: '' });
    const [deletingUserId, setDeletingUserId] = useState<number | null>(null);
    const [deletingRoleId, setDeletingRoleId] = useState<number | null>(null);

    useEffect(() => {
        if (!auth.isLoggedIn() || !auth.isAdmin()) {
            router.push('/dashboard');
            return;
        }

        loadData();
    }, [router]);

    const loadData = async () => {
        setLoading(true);
        try {
            const [usersResponse, rolesResponse] = await Promise.all([
                api.get<User[]>('/api/v1/admin/users'),
                api.get<Role[]>('/api/v1/roles')
            ]);

            if (usersResponse.success) {
                setUsers(usersResponse.data || []);
            }

            if (rolesResponse.success) {
                setRoles(rolesResponse.data || []);
            }
        } catch (error) {
            console.error('Failed to load data:', error);
            setError('Failed to load admin data');
        } finally {
            setLoading(false);
        }
    };

    const handleLogout = () => {
        auth.logout();
        router.push('/login');
    };

    const handleDeleteUser = async (userId: number) => {
        if (!confirm('Are you sure you want to delete this user? This action cannot be undone.')) {
            return;
        }

        setDeletingUserId(userId);
        try {
            setError('');
            setSuccess('');

            const response = await api.delete(`/api/v1/admin/users/${userId}`);

            if (response.success) {
                setSuccess('User deleted successfully');
                // Refresh users list
                const usersResponse = await api.get<User[]>('/api/v1/admin/users');
                if (usersResponse.success) {
                    setUsers(usersResponse.data || []);
                }
            } else {
                setError(response.message || 'Failed to delete user');
            }
        } catch (error) {
            setError('Network error. Please try again.');
        } finally {
            setDeletingUserId(null);
        }
    };

    const handleCreateRole = async (e: React.FormEvent) => {
        e.preventDefault();
        setError('');
        setSuccess('');

        if (!newRole.name.trim()) {
            setError('Role name is required');
            return;
        }

        try {
            const response = await api.post('/api/v1/admin/roles', {
                name: newRole.name,
                description: newRole.description
            });

            if (response.success) {
                setSuccess('Role created successfully');
                setShowCreateRole(false);
                setNewRole({ name: '', description: '' });

                // Refresh roles list
                const rolesResponse = await api.get<Role[]>('/api/v1/roles');
                if (rolesResponse.success) {
                    setRoles(rolesResponse.data || []);
                }
            } else {
                setError(response.message || 'Failed to create role');
            }
        } catch (error) {
            setError('Network error. Please try again.');
        }
    };

    const handleDeleteRole = async (roleId: number) => {
        if (!confirm('Are you sure you want to delete this role? This action cannot be undone.')) {
            return;
        }

        setDeletingRoleId(roleId);
        try {
            setError('');
            setSuccess('');

            const response = await api.delete(`/api/v1/admin/roles/${roleId}`);

            if (response.success) {
                setSuccess('Role deleted successfully');
                // Refresh roles list
                const rolesResponse = await api.get<Role[]>('/api/v1/roles');
                if (rolesResponse.success) {
                    setRoles(rolesResponse.data || []);
                }
            } else {
                setError(response.message || 'Failed to delete role');
            }
        } catch (error) {
            setError('Network error. Please try again.');
        } finally {
            setDeletingRoleId(null);
        }
    };

    const systemRoles = ['Admin', 'General User', 'Professional', 'Business Owner'];

    if (loading) {
        return (
            <div className="min-h-screen bg-gray-50 flex items-center justify-center">
                <div className="text-center">
                    <div className="animate-spin rounded-full h-16 w-16 border-b-2 border-red-600 mx-auto"></div>
                    <p className="mt-4 text-gray-600">Loading admin panel...</p>
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gray-50">
            {/* Header */}
            <header className="bg-white shadow-sm border-b">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="flex justify-between items-center h-16">
                        <h1 className="text-xl font-semibold text-gray-900">Admin Panel</h1>
                        <div className="flex items-center space-x-4">
                            <Button
                                variant="secondary"
                                onClick={() => router.push('/dashboard')}
                                className="text-sm px-4 py-2"
                            >
                                Back to Dashboard
                            </Button>
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

            <main className="max-w-7xl mx-auto py-8 px-4 sm:px-6 lg:px-8">
                {error && <Alert type="error" className="mb-6">{error}</Alert>}
                {success && <Alert type="success" className="mb-6">{success}</Alert>}

                {/* Tabs */}
                <div className="mb-8">
                    <div className="border-b border-gray-200">
                        <nav className="-mb-px flex space-x-8">
                            <button
                                onClick={() => setActiveTab('users')}
                                className={`py-2 px-1 border-b-2 font-medium text-sm transition-colors ${
                                    activeTab === 'users'
                                        ? 'border-red-500 text-red-600'
                                        : 'border-transparent text-gray-500 hover:text-gray-700'
                                }`}
                            >
                                User Management ({users.length})
                            </button>
                            <button
                                onClick={() => setActiveTab('roles')}
                                className={`py-2 px-1 border-b-2 font-medium text-sm transition-colors ${
                                    activeTab === 'roles'
                                        ? 'border-red-500 text-red-600'
                                        : 'border-transparent text-gray-500 hover:text-gray-700'
                                }`}
                            >
                                Role Management ({roles.length})
                            </button>
                        </nav>
                    </div>
                </div>

                {/* Users Tab */}
                {activeTab === 'users' && (
                    <div className="bg-white rounded-2xl shadow-xl overflow-hidden">
                        <div className="px-6 py-4 border-b border-gray-200">
                            <h2 className="text-lg font-semibold text-gray-900">All Users</h2>
                        </div>
                        <div className="overflow-x-auto">
                            <table className="min-w-full divide-y divide-gray-200">
                                <thead className="bg-gray-50">
                                <tr>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                                        User
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                                        Contact
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                                        Roles
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                                        Joined
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                                        Actions
                                    </th>
                                </tr>
                                </thead>
                                <tbody className="bg-white divide-y divide-gray-200">
                                {users.map((user) => (
                                    <tr key={user.userId} className="hover:bg-gray-50">
                                        <td className="px-6 py-4 whitespace-nowrap">
                                            <div className="flex items-center">
                                                <div className="w-10 h-10 bg-blue-600 rounded-full flex items-center justify-center text-white font-medium">
                                                    {user.first_name[0]}{user.last_name[0]}
                                                </div>
                                                <div className="ml-4">
                                                    <div className="text-sm font-medium text-gray-900">
                                                        {user.first_name} {user.last_name}
                                                    </div>
                                                    <div className="text-sm text-gray-500">ID: {user.userId}</div>
                                                </div>
                                            </div>
                                        </td>
                                        <td className="px-6 py-4 whitespace-nowrap">
                                            <div className="text-sm text-gray-900">{user.email}</div>
                                            <div className="text-sm text-gray-500">{user.phoneNumber || 'No phone'}</div>
                                        </td>
                                        <td className="px-6 py-4">
                                            <div className="flex flex-wrap gap-1">
                                                {user.roles.map(role => (
                                                    <span
                                                        key={role}
                                                        className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800"
                                                    >
                              {role}
                            </span>
                                                ))}
                                            </div>
                                        </td>
                                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                            {user.createdAt ? new Date(user.createdAt).toLocaleDateString() : 'Unknown'}
                                        </td>
                                        <td className="px-6 py-4 whitespace-nowrap">
                                            <Button
                                                variant="danger"
                                                onClick={() => handleDeleteUser(user.userId)}
                                                disabled={user.roles.includes('Admin') || deletingUserId === user.userId}
                                                loading={deletingUserId === user.userId}
                                                className="text-xs px-3 py-1"
                                            >
                                                {user.roles.includes('Admin') ? 'Protected' : 'Delete'}
                                            </Button>
                                        </td>
                                    </tr>
                                ))}
                                </tbody>
                            </table>
                        </div>
                    </div>
                )}

                {/* Roles Tab */}
                {activeTab === 'roles' && (
                    <div className="space-y-6">
                        <div className="flex justify-between items-center">
                            <h2 className="text-lg font-semibold text-gray-900">Role Management</h2>
                            <Button
                                variant="danger"
                                onClick={() => setShowCreateRole(true)}
                            >
                                Create New Role
                            </Button>
                        </div>

                        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                            {roles.map(role => (
                                <div key={role.role_id} className="bg-white rounded-xl shadow-md p-6">
                                    <div className="flex justify-between items-start mb-4">
                                        <h3 className="text-lg font-semibold text-gray-900">{role.name}</h3>
                                        {!systemRoles.includes(role.name) && (
                                            <Button
                                                variant="danger"
                                                onClick={() => handleDeleteRole(role.role_id)}
                                                loading={deletingRoleId === role.role_id}
                                                className="text-xs px-3 py-1"
                                            >
                                                Delete
                                            </Button>
                                        )}
                                    </div>
                                    <p className="text-gray-600 text-sm mb-4">{role.description}</p>
                                    <div className="flex items-center justify-between">
                                        <span className="text-sm text-gray-500">Users: {role.user_count || 0}</span>
                                        {systemRoles.includes(role.name) && (
                                            <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-yellow-100 text-yellow-800">
                        System Role
                      </span>
                                        )}
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>
                )}

                {/* Create Role Modal */}
                {showCreateRole && (
                    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
                        <div className="bg-white rounded-2xl shadow-xl max-w-md w-full p-6">
                            <h2 className="text-xl font-bold text-gray-900 mb-6">Create New Role</h2>

                            <form onSubmit={handleCreateRole} className="space-y-4">
                                <Input
                                    label="Role Name"
                                    value={newRole.name}
                                    onChange={(e) => setNewRole(prev => ({ ...prev, name: e.target.value }))}
                                    placeholder="Enter role name"
                                    required
                                />

                                <div className="space-y-2">
                                    <label className="block text-sm font-medium text-gray-700">Description</label>
                                    <textarea
                                        value={newRole.description}
                                        onChange={(e) => setNewRole(prev => ({ ...prev, description: e.target.value }))}
                                        rows={3}
                                        className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-500 focus:border-transparent transition-colors"
                                        placeholder="Enter role description (optional)"
                                    />
                                </div>

                                <div className="flex gap-4 pt-4">
                                    <Button type="submit" variant="danger" className="flex-1">
                                        Create Role
                                    </Button>
                                    <Button
                                        type="button"
                                        variant="secondary"
                                        onClick={() => {
                                            setShowCreateRole(false);
                                            setNewRole({ name: '', description: '' });
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
