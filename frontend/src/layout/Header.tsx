import React from 'react';
import { Bell, Search, RefreshCw } from 'lucide-react';

export const Header: React.FC = () => {
  return (
    <header className="h-16 bg-white border-b border-gray-200 px-8 flex items-center justify-between sticky top-0 z-10">
      {/* Search Bar */}
      <div className="relative w-72">
        <Search className="w-4 h-4 text-gray-400 absolute left-3 top-1/2 -translate-y-1/2" />
        <input
          type="text"
          placeholder="Search transaction ID or case..."
          className="w-full pl-9 pr-4 py-1.5 text-xs bg-gray-50 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#E94F37] focus:bg-white transition-all"
        />
      </div>

      {/* Right Action Icons */}
      <div className="flex items-center gap-3">
        <button className="p-2 text-gray-500 hover:text-[#E94F37] hover:bg-gray-50 rounded-lg transition-colors" title="Refresh Engine Status">
          <RefreshCw className="w-4 h-4" />
        </button>
        <button className="p-2 text-gray-500 hover:text-[#E94F37] hover:bg-gray-50 rounded-lg transition-colors relative" title="Notifications">
          <Bell className="w-4 h-4" />
          <span className="w-2 h-2 bg-[#EF4444] rounded-full absolute top-1.5 right-1.5"></span>
        </button>
        <div className="h-4 w-px bg-gray-200 mx-1"></div>
        <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-[11px] font-semibold bg-emerald-50 text-[#10B981] border border-emerald-200">
          <span className="w-1.5 h-1.5 rounded-full bg-[#10B981] animate-pulse"></span>
          Engine Online
        </span>
      </div>
    </header>
  );
};
