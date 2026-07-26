import React from 'react';

export const SkeletonCard: React.FC = () => {
  return (
    <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-5 animate-pulse">
      <div className="flex items-center justify-between">
        <div className="h-3 bg-gray-200 rounded w-24"></div>
        <div className="w-8 h-8 bg-gray-100 rounded-lg"></div>
      </div>
      <div className="mt-3 flex items-baseline justify-between">
        <div className="h-7 bg-gray-200 rounded w-16"></div>
        <div className="h-3 bg-gray-100 rounded w-12"></div>
      </div>
    </div>
  );
};

export const SkeletonChart: React.FC = () => {
  return (
    <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6 animate-pulse">
      <div className="h-4 bg-gray-200 rounded w-40 mb-6"></div>
      <div className="h-64 bg-gray-50 rounded-lg flex items-center justify-center">
        <div className="h-32 w-32 rounded-full border-4 border-gray-200 border-t-gray-300 animate-spin"></div>
      </div>
    </div>
  );
};

export const SkeletonSummary: React.FC = () => {
  return (
    <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6 animate-pulse space-y-4">
      <div className="h-4 bg-gray-200 rounded w-32"></div>
      <div className="grid grid-cols-2 gap-3">
        {Array.from({ length: 6 }).map((_, i) => (
          <div key={i} className="h-10 bg-gray-100 rounded-lg"></div>
        ))}
      </div>
    </div>
  );
};
