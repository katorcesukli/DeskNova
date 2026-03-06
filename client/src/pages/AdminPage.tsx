import { useEffect, useState, useContext } from 'react';
import { AuthContext } from '../context/AuthContext';
import Navbar from '../components/ui/Navbar';
import Button from '../components/ui/Button';
import Card from '../components/ui/Card';
import BarChart from '../components/BarChart';
import type { MetricResponse, TicketDetail, UpdateTicketData, User } from '../../types';
import TicketModal from '../../component/modal/ticket-modal';
import UserModal from '../../component/modal/user-modal';
import Pagination from '../components/Pagination';
import { getStatusClasses, getPriorityClasses } from '../lib/badges';



const API_URL = 'https://lucky-perception-production-d1b1.up.railway.app/api/user';
const TICKET_URL = 'https://lucky-perception-production-d1b1.up.railway.app/api/ticket';
const METRIC_URL = 'https://lucky-perception-production-d1b1.up.railway.app/api/metric/admin';

export default function AdminPage() {
  const { logout } = useContext(AuthContext);
  const [users, setUsers] = useState<User[]>([]);
  const [tickets, setTickets] = useState<TicketDetail[]>([]);
  const [metrics, setMetrics] = useState<MetricResponse | null>(null);

  const [showUserModal, setShowUserModal] = useState(false);
  const [isEditUser, setIsEditUser] = useState(false);
  const [editingUser, setEditingUser] = useState<User | null>(null);

  const [showTicketModal, setShowTicketModal] = useState(false);
  const [editingTicket, setEditingTicket] = useState<TicketDetail | null>(null);

  // Pagination & search states
  const [userPage, setUserPage] = useState(1);
  const [ticketPage, setTicketPage] = useState(1);
  const itemsPerPage = 5;

  const [userSearchInput, setUserSearchInput] = useState('');
  const [userSearchTerm, setUserSearchTerm] = useState('');
  const [ticketSearchInput, setTicketSearchInput] = useState('');
  const [ticketSearchTerm, setTicketSearchTerm] = useState('');
  const [ticketStatusFilter, setTicketStatusFilter] = useState('ALL');
  const [ticketPriorityFilter, setTicketPriorityFilter] = useState('ALL');
  const [agentSearchInput, setAgentSearchInput] = useState('');
  const [agentSearchTerm, setAgentSearchTerm] = useState('');
  const [agentPage, setAgentPage] = useState(1);
  const agentItemsPerPage = 5;

  const token = localStorage.getItem('jwtToken');
  

  // fetch helpers are declared below before useEffect

  const fetchUsers = async () => {
    try {
      const res = await fetch(API_URL, {
        headers: { Authorization: `Bearer ${token}` },
      });
      const data = await res.json();
      setUsers(data || []);
      setUserPage(1);
    } catch (err) {
      console.error('fetchUsers error', err);
    }
  };

  const fetchTickets = async () => {
    try {
      const res = await fetch(`${TICKET_URL}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      const data = await res.json();
      setTickets(data.content || []);
      setTicketPage(1);
    } catch (err) {
      console.error('fetchTickets error', err);
    }
  };

  const fetchMetrics = async () => {
    try {
      const res = await fetch(METRIC_URL, {
        headers: { Authorization: `Bearer ${token}` },
      });
      const data = await res.json();
      setMetrics(data);
    } catch (err) {
      console.error('fetchMetrics error', err);
    }
  };

  const openCreateUser = () => {
    setIsEditUser(false);
    setEditingUser(null);
    setShowUserModal(true);
  };

  const openEditUser = (u: User) => {
    setIsEditUser(true);
    setEditingUser(u);
    setShowUserModal(true);
  };

  const saveUser = async (user: User) => {
    try {
      const url = isEditUser ? `${API_URL}/admin/${editingUser?.email}` : `${API_URL}/admin`;
      const method = isEditUser ? 'PUT' : 'POST';
      const res = await fetch(url, {
        method,
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(user),
      });
      if (res.ok) {
        setShowUserModal(false);
        fetchUsers();
      } else {
        const err = await res.json();
        alert('Error saving user: ' + (err?.message || ''));
      }
    } catch (err) {
      console.error(err);
    }
  };

  const deleteUser = async (email: string) => {
    if (!window.confirm(`Delete ${email}?`)) return;
    try {
      const res = await fetch(`${API_URL}/admin/${email}`, {
        method: 'DELETE',
        headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' },
        body: JSON.stringify({}),
      });
      if (res.ok) fetchUsers();
    } catch (err) {
      console.error(err);
    }
  };

  const openTicketEdit = async (id: number) => {
    try {
      const res = await fetch(`${TICKET_URL}/${id}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      const t = await res.json();
      setEditingTicket(t);
      setShowTicketModal(true);
    } catch (err) {
      console.error(err);
    }
  };

  const saveTicket = async (updateData: UpdateTicketData) => {
    try {
      const res = await fetch(`${TICKET_URL}/admin/update/${editingTicket?.id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(updateData),
      });
      if (res.ok) {
        setShowTicketModal(false);
        fetchTickets();
        fetchMetrics();
      } else {
        alert('Failed to update ticket.');
      }
    } catch (err) {
      console.error(err);
    }
  };

  const deleteTicket = async (id: number) => {
    if (!window.confirm(`Delete ticket #${id}?`)) return;
    try {
      const res = await fetch(`${TICKET_URL}/delete/${id}`, {
        method: 'DELETE',
        headers: { Authorization: `Bearer ${token}` },
      });
      if (res.ok) fetchTickets();
    } catch (err) {
      console.error(err);   
    }
  };

  // run once on mount
  /* eslint-disable react-hooks/set-state-in-effect, react-hooks/exhaustive-deps */
  useEffect(() => {
    fetchUsers();
    fetchMetrics();
    fetchTickets();
  }, []);

  const performUserSearch = () => {
    setUserSearchTerm(userSearchInput.trim());
    setUserPage(1);
  };
  const performTicketSearch = () => {
    setTicketSearchTerm(ticketSearchInput.trim());
    setTicketPage(1);
  };
  const performAgentSearch = () => {
    setAgentSearchTerm(agentSearchInput.trim());
    setAgentPage(1);
  };

  // Pagination logic
  const filteredUsers = users.filter(u =>
    (`${u.firstName} ${u.lastName}`.toLowerCase().includes(userSearchTerm.toLowerCase()) ||
      u.email.toLowerCase().includes(userSearchTerm.toLowerCase()))
  );
  const userTotalPages = Math.ceil(filteredUsers.length / itemsPerPage);
  const paginatedUsers = filteredUsers.slice(
    (userPage - 1) * itemsPerPage,
    userPage * itemsPerPage
  );

  const filteredTickets = tickets.filter(t =>
    t.title.toLowerCase().includes(ticketSearchTerm.toLowerCase()) &&
    (ticketStatusFilter === 'ALL' || t.status === ticketStatusFilter) &&
    (ticketPriorityFilter === 'ALL' || (typeof t.priority === 'string' ? t.priority === ticketPriorityFilter : (t.priority && t.priority.name === ticketPriorityFilter)))
  );
  const ticketTotalPages = Math.ceil(filteredTickets.length / itemsPerPage);
  const paginatedTickets = filteredTickets.slice(
    (ticketPage - 1) * itemsPerPage,
    ticketPage * itemsPerPage
  );

  // prepare agent data for chart + pagination
  const agentData = metrics
    ? Object.entries(metrics.agentPerformance).map(([n, t]) => ({ label: n, value: t }))
    : [];
  const filteredAgents = agentData.filter(a => a.label.toLowerCase().includes(agentSearchTerm.toLowerCase()));
  const sortedAgents = filteredAgents.sort((a, b) => b.value - a.value);
  const agentTotalPages = Math.ceil(sortedAgents.length / agentItemsPerPage);
  const paginatedAgents = sortedAgents.slice(
    (agentPage - 1) * agentItemsPerPage,
    agentPage * agentItemsPerPage
  );

  return (
  <div className="min-h-screen bg-gray-50">
      <div className="max-w-6xl mx-auto p-6 space-y-8">
        <Navbar title="DeskNova Admin Dashboard" onLogout={logout} />

        {/* Metrics */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {metrics ? (
          <>
            <Card>
              <h3 className="font-semibold text-lg">General</h3>
              <div className="mt-4 space-y-4">
                <div className="text-3xl font-bold">{metrics.totalTickets}</div>
                <div>
                  <div className="flex justify-between items-center">
                    <span className="text-sm text-gray-600">Completion</span>
                    <span className="text-xs font-medium">
                      {metrics.completionRate.toFixed(1)}%
                    </span>
                  </div>
                  <div className="w-full bg-gray-200 rounded-full h-3 mt-1">
                    <div
                      className="bg-green-500 h-3 rounded-full"
                      style={{ width: `${metrics.completionRate}%` }}
                    />
                  </div>
                </div>
                <div>
                  <span className="text-sm text-gray-600">Avg resolve</span>
                  <div className="text-xl font-medium">
                    {metrics.avgResolutionTimeMinutes.toFixed(1)} hrs
                  </div>
                </div>
              </div>
            </Card>
            <Card>
              <h3 className="font-semibold text-lg">By Status</h3>
              <div className="mt-2">
                <BarChart
                  data={Object.entries(metrics.ticketsByStatus).map(([s, c]) => ({ label: s, value: c }))}
                />
              </div>
            </Card>
            <Card>
              <h3 className="font-semibold text-lg">Priority Avg / Month</h3>
              {metrics.avgResolvePerPriorityPerMonth ? (
                <div className="mt-2 space-y-4">
                  {Object.entries(metrics.avgResolvePerPriorityPerMonth).map(([m, prios]) => (
                    <div key={m}>
                      <strong className="block mb-1">{m}</strong>
                      <BarChart
                        data={Object.entries(prios).map(([p, t]) => ({ label: p, value: t }))}
                        formatValue={(v) => `${v.toFixed(1)}h`}
                      />
                    </div>
                  ))}
                </div>
              ) : (
                <p>No data available</p>
              )}
            </Card>
          </>
        ) : (
          <p>Loading metrics...</p>
        )}
      </div>

      {/* Statistics - Agent Performance (search + asc sort) */}
      {metrics && (
        <div className="mt-4">
          <Card>
            <div className="flex justify-between items-center mb-2">
              <h3 className="font-semibold text-lg">Agent Performance</h3>
              <div className="flex items-center gap-2">
                <input
                  type="text"
                  placeholder="Search agent..."
                  value={agentSearchInput}
                  onChange={e => setAgentSearchInput(e.target.value)}
                  className="border rounded px-2 py-1"
                />
                <Button variant="primary" onClick={performAgentSearch}>Search</Button>
              </div>
            </div>
            {paginatedAgents.length > 0 ? (
              <div className="mt-2">
                <BarChart
                  data={paginatedAgents}
                  formatValue={(v) => `${v.toFixed(1)}h`}
                />
                {agentTotalPages > 1 && (
                  <div className="mt-4">
                    <Pagination
                      currentPage={agentPage}
                      totalPages={agentTotalPages}
                      onPageChange={setAgentPage}
                    />
                  </div>
                )}
              </div>
            ) : (
              <p className="text-sm text-gray-600 mt-2">No agents found.</p>
            )}
          </Card>
        </div>
      )}

      {/* User management */}
      <section>
        <div className="flex justify-between items-center">
          <h2 className="text-xl font-semibold">User Management</h2>
          <Button variant="primary" onClick={openCreateUser}>+ Add New User</Button>
        </div>
        <div className="mt-2 flex items-center">
          <input
            type="text"
            placeholder="Search users..."
            value={userSearchInput}
            onChange={e => setUserSearchInput(e.target.value)}
            className="flex-1 border rounded px-3 py-1 mr-2"
          />
          <Button variant="primary" onClick={performUserSearch}>Search</Button>
        </div>
        <div className="overflow-x-auto mt-4">
          <table className="min-w-full divide-y divide-gray-200 bg-white shadow rounded-lg">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Name</th>
                <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Email</th>
                <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Role</th>
                <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Last Updated</th>
                <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {paginatedUsers.map(u => (
                <tr key={u.email}>
                  <td className="px-4 py-2 whitespace-nowrap">{u.firstName} {u.lastName}</td>
                  <td className="px-4 py-2 whitespace-nowrap">{u.email}</td>
                  <td className="px-4 py-2 whitespace-nowrap">{u.role}</td>
                  <td className="px-4 py-2 whitespace-nowrap">{u.updatedAt ? new Date(u.updatedAt).toLocaleString() : 'N/A'}</td>
                  <td className="px-4 py-2 whitespace-nowrap space-x-2">
                    <Button variant="ghost" onClick={() => openEditUser(u)} className="px-2">Edit</Button>
                    <Button variant="danger" onClick={() => deleteUser(u.email)} className="px-2">Delete</Button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        {userTotalPages > 1 && (
          <Pagination
            currentPage={userPage}
            totalPages={userTotalPages}
            onPageChange={setUserPage}
          />
        )}
      </section>

      {/* Ticket management */}
      <section className="mt-8">
        <h2 className="text-xl font-semibold mb-2">System Ticket Management (All Tickets)</h2>
        <div className="mt-2 flex items-center gap-2">
          <select
            value={ticketStatusFilter}
            onChange={e => { setTicketStatusFilter(e.target.value); setTicketPage(1); }}
            className="border rounded px-2 py-1"
          >
            <option value="ALL">All Status</option>
            <option value="OPEN">Open</option>
            <option value="IN_PROGRESS">In Progress</option>
            <option value="RESOLVED">Resolved</option>
            <option value="CLOSED">Closed</option>
          </select>
          <select
            value={ticketPriorityFilter}
            onChange={e => { setTicketPriorityFilter(e.target.value); setTicketPage(1); }}
            className="border rounded px-2 py-1"
          >
            <option value="ALL">All Priority</option>
            <option value="LOW">Low</option>
            <option value="MEDIUM">Medium</option>ss
            <option value="HIGH">High</option>
            <option value="CRITICAL">Critical</option>
          </select>
          <input
            type="text"
            placeholder="Search tickets..."
            value={ticketSearchInput}
            onChange={e => setTicketSearchInput(e.target.value)}
            className="flex-1 border rounded px-3 py-1"
          />
          <Button variant="primary" onClick={performTicketSearch}>Search</Button>
        </div>
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200 bg-white shadow rounded-lg">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
                <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Title</th>
                <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Client</th>
                <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Assigned Agent</th>
                <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Priority</th>
                <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
                <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {paginatedTickets.map(t => (
                <tr key={t.id}>
                  <td className="px-4 py-2">#{t.id}</td>
                  <td className="px-4 py-2">{t.title}</td>
                  <td className="px-4 py-2">{t.client.fullName}</td>
                  <td className="px-4 py-2">{t.agent ? t.agent.fullName : <strong className="text-red-600">UNASSIGNED</strong>}</td>
                  <td className="px-4 py-2">
                    <p className="text-sm text-gray-600">Priority: <span className={`inline-block px-2 py-1 rounded text-xs font-semibold ${getPriorityClasses(typeof t.priority === 'string' ? t.priority : (t.priority?.name || ''))}`}>{typeof t.priority === 'string' ? t.priority : (t.priority?.name || 'N/A')}</span></p>
                  </td>
                  <td className="px-4 py-2">
                    <span className={`inline-block px-2 py-1 rounded text-xs font-semibold ${getStatusClasses(t.status)}`}>
                      {t.status.replace('_', ' ')}
                    </span>
                  </td>
                  <td className="px-4 py-2 space-x-2">
                    <Button variant="ghost" onClick={() => openTicketEdit(t.id)} className="px-2">Edit</Button>
                    <Button variant="danger" onClick={() => deleteTicket(t.id)} className="px-2">Delete</Button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        {ticketTotalPages > 1 && (
          <Pagination
            currentPage={ticketPage}
            totalPages={ticketTotalPages}
            onPageChange={setTicketPage}
          />
        )}
      </section>

      {/* Modals */}
      {showUserModal && (
        <UserModal
          onClose={() => setShowUserModal(false)}
          onSave={saveUser}
          edit={isEditUser}
          user={editingUser}
        />
      )}

      {showTicketModal && editingTicket && (
        <TicketModal
          ticket={editingTicket}
          onClose={() => setShowTicketModal(false)}
          onSave={saveTicket}
        />
      )}
    </div>
    </div>
  )
}


