import React from 'react';
import { NavLink } from 'react-router-dom';
import { LayoutDashboard, ShieldAlert, BarChart3, SearchCode, Shield } from 'lucide-react';

export const Sidebar: React.FC = () => {
  const navItems = [
    { name: 'Dashboard', path: '/dashboard', icon: LayoutDashboard },
    { name: 'Cases', path: '/cases', icon: ShieldAlert },
    { name: 'Analytics', path: '/analytics', icon: BarChart3 },
    { name: 'Investigation', path: '/investigation/101', icon: SearchCode },
  ];

  return (
    <aside className="w-64 bg-white border-r border-gray-200 min-h-screen flex flex-col fixed left-0 top-0 bottom-0 z-20">
      {/* Brand Header */}
      <div className="h-16 flex items-center gap-3 px-6 border-b border-gray-100">
        <div className="w-8 h-8 rounded-lg bg-[#E94F37] flex items-center justify-center text-white font-bold">
          <Shield className="w-5 h-5" />
        </div>
        <div>
          <h1 className="font-bold text-sm text-[#393E41] tracking-tight">Risk Sentinel</h1>
          <p className="text-[10px] font-semibold uppercase tracking-wider text-gray-400">Fraud Engine V2</p>
        </div>
      </div>

      {/* Navigation */}
      <nav className="flex-1 px-4 py-6 space-y-1">
        <div className="px-3 pb-2 text-[10px] font-bold text-gray-400 uppercase tracking-wider">
          Platform Menu
        </div>
        {navItems.map((item) => {
          const Icon = item.icon;
          return (
            <NavLink
              key={item.path}
              to={item.path}
              className={({ isActive }) =>
                `flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-colors ${
                  isActive
                    ? 'bg-[#E94F37] text-white shadow-sm'
                    : 'text-[#393E41] hover:bg-gray-50 hover:text-[#E94F37]'
                }`
              }
            >
              <Icon className="w-4 h-4" />
              <span>{item.name}</span>
            </NavLink>
          );
        })}
      </nav>

      {/* User / Version Footer */}
      <div className="p-4 border-t border-gray-100 bg-gray-50/50">
        <div className="flex items-center gap-3">
          <div className="w-8 h-8 rounded-full bg-[#393E41] text-white flex items-center justify-center text-xs font-bold">
            CA
          </div>
          <div className="text-xs">
            <p className="font-semibold text-[#393E41]">Compliance Analyst</p>
            <p className="text-gray-400">System v2.4.0</p>
          </div>
        </div>
      </div>
    </aside>
  );
};
