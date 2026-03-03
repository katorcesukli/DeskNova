// ==== Zod Schema for validation and type inference ===

import z from "zod";
import { errorMessages, MAX_CHAR, MIN_CHAR } from "../src/lib/validation.util";
import { ROLE_VALUES, TICKET_CATEGORY_VALUES, TICKET_PRIORITY_VALUES, TICKET_STATUS_VALUES } from "./enums";


export const User = z.object({
    id: z.number(),
    firstName: z
        .string(errorMessages.invalidType('First Name', 'text'))
        .min(MIN_CHAR, errorMessages.minChar('First Name'))
        .max(MAX_CHAR, errorMessages.maxChar('First Name', MAX_CHAR))
        .trim(),
    lastName: z
        .string(errorMessages.invalidType('Last Name', 'text'))
        .min(MIN_CHAR, errorMessages.minChar('Last Name'))
        .max(MAX_CHAR, errorMessages.maxChar('Last Name', MAX_CHAR))
        .trim(),
    password: z
        .string(errorMessages.invalidType('Password', 'text'))
        .min(4, errorMessages.minChar('Password'))
        .max(MAX_CHAR, errorMessages.maxChar('Password', MAX_CHAR))
        .trim(),
    role: z.enum(ROLE_VALUES),
    createdAt: z.date(),
    updatedAt: z.date(),
});

export const Tickets = z.object({
    id: z.number(),
    title: z
        .string(errorMessages.invalidType('Ticket Title', 'text'))
        .min(MIN_CHAR, errorMessages.minChar('Ticket Title'))
        .max(MAX_CHAR, errorMessages.maxChar('Ticket Title', MAX_CHAR))
        .trim(),
    description: z
        .string(errorMessages.invalidType('Ticket Description', 'text'))
        .min(MIN_CHAR, errorMessages.minChar('Ticket Description'))
        .max(MAX_CHAR, errorMessages.maxChar('Ticket Description', MAX_CHAR))
        .trim(),
    status: z.enum(TICKET_STATUS_VALUES, 'Only select status from the listed options'),
    priority: z.enum(TICKET_PRIORITY_VALUES, 'Only select priority from the listed options'),
    category: z.enum(TICKET_CATEGORY_VALUES, 'Only select category from the listed options'),
    assignedAt: z.date(),
    dateClosed: z.date(),
    dateResolved: z.date(),
    dateOpened: z.date(),
    updatedAt: z.date(),
});

export const TicketComments = z.object({
    id: z.number(),
    ticket_id: z.number(),
    user_id: z.number(),
    comment: z
        .string(errorMessages.invalidType('Ticket Comment', 'text'))
        .min(MIN_CHAR, errorMessages.minChar('Ticket Comment'))
        .trim(),
    createdAt: z.date()
});

export const InsertTicketSchema = Tickets.pick({
    title: true,
    description: true,
    status: true,
    priority: true,
    category: true,
})
