import React from 'react';
import { TransactionDetails } from '../types';
import { Card } from '../../../components/ui/Card';
import { Badge } from '../../../components/ui/Badge';
import { CreditCard, Calendar, User, ArrowLeftRight } from 'lucide-react';

interface TransactionSummaryCardProps {
  transaction: TransactionDetails;
}

export const TransactionSummaryCard: React.FC<TransactionSummaryCardProps> = ({ transaction }) => {
  return (
    <Card title="Transaction Information" subtitle="Historical immutable transaction data">
      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 text-xs">
        <div className="p-3 bg-gray-50 rounded-lg flex items-center gap-3">
          <CreditCard className="w-5 h-5 text-[#E94F37]" />
          <div>
            <span className="text-gray-400 block text-[10px] uppercase font-semibold">Transaction ID</span>
            <span className="font-mono font-bold text-[#393E41] text-sm">#{transaction.transactionId}</span>
          </div>
        </div>

        <div className="p-3 bg-gray-50 rounded-lg flex items-center gap-3">
          <User className="w-5 h-5 text-[#393E41]" />
          <div>
            <span className="text-gray-400 block text-[10px] uppercase font-semibold">User ID</span>
            <span className="font-mono font-bold text-[#393E41] text-sm">User #{transaction.userId}</span>
          </div>
        </div>

        <div className="p-3 bg-gray-50 rounded-lg">
          <span className="text-gray-400 block text-[10px] uppercase font-semibold">Amount</span>
          <span className="text-lg font-bold text-[#393E41]">₹{transaction.amount?.toLocaleString() ?? 0}</span>
        </div>

        <div className="p-3 bg-gray-50 rounded-lg flex items-center justify-between">
          <div>
            <span className="text-gray-400 block text-[10px] uppercase font-semibold">Transaction Type</span>
            <span className="font-semibold text-[#393E41] flex items-center gap-1 mt-0.5">
              <ArrowLeftRight className="w-3.5 h-3.5" /> {transaction.transactionType}
            </span>
          </div>
          <Badge variant={transaction.status === 'APPROVED' ? 'success' : 'danger'}>
            {transaction.status}
          </Badge>
        </div>

        <div className="p-3 bg-gray-50 rounded-lg col-span-1 sm:col-span-2 flex items-center gap-2 text-gray-500">
          <Calendar className="w-4 h-4 text-gray-400" />
          <span>Created: {new Date(transaction.createdAt).toLocaleString()}</span>
        </div>
      </div>
    </Card>
  );
};
