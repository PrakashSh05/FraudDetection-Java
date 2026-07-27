export const SkeletonInvestigation = () => {
  return (
    <div className="space-y-6 animate-pulse">
      <div className="h-8 bg-gray-200 rounded w-64"></div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="bg-white p-6 rounded-xl border border-gray-100 space-y-4">
          <div className="h-5 bg-gray-200 rounded w-40"></div>
          <div className="grid grid-cols-2 gap-4">
            {Array.from({ length: 6 }).map((_, i) => (
              <div key={i} className="h-10 bg-gray-50 rounded-lg"></div>
            ))}
          </div>
        </div>

        <div className="bg-white p-6 rounded-xl border border-gray-100 space-y-4">
          <div className="h-5 bg-gray-200 rounded w-40"></div>
          <div className="grid grid-cols-2 gap-4">
            {Array.from({ length: 6 }).map((_, i) => (
              <div key={i} className="h-10 bg-gray-50 rounded-lg"></div>
            ))}
          </div>
        </div>
      </div>

      <div className="bg-white p-6 rounded-xl border border-gray-100 space-y-4">
        <div className="h-5 bg-gray-200 rounded w-48"></div>
        <div className="h-24 bg-gray-50 rounded-lg"></div>
      </div>
    </div>
  );
};
