import type { ApiErrorResponse, PaginatedResponse, TicketResponse } from "../../types";
import type { InsertTicketType } from "../../types/models";
import { API_BASE_URL } from "../lib/constants";

async function request<T, E>(endpoint: string, options?: RequestInit): Promise<T | ApiErrorResponse<E>> {
    const token = localStorage.getItem('jwtToken');

    const res = await fetch(`${API_BASE_URL}${endpoint}`, {
        headers: { 
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`
        },
        ...options,
    });

    const data = await res.json();  
    
    if (!res.ok) {
        throw data as ApiErrorResponse<E>;
        // const err = await res.json().catch(() => ({ error: "Request failed" }));
        // throw new Error(err.error || err.message || "Request failed");
    }
  return data;
}

export const ticketRequest = {
    getTickets: (page = 0, pageSize = 10) => request<PaginatedResponse<TicketResponse>, string>(`/ticket?page=${page}&pageSize=${pageSize}`),
    createTicket: (ticket: InsertTicketType) => request<string, string | Record<string, string>>(`/ticket/create`, {
        method: "POST",
        body: JSON.stringify(ticket)
    })
}