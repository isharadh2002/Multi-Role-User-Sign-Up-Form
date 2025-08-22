import React from 'react';
import { Button } from './Button';

interface ConfirmationModalProps {
    isOpen: boolean;
    onClose: () => void;
    onConfirm: () => void;
    title: string;
    message: string;
    confirmText?: string;
    cancelText?: string;
    isLoading?: boolean;
    variant?: 'danger' | 'warning';
}

export function ConfirmationModal({
                                      isOpen,
                                      onClose,
                                      onConfirm,
                                      title,
                                      message,
                                      confirmText = 'Confirm',
                                      cancelText = 'Cancel',
                                      isLoading = false,
                                      variant = 'danger'
                                  }: ConfirmationModalProps) {
    if (!isOpen) return null;

    return (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center p-4 z-50">
            <div className="bg-white rounded-2xl shadow-xl max-w-md w-full p-6">
                <div className="flex items-center mb-4">
                    <div className={`w-12 h-12 rounded-full flex items-center justify-center mr-4 ${
                        variant === 'danger' ? 'bg-red-100' : 'bg-yellow-100'
                    }`}>
                        <svg
                            className={`w-6 h-6 ${variant === 'danger' ? 'text-red-600' : 'text-yellow-600'}`}
                            fill="none"
                            viewBox="0 0 24 24"
                            stroke="currentColor"
                        >
                            <path
                                strokeLinecap="round"
                                strokeLinejoin="round"
                                strokeWidth={2}
                                d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L3.732 16.5c-.77.833.192 2.5 1.732 2.5z"
                            />
                        </svg>
                    </div>
                    <h2 className="text-xl font-bold text-gray-900">{title}</h2>
                </div>

                <p className="text-gray-600 mb-6">{message}</p>

                <div className="flex gap-4">
                    <Button
                        variant={variant}
                        onClick={onConfirm}
                        loading={isLoading}
                        className="flex-1"
                        disabled={isLoading}
                    >
                        {confirmText}
                    </Button>
                    <Button
                        variant="secondary"
                        onClick={onClose}
                        className="flex-1"
                        disabled={isLoading}
                    >
                        {cancelText}
                    </Button>
                </div>
            </div>
        </div>
    );
}