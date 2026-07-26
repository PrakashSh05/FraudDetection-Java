import React, { ReactNode } from 'react';
import { ArrowUpDown } from 'lucide-react';

export interface Column<T> {
  header: string;
  accessor?: keyof T;
  render?: (row: T) => ReactNode;
  sortableKey?: string;
  align?: 'left' | 'center' | 'right';
}

interface DataTableProps<T> {
  columns: Column<T>[];
  data: T[];
  keyExtractor: (row: T) => string | number;
  sortParam?: string;
  onSortChange?: (sortKey: string) => void;
  onRowClick?: (row: T) => void;
  mobileRender?: (row: T) => ReactNode;
}

export function DataTable<T>({
  columns,
  data,
  keyExtractor,
  onSortChange,
  onRowClick,
  mobileRender,
}: DataTableProps<T>) {
  return (
    <>
      {/* Desktop & Tablet Table */}
      <div className="hidden md:block overflow-x-auto border border-gray-100 rounded-xl bg-white shadow-sm">
        <table className="w-full text-left text-xs">
          <thead className="bg-gray-50 border-b border-gray-100 text-gray-500 font-semibold uppercase tracking-wider">
            <tr>
              {columns.map((col, idx) => (
                <th
                  key={idx}
                  className={`py-3.5 px-4 ${
                    col.align === 'right' ? 'text-right' : col.align === 'center' ? 'text-center' : 'text-left'
                  }`}
                >
                  {col.sortableKey && onSortChange ? (
                    <button
                      onClick={() => onSortChange(col.sortableKey!)}
                      className="inline-flex items-center gap-1 hover:text-[#E94F37] transition-colors"
                    >
                      <span>{col.header}</span>
                      <ArrowUpDown className="w-3 h-3" />
                    </button>
                  ) : (
                    <span>{col.header}</span>
                  )}
                </th>
              ))}
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-100 text-[#393E41]">
            {data.map((row) => (
              <tr
                key={keyExtractor(row)}
                onClick={() => onRowClick && onRowClick(row)}
                className={`${onRowClick ? 'cursor-pointer hover:bg-gray-50/80' : ''} transition-colors`}
              >
                {columns.map((col, idx) => (
                  <td
                    key={idx}
                    className={`py-3.5 px-4 ${
                      col.align === 'right' ? 'text-right' : col.align === 'center' ? 'text-center' : 'text-left'
                    }`}
                  >
                    {col.render
                      ? col.render(row)
                      : col.accessor
                      ? String(row[col.accessor] ?? '')
                      : null}
                  </td>
                ))}
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Mobile Custom Card Render */}
      {mobileRender && (
        <div className="md:hidden space-y-3">
          {data.map((row) => (
            <div key={keyExtractor(row)}>{mobileRender(row)}</div>
          ))}
        </div>
      )}
    </>
  );
}
