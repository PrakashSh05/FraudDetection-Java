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
import { ShieldAlert, ChevronLeft, ChevronRight } from 'lucide-react';

export const CasesQueuePage = () => {
  const [searchParams, setSearchParams] = useSearchParams();

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
    if (filters.status) params.status = filters.status;
    if (filters.priority) params.priority = filters.priority;
    if (filters.riskLevel) params.riskLevel = filters.riskLevel;
    if (filters.assignedTo) params.assignedTo = filters.assignedTo;
    if (searchValue) params.search = searchValue;
    if (filters.page && filters.page > 0) params.page = String(filters.page);
    if (filters.sort) params.sort = filters.sort;

    setSearchParams(params, { replace: true });
  }, [filters, searchValue, setSearchParams]);

  const { data, isLoading, isError, refetch } = useFraudCasesQueue(filters);

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

  const totalPages = data?.totalPages ?? 0;
  const currentPage = data?.number ?? 0;
  const cases = data?.content ?? [];

  return (
    <div className="space-y-6">
      <PageHeader
        title="Fraud Case Queue"
        subtitle="Operational workspace for compliance analysts to inspect, review, and resolve high-risk fraud cases"
      />

      {/* Filter Bar with URL search params & 300ms debounced search */}
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
        <div className="p-8 text-center bg-rose-50 border border-rose-200 rounded-xl">
          <ShieldAlert className="w-8 h-8 text-[#EF4444] mx-auto mb-2" />
          <p className="text-sm font-semibold text-[#EF4444]">Failed to load fraud cases queue</p>
          <div className="mt-3">
            <Button size="sm" variant="outline" onClick={() => refetch()}>
              Retry Connection
            </Button>
          </div>
        </div>
      ) : cases.length === 0 ? (
        <EmptyState
          icon={<ShieldAlert className="w-8 h-8" />}
          title="No Fraud Cases Found"
          description="No cases match your active filter criteria or search ID. Try adjusting your status or search terms."
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
          <div className="flex flex-col sm:flex-row items-center justify-between gap-4 bg-white p-4 rounded-xl border border-gray-100 shadow-sm text-xs text-gray-500">
            <div>
              Showing <span className="font-semibold text-[#393E41]">{cases.length}</span> of{' '}
              <span className="font-semibold text-[#393E41]">{data?.totalElements ?? 0}</span> fraud cases
            </div>

            <div className="flex items-center gap-2">
              <span className="mr-2">
                Page <span className="font-semibold text-[#393E41]">{currentPage + 1}</span> of{' '}
                <span className="font-semibold text-[#393E41]">{totalPages}</span>
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
