const API_BASE = "http://localhost:8080/api/ticket";
const COMMENT_API = "http://localhost:8080/api/comment";
const token = localStorage.getItem("jwtToken");
let currentTicketId = null;

// Initial Load
document.addEventListener('DOMContentLoaded', () => {
    if (!token) {
        alert("No token found, please login.");
        return;
    }
    fetchAgentTickets();
});

// GET: Fetch tickets assigned to this agent
async function fetchAgentTickets() {
    try {
        const response = await fetch(API_BASE, {
            headers: { "Authorization": `Bearer ${token}` }
        });
        const data = await response.json();
        // data.content because of PaginatedResponse DTO
        sortIntoColumns(data.content || []);
    } catch (error) {
        console.error("Error fetching tickets:", error);
    }
}

// Logic to distribute tickets into 3 columns
function sortIntoColumns(tickets) {
    const statuses = ['OPEN', 'IN_PROGRESS', 'RESOLVED'];
    
    statuses.forEach(status => {
        const colDiv = document.querySelector(`#col-${status} .task-list`);
        colDiv.innerHTML = ''; // Clear existing
        
        const filtered = tickets.filter(t => t.status === status);
        
        filtered.forEach(t => {
            console.log("Ticket object:", t);
            const card = document.createElement('div');
            card.style.border = "1px dotted gray";
            card.style.margin = "10px 0";
            card.style.padding = "10px";
            
            card.innerHTML = `
                <strong>${t.title}</strong><br>
                <small>Client: ${t.client.fullName}</small><br>
                <small>Priority: ${t.priority}</small><br>
                <div style="margin-top:5px;">
                    ${status === 'OPEN' ? `<button onclick="updateStatus(${t.id}, 'IN_PROGRESS')">Start Work</button>` : ''}
                    ${status === 'IN_PROGRESS' ? `<button onclick="updateStatus(${t.id}, 'RESOLVED')">Resolve</button>` : ''}
                    <button onclick="viewTicket(${t.id})">View Details</button>
                </div>
            `;
            colDiv.appendChild(card);
        });
    });
}

// PUT: Update Ticket Status (Open -> In Progress -> Resolved)
async function updateStatus(id, newStatus) {
    try {
        const response = await fetch(`${API_BASE}/edit/status/${id}`, {
            method: 'PUT',
            headers: {
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ status: newStatus })
        });
        
        if (response.ok) {
            fetchAgentTickets(); // Refresh board
            if (currentTicketId === id) viewTicket(id); // Update details if open
        }
    } catch (error) {
        console.error("Failed to update status:", error);
    }
}

// GET: Single Ticket Details + Comments
async function viewTicket(id) {
    currentTicketId = id;
    try {
        const response = await fetch(`${API_BASE}/${id}`, {
            headers: { "Authorization": `Bearer ${token}` }
        });
        const ticket = await response.json();

        document.getElementById('ticketDetails').innerHTML = `
            <h2>${ticket.title}</h2>
            <p><strong>Status:</strong> ${ticket.status} | <strong>Category:</strong> ${ticket.category}</p>
            <p><strong>Description:</strong> ${ticket.description}</p>
        `;
        
        renderComments(ticket.comments || []);
        document.getElementById('detailsArea').style.display = 'block';
    } catch (error) {
        console.error("Error loading ticket details:", error);
    }
}

function renderComments(comments) {
    const list = document.getElementById('commentList');
    list.innerHTML = comments.map(c => `
        <div style="background: #eee; margin-bottom: 5px; padding: 5px;">
            <strong>${c.user.fullName}:</strong> ${c.comment} 
            <br><small>${new Date(c.createdAt).toLocaleString()}</small>
        </div>
    `).join('') || '<p>No comments yet.</p>';
}

// POST: Add new comment
async function submitComment() {
    const text = document.getElementById('newCommentText').value;
    if (!text) return;

    try {
        const response = await fetch(`${COMMENT_API}/create`, {
            method: 'POST',
            headers: {
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ 
                ticketId: currentTicketId, 
                comment: text 
            })
        });

        if (response.ok) {
            document.getElementById('newCommentText').value = '';
            viewTicket(currentTicketId); // Refresh details and comment list
        }
    } catch (error) {
        console.error("Comment submission failed:", error);
    }
}

function closeDetails() {
    document.getElementById('detailsArea').style.display = 'none';
    currentTicketId = null;
}

function logout() {
    localStorage.removeItem("jwtToken");
    window.location.href = "login.html";
}