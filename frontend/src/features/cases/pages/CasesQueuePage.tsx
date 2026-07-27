import { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import { PageHeader } from '../../../components/ui/PageHeader';
import { CasesFilterParams } from '../types';
import { useFraudCasesQueue } from '../api/useCasesData';
import { CasesFilterBar } from '../components/CasesFilterBar';
import { CasesTable } from '../components/CasesTable';
import { SkeletonTable } from '../components/SkeletonTable';
import { EmptyState } from '../../../components/ui/EmptyState';
import { Button } from '../../../components/ui/Button';
import { ShieldAlert, ChevronLeft, ChevronRight, CheckCircle2, ShieldCheck } from 'lucide-react';

export const CasesQueuePage = () => {
  const [searchParams, setSearchParams] = useSearchParams();

  // Tab state: 'active' vs 'approved'
  const initialTab = searchParams.get('tab') || 'active';
  const [activeTab, setActiveTab] = useState<'active' | 'approved'>(initialTab as 'active' | 'approved');

  // Read initial filters from URL params
  const initialStatus = searchParams.get('status') || undefined;
  const initialPriority = searchParams.get('priority') || undefined;
  const initialRiskLevel = searchParams.get('riskLevel') || undefined;
  const initialAssignedTo = searchParams.get('assignedTo') || undefined;
  const initialSearch = searchParams.get('search') || '';
  const initialPage = Number(searchParams.get('page')) || 0;
  const initialSort = searchParams.get('sort') || 'openedAt,desc';

  const [filters, setFilters] = useState<CasesFilterParams>({
    status: initialStatus,
    priority: initialPriority,
    riskLevel: initialRiskLevel,
    assignedTo: initialAssignedTo,
    page: initialPage,
    size: 20,
    sort: initialSort,
  });

  const [searchValue, setSearchValue] = useState<string>(initialSearch);

  // Debounced search effect (300ms)
  useEffect(() => {
    const handler = setTimeout(() => {
      const num = Number(searchValue.trim());
      if (!isNaN(num) && num > 0) {
        setFilters((prev) => ({ ...prev, caseId: num, transactionId: num, page: 0 }));
      } else {
        setFilters((prev) => ({ ...prev, caseId: undefined, transactionId: undefined, page: 0 }));
      }
    }, 300);

    return () => clearTimeout(handler);
  }, [searchValue]);

  // Sync state changes back to URL search params
  useEffect(() => {
    const params: Record<string, string> = {};
    params.tab = activeTab;
    if (filters.status) params.status = filters.status;
    if (filters.priority) params.priority = filters.priority;
    if (filters.riskLevel) params.riskLevel = filters.riskLevel;
    if (filters.assignedTo) params.assignedTo = filters.assignedTo;
    if (searchValue) params.search = searchValue;
    if (filters.page && filters.page > 0) params.page = String(filters.page);
    if (filters.sort) params.sort = filters.sort;

    setSearchParams(params, { replace: true });
  }, [filters, searchValue, activeTab, setSearchParams]);

  const activeFilters = {
    ...filters,
    status: activeTab === 'approved' ? (filters.status || 'APPROVED') : filters.status,
  };

  const { data, isLoading, isError, refetch } = useFraudCasesQueue(activeFilters);

  const handleFilterChange = (newFilters: Partial<CasesFilterParams>) => {
    setFilters((prev) => ({ ...prev, ...newFilters, page: newFilters.page ?? 0 }));
  };

  const handleResetFilters = () => {
    setFilters({ page: 0, size: 20, sort: 'openedAt,desc' });
    setSearchValue('');
  };

  const handleSortToggle = (sortField: string) => {
    const currentSort = filters.sort || 'openedAt,desc';
    const [field, dir] = currentSort.split(',');
    const newDir = field === sortField && dir === 'desc' ? 'asc' : 'desc';
    setFilters((prev) => ({ ...prev, sort: `${sortField},${newDir}`, page: 0 }));
  };

  const handleTabSwitch = (tab: 'active' | 'approved') => {
    setActiveTab(tab);
    setFilters((prev) => ({
      ...prev,
      status: tab === 'approved' ? 'APPROVED' : undefined,
      page: 0,
    }));
  };

  const totalPages = data?.totalPages ?? 0;
  const currentPage = data?.number ?? 0;
  
  // Filter cases: In Active Queue tab, keep OPEN, ASSIGNED, UNDER_REVIEW, ESCALATED, DECLINED, CLOSED. Only APPROVED moves to Approved Archive!
  const allCases = data?.content ?? [];
  const cases = activeTab === 'active' && !filters.status
    ? allCases.filter((c) => c.status !== 'APPROVED')
    : allCases;

  return (
    <div className="space-y-6">
      <PageHeader
        title="Fraud Case Queue & Audit Portal"
        subtitle="Operational workspace for compliance analysts to inspect, review, and resolve high-risk fraud cases"
      />

      {/* Primary Sub-Navigation Tabs */}
      <div className="flex items-center justify-between border-b border-neutral-800 pb-3">
        <div className="flex items-center gap-3">
          <button
            onClick={() => handleTabSwitch('active')}
            className={`px-4 py-2.5 rounded-xl text-xs font-extrabold flex items-center gap-2 transition-all ${
              activeTab === 'active'
                ? 'bg-[#E94F37] text-white shadow-lg shadow-[#E94F37]/30 border border-[#E94F37]'
                : 'bg-neutral-950 text-neutral-400 hover:text-white border border-neutral-800'
            }`}
          >
            <ShieldAlert className="w-4 h-4" />
            <span>Active Cases Queue</span>
          </button>

          <button
            onClick={() => handleTabSwitch('approved')}
            className={`px-4 py-2.5 rounded-xl text-xs font-extrabold flex items-center gap-2 transition-all ${
              activeTab === 'approved'
                ? 'bg-[#E94F37] text-white shadow-lg shadow-[#E94F37]/30 border border-[#E94F37]'
                : 'bg-neutral-950 text-neutral-400 hover:text-white border border-neutral-800'
            }`}
          >
            <ShieldCheck className="w-4 h-4" />
            <span>Approved & Finalized Archive</span>
          </button>
        </div>

        <div className="text-xs font-bold text-neutral-400">
          {activeTab === 'active' ? 'Active Operational Queue' : 'Approved Cases Archive'}
        </div>
      </div>

      {/* Filter Bar */}
      <CasesFilterBar
        filters={filters}
        onFilterChange={handleFilterChange}
        onReset={handleResetFilters}
        searchValue={searchValue}
        onSearchChange={setSearchValue}
      />

      {/* Main Table / State View */}
      {isLoading ? (
        <SkeletonTable />
      ) : isError ? (
        <div className="p-8 text-center bg-neutral-950 border border-[#E94F37] rounded-2xl">
          <ShieldAlert className="w-8 h-8 text-[#E94F37] mx-auto mb-2" />
          <p className="text-sm font-bold text-orange-400">Failed to load fraud cases queue</p>
          <div className="mt-3">
            <Button size="sm" variant="outline" onClick={() => refetch()}>
              Retry Connection
            </Button>
          </div>
        </div>
      ) : cases.length === 0 ? (
        <EmptyState
          icon={activeTab === 'active' ? <ShieldAlert className="w-8 h-8 text-orange-500" /> : <CheckCircle2 className="w-8 h-8 text-orange-500" />}
          title={activeTab === 'active' ? "No Active Queue Cases" : "No Approved Cases in Archive"}
          description={
            activeTab === 'active'
              ? "All flagged cases have been resolved, or no active cases match your current filter parameters."
              : "No approved or finalized cases found matching your active filter criteria."
          }
          action={
            <Button size="sm" variant="outline" onClick={handleResetFilters}>
              Clear Filters
            </Button>
          }
        />
      ) : (
        <div className="space-y-4">
          <CasesTable cases={cases} sortParam={filters.sort} onSortChange={handleSortToggle} />

          {/* Pagination Footer */}
          <div className="flex flex-col sm:flex-row items-center justify-between gap-4 bg-neutral-950 p-4 rounded-2xl border border-neutral-800 shadow-lg text-xs text-neutral-400">
            <div>
              Showing <span className="font-bold text-white">{cases.length}</span> of{' '}
              <span className="font-bold text-white">{data?.totalElements ?? 0}</span> {activeTab === 'active' ? 'active' : 'approved'} cases
            </div>

            <div className="flex items-center gap-2">
              <span className="mr-2">
                Page <span className="font-bold text-white">{currentPage + 1}</span> of{' '}
                <span className="font-bold text-white">{totalPages || 1}</span>
              </span>

              <Button
                size="sm"
                variant="outline"
                disabled={currentPage === 0}
                onClick={() => handleFilterChange({ page: currentPage - 1 })}
              >
                <ChevronLeft className="w-3.5 h-3.5 mr-1" /> Previous
              </Button>

              <Button
                size="sm"
                variant="outline"
                disabled={currentPage >= totalPages - 1}
                onClick={() => handleFilterChange({ page: currentPage + 1 })}
              >
                Next <ChevronRight className="w-3.5 h-3.5 ml-1" />
              </Button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default CasesQueuePage;
