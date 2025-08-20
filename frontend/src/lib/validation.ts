export const validateEmail = (email: string): string | null => {
    if (!email) return 'Email is required';
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) return 'Please enter a valid email address';
    return null;
};

export const validatePassword = (password: string): string | null => {
    if (!password) return 'Password is required';
    if (password.length < 8) return 'Password must be at least 8 characters long';

    const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&_#])[A-Za-z\d@$!%*?&_#]+$/;
    if (!passwordRegex.test(password)) {
        return 'Password must contain uppercase, lowercase, number, and special character';
    }
    return null;
};

export const validateRequired = (value: string, fieldName: string): string | null => {
    if (!value || !value.trim()) return `${fieldName} is required`;
    return null;
};

export const validatePhone = (phone: string): string | null => {
    if (!phone) return null; // Optional field
    const phoneRegex = /^\+?[1-9]\d{1,14}$/;
    if (!phoneRegex.test(phone)) return 'Please enter a valid phone number with country code';
    return null;
};