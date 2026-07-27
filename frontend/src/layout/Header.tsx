import { useState } from 'react';
import { Search, Zap, Bell, ShieldCheck } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

interface HeaderProps {
  onOpenSimulator: () => void;
}

export const Header = ({ onOpenSimulator }: HeaderProps) => {
  const navigate = useNavigate();
  const [searchQuery, setSearchQuery] = useState('');

  const handleSearchSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!searchQuery.trim()) return;

    const query = searchQuery.trim();
    if (!isNaN(Number(query))) {
      navigate(`/investigation/${query}`);
    } else {
      navigate(`/cases`);
    }
  };

  return (
    <header className="h-16 bg-black border-b border-neutral-800 fixed top-0 right-0 left-64 z-20 px-6 flex items-center justify-between shadow-md">
      {/* Search Command Bar */}
      <form onSubmit={handleSearchSubmit} className="relative w-80">
        <Search className="w-4 h-4 text-orange-500 absolute left-3.5 top-1/2 -translate-y-1/2" />
        <input
          type="text"
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          placeholder="Search Txn ID (e.g. 1) or Case..."
          className="w-full pl-10 pr-4 py-2 bg-neutral-950 border border-neutral-800 rounded-xl text-xs text-white placeholder-neutral-500 focus:outline-none focus:border-[#E94F37] transition-all"
        />
      </form>

      {/* Right Telemetry Actions */}
      <div className="flex items-center gap-4">
        {/* Engine Telemetry Status Badge */}
        <div className="hidden md:flex items-center gap-2 px-3 py-1.5 rounded-xl bg-orange-500/10 border border-orange-500/30 text-orange-400 text-xs font-bold">
          <ShieldCheck className="w-4 h-4 text-[#E94F37]" />
          <span>Rule Engine Active</span>
        </div>

        {/* Quick Simulator CTA Button */}
        <button
          onClick={onOpenSimulator}
          className="px-4 py-2 bg-[#E94F37] hover:bg-[#D03E27] text-white text-xs font-bold rounded-xl shadow-lg shadow-[#E94F37]/30 transition-all flex items-center gap-2 active:scale-95"
        >
          <Zap className="w-4 h-4 text-white animate-pulse" />
          <span>Simulate Live Transaction</span>
        </button>

        {/* Notification Icon */}
        <button className="p-2 rounded-xl text-neutral-400 hover:text-white hover:bg-neutral-900 transition-colors relative">
          <Bell className="w-4 h-4" />
          <span className="w-2 h-2 rounded-full bg-[#E94F37] absolute top-2 right-2"></span>
        </button>
      </div>
    </header>
  );
};
