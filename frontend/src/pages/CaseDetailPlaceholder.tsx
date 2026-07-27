import { useParams } from 'react-router-dom';
import { PageHeader } from '../components/ui/PageHeader';
import { Card } from '../components/ui/Card';
import { Badge } from '../components/ui/Badge';

export const CaseDetailPlaceholder = () => {
  const { id } = useParams<{ id: string }>();

  return (
    <div>
      <PageHeader
        title={`Fraud Case #${id}`}
        subtitle="Analyst workspace, workflow state transitions, and audit timeline"
        action={<Badge variant="warning">UNDER_REVIEW</Badge>}
      />

      <Card title="Case Workflow Details">
        <div className="p-8 text-center bg-gray-50 rounded-lg border border-dashed border-gray-200">
          <p className="text-sm text-gray-600">
            Case Detail workspace for Case ID <span className="font-mono font-bold text-[#E94F37]">{id}</span>.
          </p>
        </div>
      </Card>
    </div>
  );
};
