import React from 'react';

export const SkeletonCaseDetail: React.FC = () => {
  return (
    <div className="space-y-6 animate-pulse">
      <div className="h-8 bg-gray-200 rounded w-64"></div>
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2 space-y-6">
          <div className="h-48 bg-white rounded-xl border border-gray-100 p-6"></div>
          <div className="h-64 bg-white rounded-xl border border-gray-100 p-6"></div>
        </div>
        <div className="space-y-6">
          <div className="h-64 bg-white rounded-xl border border-gray-100 p-6"></div>
          <div className="h-48 bg-white rounded-xl border border-gray-100 p-6"></div>
        </div>
      </div>
    </div>
  );
};
