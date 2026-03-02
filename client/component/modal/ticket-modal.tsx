import { useState } from "react";
import type { TicketDetail, UpdateTicketData } from "../../types";
import Modal from "../../src/components/ui/Modal";
import { getStatusClasses, getPriorityClasses } from "../../src/lib/badges";

export type  TicketModalProps = {
  ticket: TicketDetail;
  onClose: () => void;
  onSave: (updateData: UpdateTicketData) => Promise<void>;
}

export default function TicketModal(ticketProps : TicketModalProps){
  const {ticket, onClose, onSave} = ticketProps;
  const [title, setTitle] = useState(ticket.title);
  const [description, setDescription] = useState(ticket.description);
  const [status, setStatus] = useState(ticket.status);
  const [category, setCategory] = useState(ticket.category);
  const initialPriority = typeof ticket.priority === 'string' ? ticket.priority : (ticket.priority?.name || 'LOW');
  const [priority, setPriority] = useState(initialPriority);
  const [agentId, setAgentId] = useState(ticket.agent?.id);

  const handleSubmit = (e: React.SubmitEvent) => {
    e.preventDefault();
    onSave({ title, description, status, category, priority, agentId: agentId });
  };
    return (
    <Modal onClose={onClose}>
      <div className="flex items-center justify-between mb-4">
        <h3 className="text-xl font-semibold">Admin Ticket Override</h3>
        <div className="flex items-center gap-2">
          <span className={`inline-block px-2 py-1 rounded text-xs font-semibold ${getPriorityClasses(priority || '')}`}>{priority}</span>
          <span className={`inline-block px-2 py-1 rounded text-xs font-semibold ${getStatusClasses(status || '')}`}>{status.replace('_', ' ')}</span>
        </div>
      </div>
      <form onSubmit={handleSubmit} className="space-y-3">
        <div className="flex gap-4">
          <div className="flex-1">
            <label className="block">Title:</label>
            <input
              className="w-full border rounded px-2 py-1"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              required
            />
            <label className="block mt-2">Description:</label>
            <textarea
              className="w-full border rounded px-2 py-1"
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              rows={4}
              required
            />
          </div>
          <div className="flex-1">
            <label className="block">Status:</label>
            <select
              className="w-full border rounded px-2 py-1"
              value={status}
              onChange={(e) => setStatus(e.target.value)}
            >
              <option value="OPEN">OPEN</option>
              <option value="IN_PROGRESS">IN_PROGRESS</option>
              <option value="RESOLVED">RESOLVED</option>
              <option value="CLOSED">CLOSED</option>
            </select>
            <label className="block mt-2">Category:</label>
            <select
              className="w-full border rounded px-2 py-1"
              value={category}
              onChange={(e) => setCategory(e.target.value)}
            >
              <option value="HARDWARE">HARDWARE</option>
              <option value="SOFTWARE">SOFTWARE</option>
              <option value="NETWORK">NETWORK</option>
              <option value="ACCOUNTS_AND_ACCESS">ACCOUNTS_AND_ACCESS</option>
              <option value="SERVICES">SERVICES</option>
              <option value="GENERAL">GENERAL</option>
            </select>
            <label className="block mt-2">Priority:</label>
            <select
              className="w-full border rounded px-2 py-1"
              value={priority}
              onChange={(e) => setPriority(e.target.value)}
            >
              <option value="LOW">LOW</option>
              <option value="MEDIUM">MEDIUM</option>
              <option value="HIGH">HIGH</option>
              <option value="CRITICAL">CRITICAL</option>
            </select>
            <label className="block mt-2">Assign Agent (User ID):</label>
            <input
              type="number"
              className="w-full border rounded px-2 py-1"
              value={agentId}
              onChange={(e) => setAgentId(Number(e.target.value))}
              placeholder="Enter Agent ID"
            />
          </div>
        </div>
        <div className="flex justify-end space-x-2 mt-4">
          <button
            type="button"
            onClick={onClose}
            className="px-4 py-2 bg-gray-300 rounded"
          >
            Cancel
          </button>
          <button type="submit" className="px-4 py-2 bg-blue-600 text-white rounded">
            Save System Override
          </button>
        </div>
      </form>
    </Modal>
  );

}