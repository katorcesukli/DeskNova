import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import type { ApiErrorResponse, PaginatedResponse, TicketResponse } from '../types';
import type { AuthContextType } from '../src/context/AuthContext';
import { API_BASE_URL } from '../src/lib/constants';
import type { InsertTicketType } from '../types/models';
import { ticketRequest } from '../src/services/ticket.api';


type UseTicketResult = {
    res: PaginatedResponse<TicketResponse> | undefined
    isLoading: boolean,
    error: unknown,
    isSuccess: boolean
}

export function useTickets(token: AuthContextType['token']) : UseTicketResult
{
    const {data, isLoading, error, isSuccess} = useQuery<PaginatedResponse<TicketResponse>>({
        
        // prevent fetching if token is null
        enabled: !!token,
        queryKey: ['tickets', token],
        queryFn: async () => {
            const res = await fetch(`${API_BASE_URL}/ticket`, {
                headers: { Authorization: `Bearer ${token}` },
            });

            if(!res.ok) throw new Error("Failed to retrive tickets")
            return await res.json();
        },
        staleTime: 1000 * 60 * 5, // 5 minutes
    })

    return {
        res: data,
        isLoading: isLoading,
        error: error,
        isSuccess: isSuccess
    }
}

export function useCreateTicket(){

    return useMutation<
        unknown,
        ApiErrorResponse<string | Record<string, string>>,
        InsertTicketType
    >({
        mutationFn: async (ticket: InsertTicketType) => {
            await ticketRequest.createTicket(ticket);
        },
    });

    
}