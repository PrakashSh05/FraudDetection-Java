import { useState } from 'react';
import { Outlet } from 'react-router-dom';
import { Sidebar } from './Sidebar';
import { Header } from './Header';
import { SimulateTransactionModal } from '../components/SimulateTransactionModal';

export const MainLayout = () => {
  const [isSimulatorOpen, setIsSimulatorOpen] = useState(false);

  return (
    <div className="min-h-screen bg-black text-white flex">
      {/* Fixed Desktop Sidebar */}
      <Sidebar onOpenSimulator={() => setIsSimulatorOpen(true)} />

      {/* Main Content Area */}
      <div className="flex-1 pl-64 flex flex-col min-w-0">
        <Header onOpenSimulator={() => setIsSimulatorOpen(true)} />
        <main className="flex-1 pt-20 p-8 bg-black">
          <Outlet />
        </main>
      </div>

      {/* Live Transaction Simulation Modal */}
      <SimulateTransactionModal
        isOpen={isSimulatorOpen}
        onClose={() => setIsSimulatorOpen(false)}
      />
    </div>
  );
};
