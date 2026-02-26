const API_BASE = "http://localhost:8080/api/ticket";
const COMMENT_API = "http://localhost:8080/api/comment";
const token = localStorage.getItem("jwtToken");
let currentTicketId = null;

document.addEventListener('DOMContentLoaded', fetchMyTickets);

async function fetchMyTickets() {
    const response = await fetch(API_BASE, {
        headers: { "Authorization": `Bearer ${token}` }
    });
    const data = await response.json();
    renderTickets(data.content);
}

function renderTickets(tickets) {
    const container = document.getElementById('ticketContainer');
    container.innerHTML = tickets.map(t => `
        <div class="ticket-card">
            <h3>${t.title} <span class="badge">${t.status}</span></h3>
            <p>Priority: ${t.priority.name}</p>
            <button onclick="viewTicket(${t.id})">View & Comment</button>
            ${t.status === 'RESOLVED' ? `
                <button class="btn-success" onclick="updateStatus(${t.id}, 'CLOSED')">Confirm & Close</button>
                <button class="btn-warning" onclick="updateStatus(${t.id}, 'OPEN')">Re-open</button>
            ` : ''}
        </div>
    `).join('');
}

async function updateStatus(id, newStatus) {
    await fetch(`${API_BASE}/edit/status/${id}`, {
        method: 'PUT',
        headers: {
            "Authorization": `Bearer ${token}`,
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ status: newStatus })
    });
    fetchMyTickets();
}

async function viewTicket(id) {
    currentTicketId = id;
    const response = await fetch(`${API_BASE}/${id}`, {
        headers: { "Authorization": `Bearer ${token}` }
    });
    const ticket = await response.json();

    document.getElementById('ticketDetails').innerHTML = `
        <h3>${ticket.title}</h3>
        <p>${ticket.description}</p>
    `;
    renderComments(ticket.comments);
    document.getElementById('detailsModal').style.display = 'block';
}

function renderComments(comments) {
    const list = document.getElementById('commentList');
    list.innerHTML = comments.map(c => `
        <div class="comment">
            <strong>${c.user.firstName}:</strong> ${c.comment}
            <small>${new Date(c.createdAt).toLocaleString()}</small>
        </div>
    `).join('');
}

async function submitComment() {
    const text = document.getElementById('newCommentText').value;
    await fetch(`${COMMENT_API}/create`, {
        method: 'POST',
        headers: {
            "Authorization": `Bearer ${token}`,
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ ticketId: currentTicketId, comment: text })
    });
    document.getElementById('newCommentText').value = '';
    viewTicket(currentTicketId); // Refresh details
}