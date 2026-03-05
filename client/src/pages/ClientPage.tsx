import React, { useEffect, useState, useContext } from 'react';
import { AuthContext } from '../context/AuthContext';
import Navbar from '../components/ui/Navbar';
import Button from '../components/ui/Button';
import Input from '../components/ui/Input';
import Modal from '../components/ui/Modal';
import { getStatusClasses, getPriorityClasses } from '../lib/badges';
import type { TicketWithComments } from '../../types';
import Pagination from '../components/Pagination';

const API_BASE = 'https://refreshing-respect-production-9b46.up.railway.app/api/ticket';
const COMMENT_API = 'https://refreshing-respect-production-9b46.up.railway.app/api/comment';

export default function ClientPage(){
  const { logout } = useContext(AuthContext);
  const [tickets, setTickets] = useState<TicketWithComments[]>([]);
  const [currentTicket, setCurrentTicket] = useState<TicketWithComments | null>(null);
  const [newComment, setNewComment] = useState('');
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [priority, setPriority] = useState('LOW');
  // use categories accepted by backend
  const [category, setCategory] = useState('SOFTWARE');
  

  const token = localStorage.getItem('jwtToken');
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 5;

  const [statusFilter, setStatusFilter] = useState('ALL');
  const [priorityFilter, setPriorityFilter] = useState('ALL');
  const [searchInput, setSearchInput] = useState('');
  const [searchTerm, setSearchTerm] = useState('');

  const filteredTickets = tickets.filter(t =>
    t.title.toUpperCase().includes(searchTerm.toUpperCase()) &&
    (statusFilter === 'ALL' || t.status === statusFilter) &&
     (priorityFilter === 'ALL' || (typeof t.priority === 'string' ? t.priority === priorityFilter : (t.priority && t.priority.name === priorityFilter)))
  );

  const totalPages = Math.ceil(filteredTickets.length / itemsPerPage);
  const paginatedTickets = filteredTickets.slice(
    (currentPage - 1) * itemsPerPage,
    currentPage * itemsPerPage
  );

  const fetchMyTickets = async () => {
    try {
      const res = await fetch(API_BASE, {
        headers: { Authorization: `Bearer ${token}` },
      });
      const data = await res.json();
      setTickets(data.content || []);
      setCurrentPage(1);
    } catch (err) {
      console.error(err);
    }
  };

  const performSearch = () => {
    setSearchTerm(searchInput.trim());
    setCurrentPage(1);
  };

  /* eslint-disable react-hooks/set-state-in-effect, react-hooks/exhaustive-deps */
  useEffect(() => {
    fetchMyTickets();
  }, []);


  const submitNewTicket = async (e: React.SubmitEvent) => {
    e.preventDefault();
    try {
      // backend expects category from defined enum
      await fetch(API_BASE + '/create', {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ title, description, priority, category, status: "OPEN" }),
      });
      setTitle('');
      setDescription('');
      setPriority('LOW');
      setCategory('SOFTWARE');
      fetchMyTickets();
    } catch (err) {
      console.error(err);
    }
  };

  const updateStatus = async (id: number, newStatus: string) => {
    try {
      await fetch(`${API_BASE}/edit/status/${id}`, {
        method: 'PUT',
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ status: newStatus }),
      });
      fetchMyTickets();
    } catch (err) {
      console.error(err);
    }
  };

  const viewTicket = async (id: number) => {
    try {
      const res = await fetch(`${API_BASE}/${id}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      const t = await res.json();
      setCurrentTicket(t);
    } catch (err) {
      console.error(err);
    }
  };


  const submitComment = async () => {
    if (!currentTicket) return;
    try {
      await fetch(`${COMMENT_API}/create`, {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ ticketId: currentTicket.id, comment: newComment }),
      });
      setNewComment('');
      viewTicket(currentTicket.id);
    } catch (err) {
      console.error(err);
    }
  };

  const closeModal = () => {
    setCurrentTicket(null);
  };

  return (
    <>
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-4xl mx-auto p-6">
        <Navbar title="My Support Tickets" onLogout={logout} />

            <div className="bg-white shadow-md rounded-lg p-6">
            <h3 className="font-semibold text-xl">Create New Ticket</h3>
            <form onSubmit={submitNewTicket} className="space-y-4 mt-4">
            <Input type="text" placeholder="Title" value={title} onChange={(e) => setTitle(e.target.value)} />
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Description</label>
              <textarea
                placeholder="Describe your issue..."
                className="w-full border rounded px-2 py-1"
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                required
              />
            </div>
            <div className="flex gap-4">
                <div className="flex-1">
                <label className="block text-sm font-medium">Priority:</label>
                <select
                    className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
                    value={priority}
                    onChange={(e) => setPriority(e.target.value)}
                >
                    <option value="LOW">Low</option>
                    <option value="MEDIUM">Medium</option>
                    <option value="HIGH">High</option>
                    <option value="CRITICAL">Critical</option>
                </select>
                </div>
                <div className="flex-1">
                <label className="block text-sm font-medium">Category:</label>
                <select
                    className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
                    value={category}
                    onChange={(e) => setCategory(e.target.value)}
                >
                    <option value="HARDWARE">Hardware</option>
                    <option value="SOFTWARE">Software</option>
                    <option value="NETWORK">Network</option>
                    <option value="ACCOUNTS_AND_ACCESS">Accounts & Access</option>
                    <option value="SERVICES">Services</option>
                    <option value="GENERAL">General</option>
                </select>
                </div>
            </div>
            <Button type="submit" variant="primary">Submit Ticket</Button>
            </form>
        </div>

        <section className="mt-8 space-y-4">
          {/* search bar */}
          <div className="flex mb-4 gap-2">
            <select value={statusFilter} onChange={e => { setStatusFilter(e.target.value); setCurrentPage(1); }} className="border rounded px-2 py-1">
              <option value="ALL">All Status</option>
              <option value="OPEN">Open</option>
              <option value="IN_PROGRESS">In Progress</option>
              <option value="RESOLVED">Resolved</option>
              <option value="CLOSED">Closed</option>
            </select>
            <select value={priorityFilter} onChange={e => { setPriorityFilter(e.target.value); setCurrentPage(1); }} className="border rounded px-2 py-1">
              <option value="ALL">All Priority</option>
              <option value="LOW">Low</option>
              <option value="MEDIUM">Medium</option>
              <option value="HIGH">High</option>
              <option value="CRITICAL">Critical</option>
            </select>
            <input
              type="text"
              placeholder="Search tickets..."
              value={searchInput}
              onChange={e => setSearchInput(e.target.value)}
              className="flex-1 border rounded px-3 py-2"
            />
            <Button variant="primary" onClick={performSearch}>Search</Button>
          </div>
            {paginatedTickets.map(t => (
            <div
                key={t.id}
                className="bg-white shadow rounded-lg p-4 hover:shadow-lg transition"
            >
                <div className="flex justify-between items-center">
                <h3 className="text-lg font-medium text-gray-800">{t.title}</h3>
                <span className={`inline-block px-2 py-1 rounded text-xs font-semibold ${getStatusClasses(t.status)}`}>
                  {t.status.replace('_', ' ')}
                </span>
                </div>
                 <p className="text-sm text-gray-600">Priority: <span className={`inline-block px-2 py-1 rounded text-xs font-semibold ${getPriorityClasses(typeof t.priority === 'string' ? t.priority : (t.priority?.name || ''))}`}>{typeof t.priority === 'string' ? t.priority : (t.priority?.name || 'N/A')}</span></p>
                <div className="mt-3 flex flex-wrap gap-2">
                <Button variant="ghost" onClick={() => viewTicket(t.id)} className="px-3 py-1 text-xs">View & Comment</Button>
                {t.status !== 'CLOSED' ? (
                  <Button variant="primary" onClick={() => updateStatus(t.id, 'CLOSED')} className="px-3 py-1 text-xs">Close</Button>
                ) : (
                  <Button variant="ghost" onClick={() => updateStatus(t.id, 'OPEN')} className="px-3 py-1 text-xs">Re-open</Button>
                )}
                </div>
            </div>
            ))}
        </section>

        {totalPages > 1 && (
          <Pagination
            currentPage={currentPage}
            totalPages={totalPages}
            onPageChange={setCurrentPage}
          />
        )}

        {currentTicket && (
          <Modal onClose={closeModal}>
            <h3 className="text-xl font-semibold">{currentTicket.title}</h3>
            <p className="mt-2">{currentTicket.description}</p>
            <hr className="my-4" />
            <div className="max-h-40 overflow-y-auto bg-gray-100 p-2">
              {(currentTicket.comments || []).map((c, idx) => (
                <div key={idx} className="mb-2">
                  <strong>{c.user.fullName}:</strong> {c.comment}
                  <br />
                  <small>{new Date(c.createdAt).toLocaleString()}</small>
                </div>
              ))}
            </div>
            <textarea
              value={newComment}
              onChange={(e) => setNewComment(e.target.value)}
              className="w-full border rounded p-2 mt-2"
              rows={3}
              placeholder="Write a reply..."
            />
            <div className="mt-2 flex justify-end">
              <Button
                onClick={submitComment}
                variant="primary"
              >
                Post Comment
              </Button>
            </div>
          </Modal>
        )}
        </div>
        </div>
    </>
  );
}

