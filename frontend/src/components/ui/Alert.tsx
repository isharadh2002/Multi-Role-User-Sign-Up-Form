import React from 'react';

interface AlertProps {
    type: 'success' | 'error';
    children: React.ReactNode;
    className?: string;
}

export function Alert({ type, children, className = '' }: AlertProps) {
    const typeClasses = {
        success: 'bg-green-50 border-green-200 text-green-700',
        error: 'bg-red-50 border-red-200 text-red-700',
    };

    return (
        <div className={`p-4 border rounded-lg text-sm ${typeClasses[type]} ${className}`}>
            {children}
        </div>
    );
}