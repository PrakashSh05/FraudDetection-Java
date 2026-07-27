import { TransactionDetails } from '../types';
import { Card } from '../../../components/ui/Card';
import { Badge } from '../../../components/ui/Badge';
import { CreditCard, Calendar, User, ArrowLeftRight } from 'lucide-react';

interface TransactionSummaryCardProps {
  transaction: TransactionDetails;
}

export const TransactionSummaryCard = ({ transaction }: TransactionSummaryCardProps) => {
  return (
    <Card title="Transaction Telemetry Summary" subtitle="Immutable transaction record and user context">
      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 text-xs">
        <div className="p-3.5 bg-neutral-950 rounded-xl border border-neutral-800 flex items-center gap-3">
          <CreditCard className="w-5 h-5 text-[#E94F37]" />
          <div>
            <span className="text-neutral-400 block text-[10px] uppercase font-bold">Transaction ID</span>
            <span className="font-mono font-bold text-white text-sm">#{transaction.transactionId}</span>
          </div>
        </div>

        <div className="p-3.5 bg-neutral-950 rounded-xl border border-neutral-800 flex items-center gap-3">
          <User className="w-5 h-5 text-orange-400" />
          <div>
            <span className="text-neutral-400 block text-[10px] uppercase font-bold">User Account</span>
            <span className="font-mono font-bold text-white text-sm">User #{transaction.userId}</span>
          </div>
        </div>

        <div className="p-3.5 bg-neutral-950 rounded-xl border border-neutral-800">
          <span className="text-neutral-400 block text-[10px] uppercase font-bold">Amount</span>
          <span className="text-xl font-extrabold text-white">${transaction.amount?.toLocaleString() ?? 0}</span>
        </div>

        <div className="p-3.5 bg-neutral-950 rounded-xl border border-neutral-800 flex items-center justify-between">
          <div>
            <span className="text-neutral-400 block text-[10px] uppercase font-bold">Type</span>
            <span className="font-semibold text-neutral-200 flex items-center gap-1 mt-0.5">
              <ArrowLeftRight className="w-3.5 h-3.5 text-neutral-400" /> {transaction.transactionType}
            </span>
          </div>
          <Badge variant={transaction.status === 'APPROVED' ? 'success' : 'danger'}>
            {transaction.status}
          </Badge>
        </div>

        <div className="p-3.5 bg-neutral-950 rounded-xl border border-neutral-800 col-span-1 sm:col-span-2 flex items-center gap-2 text-neutral-400">
          <Calendar className="w-4 h-4 text-neutral-500" />
          <span>Evaluation Timestamp: {new Date(transaction.createdAt).toLocaleString()}</span>
        </div>
      </div>
    </Card>
  );
};
