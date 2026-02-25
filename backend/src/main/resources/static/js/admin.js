const API_URL = "http://localhost:8080/api/user";
const userForm = document.getElementById('userForm');
const userModalContainer = document.getElementById('userModal');

// Initial load
document.addEventListener('DOMContentLoaded', () => {
    // Hide form by default
    userModalContainer.style.display = 'none';
    fetchUsers();
});

// READ: Fetch all users
async function fetchUsers() {
    try {
        console.log("Attempting to fetch users from:", API_URL);
        const response = await fetch(API_URL);

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const users = await response.json();
        console.log("Users received from DB:", users);

        const tbody = document.getElementById('userTableBody');
        tbody.innerHTML = '';

        if (users.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5">No users found in database.</td></tr>';
            return;
        }

        users.forEach(user => {
            tbody.innerHTML += `
                <tr>
                    <td>${user.firstName} ${user.lastName}</td>
                    <td>${user.email}</td>
                    <td>${user.role}</td>
                    <td>${user.updatedAt ? new Date(user.updatedAt).toLocaleString() : 'N/A'}</td>
                    <td>
                        <button type="button" onclick="openEditModal('${user.email}', '${user.firstName}', '${user.lastName}', '${user.role}')">Edit</button>
                        <button type="button" onclick="deleteUser('${user.email}')">Delete</button>
                    </td>
                </tr>
            `;
        });
    } catch (error) {
        console.error("Error fetching users:", error);
    }
}

// CREATE/UPDATE Logic
userForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    const isEdit = document.getElementById('isEditMode').value === "true";
    const originalEmail = document.getElementById('originalEmail').value;

    const userData = {
        firstName: document.getElementById('firstName').value,
        lastName: document.getElementById('lastName').value,
        email: document.getElementById('email').value,
        password: document.getElementById('password').value,
        role: document.getElementById('role').value
    };

    const url = isEdit ? `${API_URL}/admin/${originalEmail}` : `${API_URL}/admin`;
    const method = isEdit ? 'PUT' : 'POST';

    try {
        const response = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(userData)
        });

        if (response.ok) {
            userModalContainer.style.display = 'none';
            fetchUsers();
            userForm.reset();
        } else {
            const err = await response.json();
            alert("Error saving user: " + (err.message || "Email might already exist"));
        }
    } catch (error) {
        console.error("Error saving user:", error);
    }
});

// DELETE
async function deleteUser(email) {
    if (confirm(`Are you sure you want to delete ${email}?`)) {
        try {
            const response = await fetch(`${API_URL}/admin/${email}`, { method: 'DELETE' });
            if (response.ok) {
                fetchUsers();
            } else {
                alert("Failed to delete user.");
            }
        } catch (error) {
            console.error("Error deleting user:", error);
        }
    }
}

// UI Helper: Show Add form
function openCreateModal() {
    document.getElementById('modalTitle').innerText = "Add New User";
    document.getElementById('isEditMode').value = "false";
    userForm.reset();
    userModalContainer.style.display = 'block';
}

// UI Helper: Show Edit form with data
function openEditModal(email, first, last, role) {
    document.getElementById('modalTitle').innerText = "Edit User";
    document.getElementById('isEditMode').value = "true";
    document.getElementById('originalEmail').value = email;

    document.getElementById('firstName').value = first;
    document.getElementById('lastName').value = last;
    document.getElementById('email').value = email;
    document.getElementById('role').value = role;
    document.getElementById('password').value = "";

    userModalContainer.style.display = 'block';
}

// Metri block
const METRIC_URL = "http://localhost:8080/api/metric/admin";

document.addEventListener('DOMContentLoaded', () => {
userModalContainer.style.display = 'none';
    fetchUsers();
    fetchMetrics();
});

async function fetchMetrics() {
  try {
      const response = await fetch(METRIC_URL);
      const data = await response.json();

      const metricsContent = document.getElementById('metricsContent');

      const statusItems = Object.entries(data.ticketsByStatus)
            .map(([status, count]) => `<li>${status}: ${count}</li>`).join('');

      const agentItems = Object.entries(data.agentPerformance)
            .map(([name, time]) => `<li>${name}: ${time.toFixed(1)}m</li>`).join('');

        metricsContent.innerHTML = `
             <div class="metric-card">
                 <strong>General</strong>
                 <p>Total: ${data.totalTickets}</p>
                 <p>Completion: ${data.completionRate.toFixed(1)}%</p>
                 <p>Avg Resolve: ${data.avgResolutionTimeMinutes.toFixed(1)}m</p>
             </div>
             <div class="metric-card">
                 <strong>By Status</strong>
                 <ul>${statusItems || '<li>No data</li>'}</ul>
             </div>
             <div class="metric-card">
                 <strong>Agent Performance (Avg)</strong>
                 <ul>${agentItems || '<li>No data</li>'}</ul>
             </div>
        `;
      } catch (error) {
          console.error("Metric Error:", error);
          document.getElementById('metricsContent').innerHTML = "Error loading metrics.";
   }
}