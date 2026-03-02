import { useState } from "react";
import type { User } from "../../types";
import Modal from "../../src/components/ui/Modal";

export type UserModalProps = {
  onClose: () => void;
  onSave: (u: User & { password?: string }) => Promise<void>;
  edit: boolean;
  user: User | null;
}
export default function UserModal(userModalProps: UserModalProps ) {
  const {user, edit, onSave, onClose } = userModalProps;
  const [firstName, setFirstName] = useState(user?.firstName || '');
  const [lastName, setLastName] = useState(user?.lastName || '');
  const [email, setEmail] = useState(user?.email || '');
  const [password, setPassword] = useState('');
  const [role, setRole] = useState(user?.role || 'CLIENT');

  const handleSubmit = (e: React.SubmitEvent) => {
    e.preventDefault();
    onSave({ firstName, lastName, email, password, role });
  };

  return (
    <Modal onClose={onClose}>
      <h3 className="text-xl font-semibold mb-4">{edit ? 'Edit User' : 'Add User'}</h3>
      <form onSubmit={handleSubmit} className="space-y-3">
        <div>
          <label className="block">First Name:</label>
          <input
            type="text"
            className="w-full border rounded px-2 py-1"
            value={firstName}
            onChange={(e) => setFirstName(e.target.value)}
            required
          />
        </div>
        <div>
          <label className="block">Last Name:</label>
          <input
            type="text"
            className="w-full border rounded px-2 py-1"
            value={lastName}
            onChange={(e) => setLastName(e.target.value)}
            required
          />
        </div>
        <div>
          <label className="block">Email:</label>
          <input
            type="email"
            className="w-full border rounded px-2 py-1"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
        </div>
        <div>
          <label className="block">Password:</label>
          <input
            type="password"
            className="w-full border rounded px-2 py-1"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder={edit ? 'Leave blank to keep same' : ''}
          />
        </div>
        <div>
          <label className="block">Role:</label>
          <select
            className="w-full border rounded px-2 py-1"
            value={role}
            onChange={(e) => setRole(e.target.value)}
          >
            <option value="CLIENT">CLIENT</option>
            <option value="AGENT">AGENT</option>
            <option value="ADMIN">ADMIN</option>
          </select>
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
            Save
          </button>
        </div>
      </form>
    </Modal>
  );
};

