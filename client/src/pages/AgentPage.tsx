import { useEffect, useState, useContext } from 'react';
import { AuthContext } from '../context/AuthContext';
import Pagination from '../components/Pagination';
import Navbar from '../components/ui/Navbar';
import Button from '../components/ui/Button';
import Modal from '../components/ui/Modal';
import { getStatusClasses, getPriorityClasses } from '../lib/badges';

interface Ticket {
  id: number;
  title: string;
  status: string;
  category: string;
  description: string;
  priority: string | { name: string };
  client: { fullName: string };
  comments?: Array<{ user: { fullName: string }; comment: string; createdAt: string }>;
}

interface Comment {
  user: { fullName: string };
  comment: string;
  createdAt: string;
}

const API_BASE = 'https://lucky-perception-production-d1b1.up.railway.app/api/ticket';
const COMMENT_API = 'https://lucky-perception-production-d1b1.up.railway.app/api/comment';

export default function AgentPage() {
  const { logout } = useContext(AuthContext);
  const [tickets, setTickets] = useState<Ticket[]>([]);
  const [currentTicket, setCurrentTicket] = useState<Ticket | null>(null);
  const [commentText, setCommentText] = useState('');

  const token = localStorage.getItem('jwtToken');
  // pagination/search state per status
  const [openPage, setOpenPage] = useState(1);
  const [inProgressPage, setInProgressPage] = useState(1);
  const [resolvedPage, setResolvedPage] = useState(1);
  const itemsPerPage = 5;

  const [searchInput, setSearchInput] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [priorityFilter, setPriorityFilter] = useState('ALL');

  const filteredTickets = tickets.filter(t =>
    t.title.toUpperCase().includes(searchTerm.toUpperCase()) &&
    (statusFilter === 'ALL' || t.status === statusFilter) &&
    (priorityFilter === 'ALL' || (typeof t.priority === 'string' ? t.priority === priorityFilter : (t.priority && t.priority.name === priorityFilter)))
  );

  const openTickets = filteredTickets.filter(t => t.status === 'OPEN');
  const inProgressTickets = filteredTickets.filter(t => t.status === 'IN_PROGRESS');
  const resolvedTickets = filteredTickets.filter(t => t.status === 'RESOLVED');

  const openTotalPages = Math.ceil(openTickets.length / itemsPerPage);
  const inProgressTotalPages = Math.ceil(inProgressTickets.length / itemsPerPage);
  const resolvedTotalPages = Math.ceil(resolvedTickets.length / itemsPerPage);

  const paginatedOpen = openTickets.slice(
    (openPage - 1) * itemsPerPage,
    openPage * itemsPerPage
  );
  const paginatedInProgress = inProgressTickets.slice(
    (inProgressPage - 1) * itemsPerPage,
    inProgressPage * itemsPerPage
  );
  const paginatedResolved = resolvedTickets.slice(
    (resolvedPage - 1) * itemsPerPage,
    resolvedPage * itemsPerPage
  );


  const fetchAgentTickets = async () => {
    try {
      const res = await fetch(API_BASE, {
        headers: { Authorization: `Bearer ${token}` },
      });
      const data = await res.json();
      setTickets(data.content || []);
      setOpenPage(1);
      setInProgressPage(1);
      setResolvedPage(1);
    } catch (err) {
      console.error(err);
    }
  };

  /* eslint-disable react-hooks/set-state-in-effect, react-hooks/exhaustive-deps */
  useEffect(() => {
    if (!token) return;
    fetchAgentTickets();
  }, []);

  // apply search term when button pressed
  const performSearch = () => {
    setSearchTerm(searchInput.trim());
    // reset pages so user sees first results
    setOpenPage(1);
    setInProgressPage(1);
    setResolvedPage(1);
  };

  const updateStatus = async (id: number, newStatus: string) => {
    try {
      const res = await fetch(`${API_BASE}/edit/status/${id}`, {
        method: 'PUT',
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ status: newStatus }),
      });
      if (res.ok) {
        fetchAgentTickets();
        if (currentTicket && currentTicket.id === id) viewTicket(id);
      }
    } catch (err) {
      console.error(err);
    }
  };

  const viewTicket = async (id: number) => {
    try {
      const res = await fetch(`${API_BASE}/${id}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      const ticket = await res.json();
      setCurrentTicket(ticket);
    } catch (err) {
      console.error(err);
    }
  };

  const submitComment = async () => {
    if (!currentTicket || !commentText) return;
    try {
      const res = await fetch(`${COMMENT_API}/create`, {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ ticketId: currentTicket.id, comment: commentText }),
      });
      if (res.ok) {
        setCommentText('');
        viewTicket(currentTicket.id);
      }
    } catch (err) {
      console.error(err);
    }
  };

  const closeDetails = () => {
    setCurrentTicket(null);
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-6xl mx-auto p-6">
        <Navbar title="Agent Kanban Board" onLogout={logout} />
        {/* search & filters */}
        <div className="flex mb-4 gap-2">
          <select value={statusFilter} onChange={e => { setStatusFilter(e.target.value); setOpenPage(1); setInProgressPage(1); setResolvedPage(1); }} className="border rounded px-2 py-1">
            <option value="ALL">All Status</option>
            <option value="OPEN">Open</option>
            <option value="IN_PROGRESS">In Progress</option>
            <option value="RESOLVED">Resolved</option>
            <option value="CLOSED">Closed</option>
          </select>
          <select value={priorityFilter} onChange={e => { setPriorityFilter(e.target.value); setOpenPage(1); setInProgressPage(1); setResolvedPage(1); }} className="border rounded px-2 py-1">
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

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {/* OPEN column */}
          <div className="bg-white shadow rounded-lg p-4">
            <h2 className="text-xl font-semibold mb-2">Open</h2>
            <div className="space-y-2">
              {paginatedOpen.map(t => (
                <div
                  key={t.id}
                  className="border border-gray-200 p-3 rounded hover:shadow-md transition"
                >
                  <strong className="block text-gray-800">{t.title}</strong>
                  <p className="text-sm text-gray-600">Client: {t.client.fullName}</p>
                  <p className="text-sm text-gray-600">Priority: <span className={`inline-block px-2 py-1 rounded text-xs font-semibold ${getPriorityClasses(typeof t.priority === 'string' ? t.priority : (t.priority?.name || ''))}`}>{typeof t.priority === 'string' ? t.priority : (t.priority?.name || 'N/A')}</span></p>
                  <div className="mt-2 flex flex-wrap gap-1">
                    <Button
                      variant="primary"
                      onClick={() => updateStatus(t.id, 'IN_PROGRESS')}
                      className="px-2 py-1 text-xs"
                    >
                      Start Work
                    </Button>
                    <Button
                      variant="ghost"
                      onClick={() => viewTicket(t.id)}
                      className="px-2 py-1 text-xs"
                    >
                      View Details
                    </Button>
                  </div>
                </div>
              ))}
            </div>
            {openTotalPages > 1 && (
              <div className="mt-4 flex justify-center">
                <Pagination currentPage={openPage} totalPages={openTotalPages} onPageChange={setOpenPage} />
              </div>
            )}
          </div>

          {/* IN_PROGRESS column */}
          <div className="bg-white shadow rounded-lg p-4">
            <h2 className="text-xl font-semibold mb-2">In Progress</h2>
            <div className="space-y-2">
              {paginatedInProgress.map(t => (
                <div
                  key={t.id}
                  className="border border-gray-200 p-3 rounded hover:shadow-md transition"
                >
                  <strong className="block text-gray-800">{t.title}</strong>
                  <p className="text-sm text-gray-600">Client: {t.client.fullName}</p>
                  {/* <p className="text-sm text-gray-600">Priority: {typeof t.priority}</p> */}
                  <p className="text-sm text-gray-600">Priority: <span className={`inline-block px-2 py-1 rounded text-xs font-semibold ${getPriorityClasses(typeof t.priority === 'string' ? t.priority : (t.priority?.name || ''))}`}>{typeof t.priority === 'string' ? t.priority : (t.priority?.name || 'N/A')}</span></p>
                  <div className="mt-2 flex flex-wrap gap-1">
                    <Button variant="primary" onClick={() => updateStatus(t.id, 'RESOLVED')} className="px-2 py-1 text-xs">Resolve</Button>
                    <Button variant="ghost" onClick={() => viewTicket(t.id)} className="px-2 py-1 text-xs">View Details</Button>
                  </div>
                </div>
              ))}
            </div>
            {inProgressTotalPages > 1 && (
              <div className="mt-4 flex justify-center">
                <Pagination
                  currentPage={inProgressPage}
                  totalPages={inProgressTotalPages}
                  onPageChange={setInProgressPage}
                />
              </div>
            )}
          </div>

          {/* RESOLVED column */}
          <div className="bg-white shadow rounded-lg p-4">
            <h2 className="text-xl font-semibold mb-2">Resolved</h2>
            <div className="space-y-2">
              {paginatedResolved.map(t => (
                <div
                  key={t.id}
                  className="border border-gray-200 p-3 rounded hover:shadow-md transition"
                >
                  <strong className="block text-gray-800">{t.title}</strong>
                  <p className="text-sm text-gray-600">Client: {t.client.fullName}</p>
                  <p className="text-sm text-gray-600">Priority: <span className={`inline-block px-2 py-1 rounded text-xs font-semibold ${getPriorityClasses(typeof t.priority === 'string' ? t.priority : (t.priority?.name || ''))}`}>{typeof t.priority === 'string' ? t.priority : (t.priority?.name || 'N/A')}</span></p>
                  <div className="mt-2 flex flex-wrap gap-1">
                    <Button variant="ghost" onClick={() => viewTicket(t.id)} className="px-2 py-1 text-xs">View Details</Button>
                  </div>
                </div>
              ))}
            </div>
            {resolvedTotalPages > 1 && (
              <div className="mt-4 flex justify-center">
                <Pagination
                  currentPage={resolvedPage}
                  totalPages={resolvedTotalPages}
                  onPageChange={setResolvedPage}
                />
              </div>
            )}
          </div>
        </div>

      {currentTicket && (
        <Modal onClose={closeDetails}>
          <h2 className="text-2xl font-semibold text-gray-800 mb-2">{currentTicket.title}</h2>
          <p className="text-sm text-gray-600">
            <strong>Status:</strong> <span className={`inline-block px-2 py-1 rounded text-xs font-semibold ${getStatusClasses(currentTicket.status)}`}>{currentTicket.status.replace('_', ' ')}</span> |{' '}
            <strong>Category:</strong> {currentTicket.category}
          </p>
          <p className="mt-2 text-gray-700">
            <strong>Description:</strong> {currentTicket.description}
          </p>
          <hr className="my-4" />
          <h3 className="font-semibold">Comments</h3>
          <div className="space-y-2 max-h-48 overflow-y-auto mb-2">
            {(currentTicket.comments || []).map((c: Comment, idx: number) => (
              <div key={idx} className="bg-gray-100 p-2 rounded">
                <strong>{c.user.fullName}:</strong> {c.comment}
                <br />
                <small className="text-xs text-gray-500">{new Date(c.createdAt).toLocaleString()}</small>
              </div>
            ))}
          </div>
          <textarea
            value={commentText}
            onChange={(e) => setCommentText(e.target.value)}
            className="w-full border rounded p-2"
            rows={3}
            placeholder="Type your reply..."
          />
          <div className="mt-2 flex justify-end">
            <Button variant="primary" onClick={submitComment}>Post Comment</Button>
          </div>
        </Modal>
      )}
    </div>
    </div>
  );
}

