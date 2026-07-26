import React from 'react';
import { CasesFilterParams } from '../types';
import { Button } from '../../../components/ui/Button';
import { Search, RotateCcw, Filter } from 'lucide-react';

interface CasesFilterBarProps {
  filters: CasesFilterParams;
  onFilterChange: (newFilters: Partial<CasesFilterParams>) => void;
  onReset: () => void;
  searchValue: string;
  onSearchChange: (value: string) => void;
}

export const CasesFilterBar: React.FC<CasesFilterBarProps> = ({
  filters,
  onFilterChange,
  onReset,
  searchValue,
  onSearchChange,
}) => {
  return (
    <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-4 mb-6 space-y-4">
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-2 text-sm font-semibold text-[#393E41]">
          <Filter className="w-4 h-4 text-[#E94F37]" />
          <span>Filter Queue</span>
        </div>
        <Button variant="ghost" size="sm" onClick={onReset} className="text-gray-500 hover:text-[#E94F37]">
          <RotateCcw className="w-3.5 h-3.5 mr-1" />
          Reset Filters
        </Button>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-5 gap-3">
        {/* Search ID input */}
        <div className="relative">
          <Search className="w-3.5 h-3.5 text-gray-400 absolute left-3 top-1/2 -translate-y-1/2" />
          <input
            type="text"
            placeholder="Search Case or Txn ID..."
            value={searchValue}
            onChange={(e) => onSearchChange(e.target.value)}
            className="w-full pl-8 pr-3 py-1.5 text-xs bg-gray-50 border border-gray-200 rounded-lg focus:outline-none focus:ring-1 focus:ring-[#E94F37]"
          />
        </div>

        {/* Status Dropdown */}
        <select
          value={filters.status || ''}
          onChange={(e) => onFilterChange({ status: e.target.value || undefined, page: 0 })}
          className="px-3 py-1.5 text-xs bg-gray-50 border border-gray-200 rounded-lg text-[#393E41] focus:outline-none focus:ring-1 focus:ring-[#E94F37]"
        >
          <option value="">All Statuses</option>
          <option value="OPEN">OPEN</option>
          <option value="ASSIGNED">ASSIGNED</option>
          <option value="UNDER_REVIEW">UNDER_REVIEW</option>
          <option value="APPROVED">APPROVED</option>
          <option value="DECLINED">DECLINED</option>
          <option value="ESCALATED">ESCALATED</option>
          <option value="CLOSED">CLOSED</option>
        </select>

        {/* Priority Dropdown */}
        <select
          value={filters.priority || ''}
          onChange={(e) => onFilterChange({ priority: e.target.value || undefined, page: 0 })}
          className="px-3 py-1.5 text-xs bg-gray-50 border border-gray-200 rounded-lg text-[#393E41] focus:outline-none focus:ring-1 focus:ring-[#E94F37]"
        >
          <option value="">All Priorities</option>
          <option value="CRITICAL">CRITICAL</option>
          <option value="HIGH">HIGH</option>
          <option value="MEDIUM">MEDIUM</option>
          <option value="LOW">LOW</option>
        </select>

        {/* Risk Level Dropdown */}
        <select
          value={filters.riskLevel || ''}
          onChange={(e) => onFilterChange({ riskLevel: e.target.value || undefined, page: 0 })}
          className="px-3 py-1.5 text-xs bg-gray-50 border border-gray-200 rounded-lg text-[#393E41] focus:outline-none focus:ring-1 focus:ring-[#E94F37]"
        >
          <option value="">All Risk Levels</option>
          <option value="CRITICAL">CRITICAL</option>
          <option value="HIGH">HIGH</option>
          <option value="MEDIUM">MEDIUM</option>
          <option value="LOW">LOW</option>
        </select>

        {/* Assigned Analyst input */}
        <input
          type="text"
          placeholder="Filter Analyst..."
          value={filters.assignedTo || ''}
          onChange={(e) => onFilterChange({ assignedTo: e.target.value || undefined, page: 0 })}
          className="px-3 py-1.5 text-xs bg-gray-50 border border-gray-200 rounded-lg text-[#393E41] focus:outline-none focus:ring-1 focus:ring-[#E94F37]"
        />
      </div>
    </div>
  );
};
