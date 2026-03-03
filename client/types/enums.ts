
export const ROLE_VALUES = [
  "CLIENT",
  "AGENT",
  "ADMIN"
] as const;



// ticket priority enum

export const TICKET_PRIORITY_VALUES = [
  "LOW",
  "MEDIUM",
  "HIGH",
  "CRITICAL"
] as const;

export const TICKET_CATEGORY_VALUES = [
    "HARDWARE",
    "SOFTWARE",
    "NETWORK",
    "ACCOUNTS_AND_ACCESS",
    "SERVICES",
    "GENERAL"
] as const;

export const TICKET_STATUS_VALUES = [
    "OPEN",
    "IN_PROGRESS",
    "RESOLVED",
    "CLOSED",
] as const;


export type TicketPriority = (typeof TICKET_PRIORITY_VALUES)[number];
export type TicketCategory = (typeof TICKET_CATEGORY_VALUES)[number];
export type TicketStatus = (typeof TICKET_STATUS_VALUES)[number];
export type Role = (typeof ROLE_VALUES)[number];