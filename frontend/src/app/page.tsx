'use client';

import { useRouter } from 'next/navigation';
import { useEffect, useState } from 'react';
import { auth } from '@/lib/auth';
import { Button } from '@/components/ui/Button';

export default function HomePage() {
  const router = useRouter();
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);

  useEffect(() => {
    if (auth.isLoggedIn()) {
      router.push('/dashboard');
    }
  }, [router]);

  const toggleMobileMenu = () => {
    setIsMobileMenuOpen(!isMobileMenuOpen);
  };

  return (
      <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100">
        {/* Header */}
        <nav className="bg-white shadow-sm">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
            <div className="flex justify-between items-center h-16">
              <h1 className="text-xl font-bold text-gray-900">UserHub</h1>

              {/* Desktop Navigation */}
              <div className="hidden md:flex items-center space-x-4">
                <Button
                    variant="secondary"
                    onClick={() => router.push('/login')}
                >
                  Sign In
                </Button>
                <Button onClick={() => router.push('/register')}>
                  Get Started
                </Button>
              </div>

              {/* Mobile menu button */}
              <div className="md:hidden">
                <button
                    onClick={toggleMobileMenu}
                    className="inline-flex items-center justify-center p-2 rounded-md text-gray-500 hover:text-gray-900 hover:bg-gray-100 focus:outline-none focus:ring-2 focus:ring-inset focus:ring-blue-500"
                >
                  <span className="sr-only">Open main menu</span>
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
                <button
                    onClick={() => {
                      router.push('/login');
                      setIsMobileMenuOpen(false);
                    }}
                    className="block w-full text-left px-3 py-2 rounded-md text-base font-medium text-gray-700 hover:text-gray-900 hover:bg-gray-50 transition-colors"
                >
                  Sign In
                </button>
                <button
                    onClick={() => {
                      router.push('/register');
                      setIsMobileMenuOpen(false);
                    }}
                    className="block w-full text-left px-3 py-2 rounded-md text-base font-medium text-white bg-blue-600 hover:bg-blue-700 transition-colors"
                >
                  Get Started
                </button>
              </div>
            </div>
          </div>
        </nav>

        {/* Hero Section */}
        <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12 sm:py-16 lg:py-20">
          <div className="text-center">
            <h1 className="text-4xl sm:text-5xl md:text-6xl font-bold text-gray-900 mb-4 sm:mb-6">
              Multi-Role User
              <span className="text-blue-600 block sm:inline"> Registration</span>
            </h1>

            <p className="text-lg sm:text-xl text-gray-600 mb-8 sm:mb-12 max-w-3xl mx-auto">
              A secure and modern user registration system with role-based access control.
              Join as a General User, Professional, Business Owner, or Administrator.
            </p>

            <div className="flex flex-col sm:flex-row gap-4 justify-center mb-12 sm:mb-16">
              <Button
                  onClick={() => router.push('/register')}
                  className="text-base sm:text-lg px-6 sm:px-8 py-3 sm:py-4 w-full sm:w-auto"
              >
                Create Account
              </Button>
              <Button
                  variant="secondary"
                  onClick={() => router.push('/login')}
                  className="text-base sm:text-lg px-6 sm:px-8 py-3 sm:py-4 w-full sm:w-auto"
              >
                Sign In
              </Button>
            </div>

            {/* Features */}
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6 sm:gap-8 mt-16 sm:mt-20">
              {[
                { title: 'General User', desc: 'Standard user with basic access permissions', icon: '👤' },
                { title: 'Professional', desc: 'Professional user with advanced features', icon: '💼' },
                { title: 'Business Owner', desc: 'Business owner with management capabilities', icon: '🏢' },
                { title: 'Administrator', desc: 'System admin with full access', icon: '🛡️' }
              ].map((feature) => (
                  <div key={feature.title} className="bg-white rounded-2xl p-4 sm:p-6 shadow-lg">
                    <div className="text-3xl sm:text-4xl mb-3 sm:mb-4">{feature.icon}</div>
                    <h3 className="text-base sm:text-lg font-semibold text-gray-900 mb-2">{feature.title}</h3>
                    <p className="text-gray-600 text-sm">{feature.desc}</p>
                  </div>
              ))}
            </div>
          </div>
        </main>
      </div>
  );
}