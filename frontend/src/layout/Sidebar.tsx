import { NavLink } from 'react-router-dom';
import { LayoutDashboard, ShieldAlert, BarChart3, SearchCode, Shield, Zap } from 'lucide-react';

interface SidebarProps {
  onOpenSimulator: () => void;
}

export const Sidebar = ({ onOpenSimulator }: SidebarProps) => {
  const navItems = [
    { name: 'Dashboard', path: '/dashboard', icon: LayoutDashboard },
    { name: 'Cases Queue', path: '/cases', icon: ShieldAlert },
    { name: 'Risk Analytics', path: '/analytics', icon: BarChart3 },
    { name: 'Investigation', path: '/investigation/1', icon: SearchCode },
  ];

  return (
    <aside className="w-64 bg-black border-r border-neutral-800 min-h-screen flex flex-col fixed left-0 top-0 bottom-0 z-30 shadow-2xl">
      {/* Brand Header */}
      <div className="h-16 flex items-center justify-between px-5 border-b border-neutral-800 bg-neutral-950">
        <div className="flex items-center gap-3">
          <div className="w-9 h-9 rounded-xl bg-[#E94F37] flex items-center justify-center text-white font-extrabold shadow-lg shadow-[#E94F37]/30">
            <Shield className="w-5 h-5 text-white" />
          </div>
          <div>
            <h1 className="font-bold text-sm text-white tracking-tight">
              Risk Sentinel
            </h1>
            <p className="text-[10px] font-semibold tracking-wider text-[#E94F37] uppercase">
              Fraud Engine v2.4
            </p>
          </div>
        </div>
        <div className="flex items-center gap-1">
          <span className="w-2 h-2 rounded-full bg-[#E94F37] animate-pulse"></span>
        </div>
      </div>

      {/* Navigation */}
      <nav className="flex-1 px-3 py-5 space-y-1 overflow-y-auto">
        <div className="px-3 pb-2 text-[10px] font-bold text-neutral-500 uppercase tracking-widest">
          Platform Menu
        </div>

        {navItems.map((item) => {
          const Icon = item.icon;
          return (
            <NavLink
              key={item.path}
              to={item.path}
              className={({ isActive }) =>
                `flex items-center gap-3 px-3.5 py-2.5 rounded-xl text-sm font-medium transition-all duration-200 ${
                  isActive
                    ? 'bg-[#E94F37] text-white shadow-lg shadow-[#E94F37]/30 font-semibold'
                    : 'text-neutral-400 hover:bg-neutral-900 hover:text-orange-400'
                }`
              }
            >
              <Icon className="w-4 h-4" />
              <span>{item.name}</span>
            </NavLink>
          );
        })}

        {/* Quick Action Simulator Button */}
        <div className="pt-6 px-1">
          <div className="p-3.5 rounded-2xl bg-neutral-950 border border-neutral-800">
            <div className="flex items-center gap-2 mb-2">
              <Zap className="w-4 h-4 text-[#E94F37]" />
              <span className="text-xs font-bold text-white">Rule Simulator</span>
            </div>
            <p className="text-[11px] text-neutral-400 leading-relaxed mb-3">
              Simulate live financial transactions to test real-time fraud scoring.
            </p>
            <button
              onClick={onOpenSimulator}
              className="w-full py-2 px-3 bg-neutral-900 hover:bg-[#E94F37] text-orange-400 hover:text-white rounded-xl text-xs font-bold transition-all duration-200 border border-orange-500/30 hover:border-[#E94F37] shadow-sm flex items-center justify-center gap-1.5"
            >
              <Zap className="w-3.5 h-3.5" />
              <span>Test Live Transaction</span>
            </button>
          </div>
        </div>
      </nav>

      {/* Analyst Status Footer */}
      <div className="p-4 border-t border-neutral-800 bg-neutral-950">
        <div className="flex items-center gap-3">
          <div className="w-8 h-8 rounded-full bg-[#E94F37]/20 border border-[#E94F37] text-[#E94F37] flex items-center justify-center text-xs font-bold">
            CA
          </div>
          <div className="text-xs">
            <p className="font-semibold text-white">Compliance Analyst</p>
            <p className="text-[10px] text-orange-400 flex items-center gap-1">
              <span className="w-1.5 h-1.5 rounded-full bg-[#E94F37]"></span> System Online
            </p>
          </div>
        </div>
      </div>
    </aside>
  );
};
