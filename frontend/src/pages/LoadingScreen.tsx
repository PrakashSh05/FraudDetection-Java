import { LoadingSpinner } from '../components/ui/LoadingSpinner';

export const LoadingScreen = () => {
  return (
    <div className="min-h-screen bg-slate-950 flex items-center justify-center">
      <LoadingSpinner size="lg" label="Initializing Risk Sentinel Engine..." />
    </div>
  );
};
