'use client';

import { useRouter } from 'next/navigation';
import { useEffect } from 'react';
import { auth } from '@/lib/auth';
import { Button } from '@/components/ui/Button';

export default function HomePage() {
  const router = useRouter();

  useEffect(() => {
    if (auth.isLoggedIn()) {
      router.push('/dashboard');
    }
  }, [router]);

  return (
      <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100">
        {/* Header */}
        <nav className="bg-white shadow-sm">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
            <div className="flex justify-between items-center h-16">
              <h1 className="text-xl font-bold text-gray-900">UserHub</h1>
              <div className="flex items-center space-x-4">
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
            </div>
          </div>
        </nav>

        {/* Hero Section */}
        <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-20">
          <div className="text-center">
            <h1 className="text-5xl md:text-6xl font-bold text-gray-900 mb-6">
              Multi-Role User
              <span className="text-blue-600"> Registration</span>
            </h1>

            <p className="text-xl text-gray-600 mb-12 max-w-3xl mx-auto">
              A secure and modern user registration system with role-based access control.
              Join as a General User, Professional, Business Owner, or Administrator.
            </p>

            <div className="flex flex-col sm:flex-row gap-4 justify-center mb-16">
              <Button
                  onClick={() => router.push('/register')}
                  className="text-lg px-8 py-4"
              >
                Create Account
              </Button>
              <Button
                  variant="secondary"
                  onClick={() => router.push('/login')}
                  className="text-lg px-8 py-4"
              >
                Sign In
              </Button>
            </div>

            {/* Features */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8 mt-20">
              {[
                { title: 'General User', desc: 'Standard user with basic access permissions', icon: '👤' },
                { title: 'Professional', desc: 'Professional user with advanced features', icon: '💼' },
                { title: 'Business Owner', desc: 'Business owner with management capabilities', icon: '🏢' },
                { title: 'Administrator', desc: 'System admin with full access', icon: '🛡️' }
              ].map((feature) => (
                  <div key={feature.title} className="bg-white rounded-2xl p-6 shadow-lg">
                    <div className="text-4xl mb-4">{feature.icon}</div>
                    <h3 className="text-lg font-semibold text-gray-900 mb-2">{feature.title}</h3>
                    <p className="text-gray-600 text-sm">{feature.desc}</p>
                  </div>
              ))}
            </div>
          </div>
        </main>
      </div>
  );
}