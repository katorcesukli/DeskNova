import { useQuery } from '@tanstack/react-query';
import type { PaginatedResponse, TicketResponse } from '../types';
import type { AuthContextType } from '../src/context/AuthContext';
import { API_BASE_URL } from '../src/lib/constants';


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