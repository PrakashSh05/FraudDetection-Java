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
          className={`p-3.5 bg-neutral-950 rounded-xl border border-neutral-800 ${
            item.fullWidth ? 'col-span-full' : ''
          }`}
        >
          <span className="text-neutral-400 block text-[10px] uppercase font-bold tracking-wider">
            {item.label}
          </span>
          <div className="font-bold text-white mt-1 break-words">
            {item.value ?? <span className="text-neutral-500 italic">N/A</span>}
          </div>
        </div>
      ))}
    </div>
  );
};
