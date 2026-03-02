import React from 'react';

interface DataPoint {
  label: string;
  value: number;
}

interface BarChartProps {
  data: DataPoint[];
  maxValue?: number;
  // can pass additional class names for container
  className?: string;
  // optionally display values as formatted text
  formatValue?: (v: number) => React.ReactNode;
}

export default function BarChart({ data, maxValue, className = '', formatValue }: BarChartProps) {
  const max = maxValue ?? (data.length ? Math.max(...data.map((d) => d.value)) : 0);

  return (
    <div className={`space-y-2 ${className}`}>      
      {data.map((d) => {
        const pct = max > 0 ? (d.value / max) * 100 : 0;
        return (
          <div key={d.label} className="flex items-center">
            <span className="w-24 text-xs text-gray-600 truncate">{d.label}</span>
            <div className="flex-1 bg-gray-200 rounded-full h-3 relative mx-2">
              <div
                className="bg-blue-600 h-3 rounded-full transition-all"
                style={{ width: `${pct}%` }}
              />
            </div>
            <span className="w-12 text-xs font-medium text-right">
              {formatValue ? formatValue(d.value) : d.value}
            </span>
          </div>
        );
      })}
    </div>
  );
}
