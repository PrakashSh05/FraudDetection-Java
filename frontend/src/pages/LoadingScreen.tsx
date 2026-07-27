import { LoadingSpinner } from '../components/ui/LoadingSpinner';

export const LoadingScreen = () => {
  return (
    <div className="min-h-screen bg-[#F6F7EB] flex items-center justify-center">
      <LoadingSpinner size="lg" label="Initializing Risk Platform Engine..." />
    </div>
  );
};
