import React from 'react';
import { PageHeader } from '../components/ui/PageHeader';
import { Card } from '../components/ui/Card';
import { ShieldAlert } from 'lucide-react';

export const CasesPlaceholder: React.FC = () => {
  return (
    <div>
      <PageHeader
        title="Fraud Case Queue"
        subtitle="Analyst manual review queue for high-risk transactions"
      />

      <Card title="Case Management Queue" subtitle="Paginated queue with status and priority filters">
        <div className="p-12 text-center bg-gray-50 rounded-lg border border-dashed border-gray-200 flex flex-col items-center">
          <div className="p-3 bg-red-50 text-[#E94F37] rounded-full mb-3">
            <ShieldAlert className="w-8 h-8" />
          </div>
          <h3 className="text-base font-semibold text-[#393E41]">Case Queue Initialized</h3>
          <p className="mt-1 text-sm text-gray-500 max-w-md">
            The Fraud Case Management queue UI will be populated in subsequent tasks using the backend REST API.
          </p>
        </div>
      </Card>
    </div>
  );
};
