import { Link } from 'react-router-dom';
import { Button } from '../components/ui/Button';
import { AlertOctagon } from 'lucide-react';

export const NotFoundPage = () => {
  return (
    <div className="min-h-screen bg-[#F6F7EB] flex flex-col items-center justify-center p-6 text-center">
      <div className="p-4 bg-red-50 text-[#E94F37] rounded-full mb-4">
        <AlertOctagon className="w-12 h-12" />
      </div>
      <h1 className="text-4xl font-extrabold text-[#393E41] tracking-tight">404 - Page Not Found</h1>
      <p className="mt-2 text-sm text-gray-500 max-w-md">
        The resource or page you requested could not be located on the Transaction Risk Analysis Platform.
      </p>
      <div className="mt-6">
        <Link to="/dashboard">
          <Button variant="primary">Return to Dashboard</Button>
        </Link>
      </div>
    </div>
  );
};
