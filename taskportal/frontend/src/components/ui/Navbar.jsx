import { useNavigate } from 'react-router-dom';
import useAuthStore from '../../store/authStore';
import { Zap, LogOut, User } from 'lucide-react';
import toast from 'react-hot-toast';

export default function Navbar() {
  const { user, logout } = useAuthStore();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    toast.success('Signed out');
    navigate('/login');
  };

  return (
    <header className="border-b border-slate-800 bg-slate-950/80 backdrop-blur-md sticky top-0 z-40">
      <div className="max-w-6xl mx-auto px-4 h-14 flex items-center justify-between">
        {/* Logo */}
        <div className="flex items-center gap-2.5">
          <div className="w-8 h-8 bg-brand-500/20 border border-brand-500/30 rounded-lg flex items-center justify-center">
            <Zap size={15} className="text-brand-400" />
          </div>
          <span className="font-display font-bold text-white text-lg">TaskPortal</span>
        </div>

        {/* User + Logout */}
        <div className="flex items-center gap-3">
          <div className="hidden sm:flex items-center gap-2 text-sm text-slate-400">
            <div className="w-7 h-7 bg-brand-600/30 rounded-full flex items-center justify-center">
              <User size={13} className="text-brand-400" />
            </div>
            <span className="font-medium text-slate-300">{user?.fullName}</span>
          </div>
          <button
            onClick={handleLogout}
            className="flex items-center gap-1.5 text-slate-500 hover:text-slate-300 text-sm transition-colors"
          >
            <LogOut size={15} />
            <span className="hidden sm:inline">Sign out</span>
          </button>
        </div>
      </div>
    </header>
  );
}
