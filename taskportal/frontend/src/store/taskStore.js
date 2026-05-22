import { create } from 'zustand';
import { tasksApi } from '../services/api';
import toast from 'react-hot-toast';

/**
 * Global task state store using Zustand.
 * Handles all CRUD operations and loading states.
 */
const useTaskStore = create((set, get) => ({
  tasks: [],
  stats: null,
  loading: false,
  statsLoading: false,

  // ─── Fetch all tasks ────────────────────────────────────────────────────────
  fetchTasks: async () => {
    set({ loading: true });
    try {
      const res = await tasksApi.getAll();
      set({ tasks: res.data, loading: false });
    } catch (err) {
      toast.error('Failed to load tasks');
      set({ loading: false });
    }
  },

  // ─── Fetch stats ────────────────────────────────────────────────────────────
  fetchStats: async () => {
    set({ statsLoading: true });
    try {
      const res = await tasksApi.getStats();
      set({ stats: res.data, statsLoading: false });
    } catch {
      set({ statsLoading: false });
    }
  },

  // ─── Create task ────────────────────────────────────────────────────────────
  createTask: async (data) => {
    try {
      const res = await tasksApi.create(data);
      set((state) => ({ tasks: [res.data, ...state.tasks] }));
      toast.success('Task created!');
      get().fetchStats();
      return res.data;
    } catch (err) {
      const msg = err.response?.data?.message || 'Failed to create task';
      toast.error(msg);
      throw err;
    }
  },

  // ─── Update task ────────────────────────────────────────────────────────────
  updateTask: async (id, data) => {
    try {
      const res = await tasksApi.update(id, data);
      set((state) => ({
        tasks: state.tasks.map((t) => (t.id === id ? res.data : t)),
      }));
      toast.success('Task updated!');
      get().fetchStats();
      return res.data;
    } catch (err) {
      const msg = err.response?.data?.message || 'Failed to update task';
      toast.error(msg);
      throw err;
    }
  },

  // ─── Delete task ────────────────────────────────────────────────────────────
  deleteTask: async (id) => {
    try {
      await tasksApi.delete(id);
      set((state) => ({ tasks: state.tasks.filter((t) => t.id !== id) }));
      toast.success('Task deleted');
      get().fetchStats();
    } catch (err) {
      toast.error('Failed to delete task');
    }
  },

  // ─── AI Summarize ───────────────────────────────────────────────────────────
  aiSummarize: async (id) => {
    try {
      const res = await tasksApi.aiSummarize(id);
      set((state) => ({
        tasks: state.tasks.map((t) => (t.id === id ? res.data : t)),
      }));
      toast.success('AI summary generated!');
      return res.data;
    } catch (err) {
      const msg = err.response?.data?.message || 'AI summarization failed';
      toast.error(msg);
      throw err;
    }
  },
}));

export default useTaskStore;
