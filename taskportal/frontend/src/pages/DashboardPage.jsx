import { useEffect, useState } from 'react';
import Navbar from '../components/ui/Navbar';
import StatsCards from '../components/tasks/StatsCards';
import TaskCard from '../components/tasks/TaskCard';
import TaskFormModal from '../components/tasks/TaskFormModal';
import useTaskStore from '../store/taskStore';
import { Plus, Search, Filter, Inbox } from 'lucide-react';

const STATUS_FILTERS = ['ALL', 'TODO', 'IN_PROGRESS', 'DONE'];
const PRIORITY_FILTERS = ['ALL', 'HIGH', 'MEDIUM', 'LOW'];
const STATUS_LABELS = { ALL: 'All', TODO: 'To Do', IN_PROGRESS: 'In Progress', DONE: 'Done' };

export default function DashboardPage() {
  const { tasks, stats, loading, statsLoading, fetchTasks, fetchStats } = useTaskStore();

  const [showModal, setShowModal] = useState(false);
  const [editingTask, setEditingTask] = useState(null);
  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [priorityFilter, setPriorityFilter] = useState('ALL');

  useEffect(() => {
    fetchTasks();
    fetchStats();
  }, []);

  const handleEdit = (task) => {
    setEditingTask(task);
    setShowModal(true);
  };

  const handleCloseModal = () => {
    setShowModal(false);
    setEditingTask(null);
  };

  // Apply filters and search
  const filtered = tasks.filter((t) => {
    const matchSearch =
      !search ||
      t.title.toLowerCase().includes(search.toLowerCase()) ||
      (t.description && t.description.toLowerCase().includes(search.toLowerCase()));
    const matchStatus = statusFilter === 'ALL' || t.status === statusFilter;
    const matchPriority = priorityFilter === 'ALL' || t.priority === priorityFilter;
    return matchSearch && matchStatus && matchPriority;
  });

  return (
    <div className="min-h-screen bg-slate-950">
      <Navbar />

      <main className="max-w-6xl mx-auto px-4 py-8">
        {/* Page Header */}
        <div className="flex items-center justify-between mb-6">
          <div>
            <h1 className="font-display text-2xl font-bold text-white">My Tasks</h1>
            <p className="text-slate-400 text-sm mt-0.5">
              {tasks.length} task{tasks.length !== 1 ? 's' : ''} total
            </p>
          </div>
          <button
            onClick={() => setShowModal(true)}
            className="btn-primary flex items-center gap-2"
          >
            <Plus size={16} />
            <span className="hidden sm:inline">New Task</span>
            <span className="sm:hidden">New</span>
          </button>
        </div>

        {/* Stats */}
        <StatsCards data={stats} loading={statsLoading} />

        {/* Filters */}
        <div className="flex flex-col sm:flex-row gap-3 mb-5">
          {/* Search */}
          <div className="relative flex-1">
            <Search size={15} className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-500" />
            <input
              type="text"
              placeholder="Search tasks..."
              className="input-field pl-9"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
            />
          </div>

          {/* Status filter */}
          <div className="flex items-center gap-1 bg-slate-900 border border-slate-800 rounded-lg p-1">
            {STATUS_FILTERS.map((s) => (
              <button
                key={s}
                onClick={() => setStatusFilter(s)}
                className={`px-3 py-1.5 rounded-md text-xs font-medium transition-all ${
                  statusFilter === s
                    ? 'bg-brand-500/20 text-brand-300 border border-brand-500/30'
                    : 'text-slate-500 hover:text-slate-300'
                }`}
              >
                {STATUS_LABELS[s]}
              </button>
            ))}
          </div>

          {/* Priority filter */}
          <select
            className="input-field w-auto min-w-[130px]"
            value={priorityFilter}
            onChange={(e) => setPriorityFilter(e.target.value)}
          >
            {PRIORITY_FILTERS.map((p) => (
              <option key={p} value={p}>
                {p === 'ALL' ? 'All Priorities' : p.charAt(0) + p.slice(1).toLowerCase() + ' Priority'}
              </option>
            ))}
          </select>
        </div>

        {/* Task Grid */}
        {loading ? (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
            {[...Array(6)].map((_, i) => (
              <div key={i} className="card h-36 animate-pulse bg-slate-800/50" />
            ))}
          </div>
        ) : filtered.length === 0 ? (
          <div className="flex flex-col items-center justify-center py-20 text-center">
            <div className="w-16 h-16 bg-slate-800 rounded-2xl flex items-center justify-center mb-4">
              <Inbox size={28} className="text-slate-600" />
            </div>
            <h3 className="font-semibold text-slate-400 mb-1">
              {tasks.length === 0 ? 'No tasks yet' : 'No matching tasks'}
            </h3>
            <p className="text-slate-600 text-sm">
              {tasks.length === 0
                ? 'Create your first task to get started'
                : 'Try adjusting your search or filters'}
            </p>
            {tasks.length === 0 && (
              <button
                onClick={() => setShowModal(true)}
                className="btn-primary mt-4 flex items-center gap-2"
              >
                <Plus size={15} />
                Create first task
              </button>
            )}
          </div>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
            {filtered.map((task) => (
              <TaskCard key={task.id} task={task} onEdit={handleEdit} />
            ))}
          </div>
        )}
      </main>

      {/* Modal */}
      {showModal && (
        <TaskFormModal task={editingTask} onClose={handleCloseModal} />
      )}
    </div>
  );
}
