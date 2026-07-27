import { Link } from 'react-router-dom';
import { Button } from '../components/ui/Button';
import { AlertOctagon } from 'lucide-react';

export const NotFoundPage = () => {
  return (
    <div className="min-h-screen bg-slate-950 flex flex-col items-center justify-center p-6 text-center">
      <div className="p-4 bg-rose-500/10 text-rose-400 rounded-2xl mb-4 border border-rose-500/20 shadow-xl">
        <AlertOctagon className="w-12 h-12" />
      </div>
      <h1 className="text-4xl font-extrabold text-white tracking-tight">404 - Resource Not Found</h1>
      <p className="mt-2 text-xs text-slate-400 max-w-md leading-relaxed">
        The route or telemetry resource you requested does not exist on the Transaction Risk Analysis Platform.
      </p>
      <div className="mt-6">
        <Link to="/dashboard">
          <Button variant="primary">Return to Executive Dashboard</Button>
        </Link>
      </div>
    </div>
  );
};
