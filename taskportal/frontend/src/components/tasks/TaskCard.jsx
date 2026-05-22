import { useState } from 'react';
import { Pencil, Trash2, Sparkles, Calendar, ChevronDown, ChevronUp } from 'lucide-react';
import useTaskStore from '../../store/taskStore';
import { format } from 'date-fns';

const statusClass = {
  TODO: 'tag-todo',
  IN_PROGRESS: 'tag-in-progress',
  DONE: 'tag-done',
};

const statusLabel = {
  TODO: 'To Do',
  IN_PROGRESS: 'In Progress',
  DONE: 'Done',
};

const priorityClass = {
  LOW: 'tag-low',
  MEDIUM: 'tag-medium',
  HIGH: 'tag-high',
};

export default function TaskCard({ task, onEdit }) {
  const { deleteTask, aiSummarize } = useTaskStore();
  const [aiLoading, setAiLoading] = useState(false);
  const [expanded, setExpanded] = useState(false);
  const [confirmDelete, setConfirmDelete] = useState(false);

  const handleAi = async () => {
    setAiLoading(true);
    try {
      await aiSummarize(task.id);
      setExpanded(true);
    } finally {
      setAiLoading(false);
    }
  };

  const handleDelete = () => {
    if (confirmDelete) {
      deleteTask(task.id);
    } else {
      setConfirmDelete(true);
      setTimeout(() => setConfirmDelete(false), 3000);
    }
  };

  return (
    <div className="card hover:border-slate-700 transition-all duration-200 group animate-fade-in">
      {/* Header row */}
      <div className="flex items-start justify-between gap-3 mb-3">
        <h3 className="font-semibold text-slate-100 text-sm leading-snug flex-1 min-w-0 truncate">
          {task.title}
        </h3>
        {/* Action buttons */}
        <div className="flex items-center gap-1 opacity-0 group-hover:opacity-100 transition-opacity shrink-0">
          <button
            onClick={handleAi}
            disabled={aiLoading}
            title="Generate AI Summary"
            className="p-1.5 rounded-lg text-slate-500 hover:text-brand-400 hover:bg-brand-500/10 transition-all"
          >
            <Sparkles size={14} className={aiLoading ? 'animate-spin-slow' : ''} />
          </button>
          <button
            onClick={() => onEdit(task)}
            title="Edit task"
            className="p-1.5 rounded-lg text-slate-500 hover:text-slate-200 hover:bg-slate-800 transition-all"
          >
            <Pencil size={14} />
          </button>
          <button
            onClick={handleDelete}
            title={confirmDelete ? 'Click again to confirm' : 'Delete task'}
            className={`p-1.5 rounded-lg transition-all ${
              confirmDelete
                ? 'text-red-400 bg-red-500/20'
                : 'text-slate-500 hover:text-red-400 hover:bg-red-500/10'
            }`}
          >
            <Trash2 size={14} />
          </button>
        </div>
      </div>

      {/* Tags */}
      <div className="flex flex-wrap gap-1.5 mb-3">
        <span className={statusClass[task.status]}>{statusLabel[task.status]}</span>
        <span className={priorityClass[task.priority]}>{task.priority.charAt(0) + task.priority.slice(1).toLowerCase()}</span>
      </div>

      {/* Description */}
      {task.description && (
        <p className="text-slate-400 text-xs leading-relaxed mb-3 line-clamp-2">
          {task.description}
        </p>
      )}

      {/* Due date */}
      {task.dueDate && (
        <div className="flex items-center gap-1 text-slate-500 text-xs mb-3">
          <Calendar size={12} />
          <span>Due {format(new Date(task.dueDate), 'MMM d, yyyy')}</span>
        </div>
      )}

      {/* AI Summary */}
      {task.aiSummary && (
        <div className="mt-3 border-t border-slate-800 pt-3">
          <button
            onClick={() => setExpanded(!expanded)}
            className="flex items-center gap-1.5 text-brand-400 text-xs font-medium hover:text-brand-300 transition-colors"
          >
            <Sparkles size={12} />
            AI Summary
            {expanded ? <ChevronUp size={12} /> : <ChevronDown size={12} />}
          </button>
          {expanded && (
            <p className="text-slate-400 text-xs leading-relaxed mt-2 pl-0.5 border-l-2 border-brand-600/40 pl-3 animate-fade-in">
              {task.aiSummary}
            </p>
          )}
        </div>
      )}

      {/* Footer */}
      <p className="text-slate-600 text-xs mt-3">
        {task.createdAt ? format(new Date(task.createdAt), 'MMM d, yyyy') : '—'}
      </p>
    </div>
  );
}
