import { Navigate } from 'react-router-dom';
import useAuthStore from '../store/authStore';

/**
 * Wrapper component that redirects to /login if the user is not authenticated.
 */
export default function ProtectedRoute({ children }) {
  const { isAuthenticated } = useAuthStore();

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  return children;
}
