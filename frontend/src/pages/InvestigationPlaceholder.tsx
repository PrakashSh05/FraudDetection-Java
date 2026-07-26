import React from 'react';
import { useParams } from 'react-router-dom';
import { PageHeader } from '../components/ui/PageHeader';
import { Card } from '../components/ui/Card';
import { SearchCode } from 'lucide-react';

export const InvestigationPlaceholder: React.FC = () => {
  const { transactionId } = useParams<{ transactionId: string }>();

  return (
    <div>
      <PageHeader
        title={`Transaction Investigation #${transactionId}`}
        subtitle="Deep risk evaluation audit, triggered rules breakdown, and score breakdown"
      />

      <Card title="Investigation Telemetry">
        <div className="p-12 text-center bg-gray-50 rounded-lg border border-dashed border-gray-200 flex flex-col items-center">
          <SearchCode className="w-8 h-8 text-[#E94F37] mb-3" />
          <p className="text-sm font-medium text-[#393E41]">
            Investigation workspace ready for Transaction <span className="font-mono font-bold text-[#E94F37]">{transactionId}</span>.
          </p>
        </div>
      </Card>
    </div>
  );
};
