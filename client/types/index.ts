export type User = {
  id?: number;
  firstName: string;
  lastName: string;
  email: string;
  role: string;
  updatedAt?: string;
}

export type UpdateTicketData = {
    title: string;
    description: string;
    category: string;
    status: string;
    priority: string;
    agentId?: Person['id'];
  };


export type TicketWithComments = {
  id: number;
  title: string;
  description: string;
  status: string;
  priority: { name: string };
  comments?: Array<{ user: { fullName: string }; comment: string; createdAt: string }>;
}


export type LoginRequest = {
    email: User['email'],
    password: string
}

export type Person = {
    id: number,
    fullName: string
}

interface Ticket {
  id: number;
  title: string;
  client: Person;
  agent?: Person;
  status: string;
}

export interface TicketDetail extends Ticket {
  description: string;
  category: string;
  priority: { name: string };
  comments?: Array<{ user: { fullName: string }; comment: string; createdAt: string }>;
}

export type MetricResponse = {
  totalTickets: number;
  completionRate: number;
  avgResolutionTimeMinutes: number;
  ticketsByStatus: Record<string, number>;
  agentPerformance: Record<string, number>;
  avgResolvePerPriorityPerMonth?: Record<string, Record<string, number>>;
}