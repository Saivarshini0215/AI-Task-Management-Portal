import { CheckCircle2, Circle, Clock, LayoutGrid } from 'lucide-react';

const stats = [
  { key: 'total', label: 'Total Tasks', icon: LayoutGrid, color: 'text-slate-300', bg: 'bg-slate-700/40' },
  { key: 'todo', label: 'To Do', icon: Circle, color: 'text-slate-400', bg: 'bg-slate-700/30' },
  { key: 'inProgress', label: 'In Progress', icon: Clock, color: 'text-amber-400', bg: 'bg-amber-500/10' },
  { key: 'done', label: 'Completed', icon: CheckCircle2, color: 'text-emerald-400', bg: 'bg-emerald-500/10' },
];

export default function StatsCards({ data, loading }) {
  return (
    <div className="grid grid-cols-2 lg:grid-cols-4 gap-3 mb-6">
      {stats.map(({ key, label, icon: Icon, color, bg }) => (
        <div key={key} className="card flex items-center gap-3">
          <div className={`w-9 h-9 rounded-lg ${bg} flex items-center justify-center shrink-0`}>
            <Icon size={18} className={color} />
          </div>
          <div>
            <p className="text-slate-500 text-xs">{label}</p>
            <p className="font-display font-bold text-xl text-white">
              {loading ? '—' : data?.[key] ?? 0}
            </p>
          </div>
        </div>
      ))}
    </div>
  );
}
