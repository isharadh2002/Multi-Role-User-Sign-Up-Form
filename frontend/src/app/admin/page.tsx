'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { api } from '@/lib/api';
import { auth } from '@/lib/auth';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { Alert } from '@/components/ui/Alert';
import { ConfirmationModal } from '@/components/ui/ConfirmationModal';
import { User, Role } from '@/types';

interface ConfirmationState {
    isOpen: boolean;
    type: 'deleteUser' | 'deleteRole' | null;
    title: string;
    message: string;
    targetId: number | null;
    targetName?: string;
}

export default function AdminPage() {
    const router = useRouter();
    const [users, setUsers] = useState<User[]>([]);
    const [roles, setRoles] = useState<Role[]>([]);
    const [loading, setLoading] = useState(true);
    const [activeTab, setActiveTab] = useState<'users' | 'roles'>('users');
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    // Role management states
    const [showCreateRole, setShowCreateRole] = useState(false);
    const [showEditRole, setShowEditRole] = useState(false);
    const [selectedRole, setSelectedRole] = useState<Role | null>(null);
    const [newRole, setNewRole] = useState({ name: '', description: '' });
    const [editRole, setEditRole] = useState({ name: '', description: '' });

    // Loading states
    const [deletingUserId, setDeletingUserId] = useState<number | null>(null);
    const [deletingRoleId, setDeletingRoleId] = useState<number | null>(null);
    const [creatingRole, setCreatingRole] = useState(false);
    const [updatingRole, setUpdatingRole] = useState(false);

    // Confirmation modal state
    const [confirmation, setConfirmation] = useState<ConfirmationState>({
        isOpen: false,
        type: null,
        title: '',
        message: '',
        targetId: null,
        targetName: ''
    });

    useEffect(() => {
        document.title = 'UserHub - Admin Panel';
    }, []);

    useEffect(() => {
        if (error || success) {
            const timer = setTimeout(() => {
                setError('');
                setSuccess('');
            }, 2000);

            return () => clearTimeout(timer); // Cleanup if component re-renders before 5s
        }
    }, [error, success]);

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

    const clearMessages = () => {
        setError('');
        setSuccess('');
    };

    const handleLogout = () => {
        auth.logout();
        router.push('/');
    };

    // User deletion with confirmation modal
    const showUserDeleteConfirmation = (user: User) => {
        setConfirmation({
            isOpen: true,
            type: 'deleteUser',
            title: 'Delete User',
            message: `Are you sure you want to delete "${user.first_name} ${user.last_name}"? This action cannot be undone and will permanently remove all user data.`,
            targetId: user.user_id,
            targetName: `${user.first_name} ${user.last_name}`
        });
        clearMessages();
    };

    const handleDeleteUser = async () => {
        if (!confirmation.targetId) return;

        setDeletingUserId(confirmation.targetId);

        try {
            const response = await api.delete(`/api/v1/admin/users/${confirmation.targetId}`);

            if (response.success) {
                setSuccess(`User "${confirmation.targetName}" deleted successfully`);
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
            closeConfirmation();
        }
    };

    // Role deletion with confirmation modal
    const showRoleDeleteConfirmation = (role: Role) => {
        setConfirmation({
            isOpen: true,
            type: 'deleteRole',
            title: 'Delete Role',
            message: `Are you sure you want to delete the role "${role.name}"? This action cannot be undone and may affect users who have this role assigned.`,
            targetId: role.role_id,
            targetName: role.name
        });
        clearMessages();
    };

    const handleDeleteRole = async () => {
        if (!confirmation.targetId) return;

        setDeletingRoleId(confirmation.targetId);

        try {
            const response = await api.delete(`/api/v1/admin/roles/${confirmation.targetId}`);

            if (response.success) {
                setSuccess(`Role "${confirmation.targetName}" deleted successfully`);
                // Refresh roles list
                await loadRoles();
            } else {
                setError(response.message || 'Failed to delete role');
            }
        } catch (error) {
            setError('Network error. Please try again.');
        } finally {
            setDeletingRoleId(null);
            closeConfirmation();
        }
    };

    const closeConfirmation = () => {
        setConfirmation({
            isOpen: false,
            type: null,
            title: '',
            message: '',
            targetId: null,
            targetName: ''
        });
    };

    const handleConfirmAction = () => {
        if (confirmation.type === 'deleteUser') {
            handleDeleteUser();
        } else if (confirmation.type === 'deleteRole') {
            handleDeleteRole();
        }
    };

    const handleCreateRole = async (e: React.FormEvent) => {
        e.preventDefault();
        clearMessages();

        if (!newRole.name.trim()) {
            setError('Role name is required');
            return;
        }

        setCreatingRole(true);
        try {
            const response = await api.post<Role>('/api/v1/admin/roles', {
                name: newRole.name.trim(),
                description: newRole.description.trim()
            });

            if (response.success) {
                setSuccess('Role created successfully');
                setShowCreateRole(false);
                setNewRole({ name: '', description: '' });
                // Refresh roles list
                await loadRoles();
            } else {
                setError(response.message || 'Failed to create role');
            }
        } catch (error) {
            setError('Network error. Please try again.');
        } finally {
            setCreatingRole(false);
        }
    };

    const handleEditRole = (role: Role) => {
        setSelectedRole(role);
        setEditRole({ name: role.name, description: role.description || '' });
        setShowEditRole(true);
        clearMessages();
    };

    const handleUpdateRole = async (e: React.FormEvent) => {
        e.preventDefault();
        clearMessages();

        if (!selectedRole || !editRole.name.trim()) {
            setError('Role name is required');
            return;
        }

        setUpdatingRole(true);
        try {
            const response = await api.put<Role>(`/api/v1/admin/roles/${selectedRole.role_id}`, {
                name: editRole.name.trim(),
                description: editRole.description.trim()
            });

            if (response.success) {
                setSuccess('Role updated successfully');
                setShowEditRole(false);
                setSelectedRole(null);
                setEditRole({ name: '', description: '' });
                // Refresh roles list
                await loadRoles();
            } else {
                setError(response.message || 'Failed to update role');
            }
        } catch (error) {
            setError('Network error. Please try again.');
        } finally {
            setUpdatingRole(false);
        }
    };

    const loadRoles = async () => {
        try {
            const rolesResponse = await api.get<Role[]>('/api/v1/roles');
            if (rolesResponse.success) {
                setRoles(rolesResponse.data || []);
            }
        } catch (error) {
            console.error('Failed to load roles:', error);
        }
    };

    const closeCreateModal = () => {
        setShowCreateRole(false);
        setNewRole({ name: '', description: '' });
        clearMessages();
    };

    const closeEditModal = () => {
        setShowEditRole(false);
        setSelectedRole(null);
        setEditRole({ name: '', description: '' });
        clearMessages();
    };

    const systemRoles = ['Admin', 'General User', 'Professional', 'Business Owner'];

    const isUserDeletionInProgress = (userId: number) => {
        return deletingUserId === userId || (confirmation.isOpen && confirmation.targetId === userId && confirmation.type === 'deleteUser');
    };

    const isRoleDeletionInProgress = (roleId: number) => {
        return deletingRoleId === roleId || (confirmation.isOpen && confirmation.targetId === roleId && confirmation.type === 'deleteRole');
    };

    if (loading) {
        return (
            <div className="min-h-screen bg-gray-50 flex items-center justify-center">
                <div className="text-center">
                    <div className="animate-spin rounded-full h-16 w-16 border-b-2 border-blue-600 mx-auto"></div>
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
                                        ? 'border-blue-500 text-blue-600'
                                        : 'border-transparent text-gray-500 hover:text-gray-700'
                                }`}
                            >
                                User Management ({users.length})
                            </button>
                            <button
                                onClick={() => setActiveTab('roles')}
                                className={`py-2 px-1 border-b-2 font-medium text-sm transition-colors ${
                                    activeTab === 'roles'
                                        ? 'border-blue-500 text-blue-600'
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
                                    <tr key={user.user_id} className="hover:bg-gray-50">
                                        <td className="px-6 py-4 whitespace-nowrap">
                                            <div className="flex items-center">
                                                <div className="w-10 h-10 bg-blue-600 rounded-full flex items-center justify-center text-white font-medium">
                                                    {user.first_name[0]}{user.last_name[0]}
                                                </div>
                                                <div className="ml-4">
                                                    <div className="text-sm font-medium text-gray-900">
                                                        {user.first_name} {user.last_name}
                                                    </div>
                                                    <div className="text-sm text-gray-500">ID: {user.user_id}</div>
                                                </div>
                                            </div>
                                        </td>
                                        <td className="px-6 py-4 whitespace-nowrap">
                                            <div className="text-sm text-gray-900">{user.email}</div>
                                            <div className="text-sm text-gray-500">{user.phone_number || 'No phone'}</div>
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
                                            {user.created_at ? new Date(user.created_at).toLocaleDateString() : 'Unknown'}
                                        </td>
                                        <td className="px-6 py-4 whitespace-nowrap">
                                            <Button
                                                variant="danger"
                                                onClick={() => showUserDeleteConfirmation(user)}
                                                disabled={user.roles.includes('Admin') || isUserDeletionInProgress(user.user_id)}
                                                loading={isUserDeletionInProgress(user.user_id)}
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
                                onClick={() => setShowCreateRole(true)}
                                className="bg-blue-600 hover:bg-blue-700"
                            >
                                Create New Role
                            </Button>
                        </div>

                        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                            {roles.map(role => (
                                <div key={role.role_id} className="bg-white rounded-xl shadow-md p-6">
                                    <div className="flex justify-between items-start mb-4">
                                        <h3 className="text-lg font-semibold text-gray-900">{role.name}</h3>
                                        <div className="flex gap-2">
                                            {!systemRoles.includes(role.name) && (
                                                <>
                                                    <Button
                                                        variant="secondary"
                                                        onClick={() => handleEditRole(role)}
                                                        className="text-xs px-3 py-1"
                                                    >
                                                        Edit
                                                    </Button>
                                                    <Button
                                                        variant="danger"
                                                        onClick={() => showRoleDeleteConfirmation(role)}
                                                        loading={isRoleDeletionInProgress(role.role_id)}
                                                        disabled={isRoleDeletionInProgress(role.role_id)}
                                                        className="text-xs px-3 py-1"
                                                    >
                                                        Delete
                                                    </Button>
                                                </>
                                            )}
                                        </div>
                                    </div>
                                    <p className="text-gray-600 text-sm mb-4">{role.description || 'No description'}</p>
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

                {/* Confirmation Modal */}
                <ConfirmationModal
                    isOpen={confirmation.isOpen}
                    onClose={closeConfirmation}
                    onConfirm={handleConfirmAction}
                    title={confirmation.title}
                    message={confirmation.message}
                    confirmText="Delete"
                    cancelText="Cancel"
                    isLoading={confirmation.type === 'deleteUser' ? !!deletingUserId : !!deletingRoleId}
                    variant="danger"
                />

                {/* Create Role Modal */}
                {showCreateRole && (
                    <div className="fixed inset-0 bg-black/50 flex items-center justify-center p-4 z-50">
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
                                        className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-colors text-gray-900"
                                        placeholder="Enter role description (optional)"
                                    />
                                </div>

                                <div className="flex gap-4 pt-4">
                                    <Button
                                        type="submit"
                                        className="flex-1 bg-blue-600 hover:bg-blue-700"
                                        loading={creatingRole}
                                    >
                                        Create Role
                                    </Button>
                                    <Button
                                        type="button"
                                        variant="secondary"
                                        onClick={closeCreateModal}
                                        className="flex-1"
                                        disabled={creatingRole}
                                    >
                                        Cancel
                                    </Button>
                                </div>
                            </form>
                        </div>
                    </div>
                )}

                {/* Edit Role Modal */}
                {showEditRole && selectedRole && (
                    <div className="fixed inset-0 bg-black/50 flex items-center justify-center p-4 z-50">
                        <div className="bg-white rounded-2xl shadow-xl max-w-md w-full p-6">
                            <h2 className="text-xl font-bold text-gray-900 mb-6">Edit Role</h2>

                            <form onSubmit={handleUpdateRole} className="space-y-4">
                                <Input
                                    label="Role Name"
                                    value={editRole.name}
                                    onChange={(e) => setEditRole(prev => ({ ...prev, name: e.target.value }))}
                                    placeholder="Enter role name"
                                    required
                                />

                                <div className="space-y-2">
                                    <label className="block text-sm font-medium text-gray-700">Description</label>
                                    <textarea
                                        value={editRole.description}
                                        onChange={(e) => setEditRole(prev => ({ ...prev, description: e.target.value }))}
                                        rows={3}
                                        className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-colors text-gray-900"
                                        placeholder="Enter role description (optional)"
                                    />
                                </div>

                                <div className="flex gap-4 pt-4">
                                    <Button
                                        type="submit"
                                        className="flex-1 bg-blue-600 hover:bg-blue-700"
                                        loading={updatingRole}
                                    >
                                        Update Role
                                    </Button>
                                    <Button
                                        type="button"
                                        variant="secondary"
                                        onClick={closeEditModal}
                                        className="flex-1"
                                        disabled={updatingRole}
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