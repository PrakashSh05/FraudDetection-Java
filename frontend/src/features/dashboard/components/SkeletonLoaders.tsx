export const SkeletonCard = () => {
  return (
    <div className="bg-slate-900 border border-slate-800 rounded-2xl p-5 animate-pulse">
      <div className="flex items-center justify-between">
        <div className="h-3 bg-slate-800 rounded w-24"></div>
        <div className="w-8 h-8 bg-slate-800 rounded-xl"></div>
      </div>
      <div className="mt-3 flex items-baseline justify-between">
        <div className="h-7 bg-slate-800 rounded w-16"></div>
        <div className="h-3 bg-slate-800 rounded w-12"></div>
      </div>
    </div>
  );
};

export const SkeletonChart = () => {
  return (
    <div className="bg-slate-900 border border-slate-800 rounded-2xl p-6 animate-pulse">
      <div className="h-4 bg-slate-800 rounded w-40 mb-6"></div>
      <div className="h-64 bg-slate-950/60 rounded-xl flex items-center justify-center border border-slate-800">
        <div className="h-12 w-12 rounded-full border-4 border-slate-800 border-t-[#E94F37] animate-spin"></div>
      </div>
    </div>
  );
};

export const SkeletonSummary = () => {
  return (
    <div className="bg-slate-900 border border-slate-800 rounded-2xl p-6 animate-pulse space-y-4">
      <div className="h-4 bg-slate-800 rounded w-32"></div>
      <div className="grid grid-cols-2 gap-3">
        {Array.from({ length: 6 }).map((_, i) => (
          <div key={i} className="h-10 bg-slate-950/80 rounded-xl border border-slate-800"></div>
        ))}
      </div>
    </div>
  );
};
