const API_URL = "http://localhost:8080/api/user";
const userForm = document.getElementById('userForm');
const userModalContainer = document.getElementById('userModal');
const ticketModalContainer = document.getElementById('ticketModal'); // New Modal for Tickets
const ticketForm = document.getElementById('ticketForm');

const token = localStorage.getItem("jwtToken");

// Initial load
document.addEventListener('DOMContentLoaded', () => {
    // Hide form by default
    userModalContainer.style.display = 'none';
    fetchUsers();
    fetchMetrics();
    fetchTickets();
});

const TICKET_URL = "http://localhost:8080/api/ticket";
async function fetchTickets() {
    try {
        const response = await fetch(`${TICKET_URL}`, {
            headers: { "Authorization": `Bearer ${token}` }
        });
        const data = await response.json();
        const tbody = document.getElementById('adminTicketBody');
        if(!tbody) return;

        tbody.innerHTML = (data.content || []).map(t => `
            <tr>
                <td>#${t.id}</td>
                <td>${t.title}</td>
                <td>${t.client.firstName}</td>
                <td>${t.agent ? t.agent.firstName : '<strong>UNASSIGNED</strong>'}</td>
                <td>${t.status}</td>
                <td>
                    <button type="button" onclick="openTicketEditModal(${t.id})">Edit</button>
                    <button type="button" style="color:red" onclick="deleteTicket(${t.id})">Delete</button>
                </td>
            </tr>
        `).join('');
    } catch (error) { console.error("Ticket Fetch Error:", error); }
}

async function openTicketEditModal(id) {
    try {
        const response = await fetch(`${TICKET_URL}/${id}`, {
            headers: { "Authorization": `Bearer ${token}` }
        });
        const t = await response.json();

        // Fill Ticket Modal Fields
        document.getElementById('editTicketId').value = t.id;
        document.getElementById('editTitle').value = t.title;
        document.getElementById('editDescription').value = t.description;
        document.getElementById('editCategory').value = t.category;
        document.getElementById('editStatus').value = t.status;
        document.getElementById('editPriority').value = t.priority.name;
        document.getElementById('editAgentId').value = t.agent ? t.agent.id : '';

        ticketModalContainer.style.display = 'block';
    } catch (error) { console.error("Error loading ticket:", error); }
}

ticketForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const id = document.getElementById('editTicketId').value;

    // Map to AdminTicketUpdateRequest DTO
    const updateData = {
        title: document.getElementById('editTitle').value,
        description: document.getElementById('editDescription').value,
        category: document.getElementById('editCategory').value,
        status: document.getElementById('editStatus').value,
        priority: document.getElementById('editPriority').value,
        agentId: document.getElementById('editAgentId').value || null
    };

    try {
        const response = await fetch(`${TICKET_URL}/admin/update/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(updateData)
        });

        if (response.ok) {
            ticketModalContainer.style.display = 'none';
            fetchTickets();
            fetchMetrics(); // Update charts since status might have changed
        } else {
            alert("Failed to update ticket.");
        }
    } catch (error) { console.error("Error saving ticket:", error); }
});

async function deleteTicket(id) {
    if (confirm(`Permanently delete Ticket #${id}?`)) {
        const response = await fetch(`${TICKET_URL}/delete/${id}`, {
            method: 'DELETE',
            headers: { "Authorization": `Bearer ${token}` }
        });
        if (response.ok) fetchTickets();
    }
}

// READ: Fetch all users
async function fetchUsers() {
    try {
        const token = localStorage.getItem("jwtToken"); // get token

        const response = await fetch(API_URL, {
            headers: {
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json"
            }
        });

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
        const token = localStorage.getItem("jwtToken");
        const response = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json','Authorization': `Bearer ${token}` },
            body: JSON.stringify(userData)
        });

        if (response.ok) {
            userModalContainer.style.display = 'none';
            await fetchUsers();
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
    const userData = {
        firstName: document.getElementById('firstName').value,
        lastName: document.getElementById('lastName').value,
        email: document.getElementById('email').value,
        password: document.getElementById('password').value,
        role: document.getElementById('role').value
    };
    if (confirm(`Are you sure you want to delete ${email}?`)) {
        try {
            const token = localStorage.getItem("jwtToken");
            const response = await fetch(`${API_URL}/admin/${email}`, {
                method: 'DELETE',
                headers: { 'Content-Type': 'application/json','Authorization': `Bearer ${token}` },
                body: JSON.stringify(userData)});
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
      const token = localStorage.getItem("jwtToken"); // get token

      const response = await fetch(METRIC_URL, {
          headers: {
              "Authorization": `Bearer ${token}`,
              "Content-Type": "application/json"
          }
      });
      const data = await response.json();
      //refer to metric service return
      const metricsContent = document.getElementById('metricsContent');

      //ticket status count
      const statusItems = Object.entries(data.ticketsByStatus)
            .map(([status, count]) => `<li>${status}: ${count}</li>`).join('');

      //get agent perf
      const agentItems = Object.entries(data.agentPerformance)
            .map(([name, time]) => `<li>${name}: ${time.toFixed(1)}m</li>`).join('');

      //getave per prio
      let priorityMonthlyHtml = "";
            if (data.avgResolvePerPriorityPerMonth) {
                priorityMonthlyHtml = Object.entries(data.avgResolvePerPriorityPerMonth)
                    .map(([month, priorities]) => {
                        const prioList = Object.entries(priorities)
                            .map(([prio, time]) => `<li>${prio}: ${time.toFixed(1)}m</li>`)
                            .join('');
                        return `<div style="margin-bottom:10px;"><strong>${month}</strong><ul>${prioList}</ul></div>`;
                    }).join('');
            }

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
             <div class="metric-card">
                 <strong>Priority Avg / Month</strong>
                 <div>${priorityMonthlyHtml || 'No data available'}</div>
             </div>
        `;
      } catch (error) {
          console.error("Metric Error:", error);
          document.getElementById('metricsContent').innerHTML = "Error loading metrics.";
   }
}