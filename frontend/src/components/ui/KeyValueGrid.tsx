import { ReactNode } from 'react';

export interface KeyValueItem {
  label: string;
  value: ReactNode;
  fullWidth?: boolean;
}

interface KeyValueGridProps {
  items: KeyValueItem[];
  cols?: 2 | 3 | 4;
}

export const KeyValueGrid = ({ items, cols = 2 }: KeyValueGridProps) => {
  const colStyles = {
    2: 'grid-cols-1 sm:grid-cols-2',
    3: 'grid-cols-1 sm:grid-cols-2 lg:grid-cols-3',
    4: 'grid-cols-1 sm:grid-cols-2 lg:grid-cols-4',
  };

  return (
    <div className={`grid ${colStyles[cols]} gap-3 text-xs`}>
      {items.map((item, idx) => (
        <div
          key={idx}
          className={`p-3 bg-gray-50 rounded-lg border border-gray-100 ${
            item.fullWidth ? 'col-span-full' : ''
          }`}
        >
          <span className="text-gray-400 block text-[10px] uppercase font-semibold tracking-wider">
            {item.label}
          </span>
          <div className="font-semibold text-[#393E41] mt-1 break-words">
            {item.value ?? <span className="text-gray-300 italic">N/A</span>}
          </div>
        </div>
      ))}
    </div>
  );
};
