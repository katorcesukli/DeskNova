import { clsx, type ClassValue } from "clsx"
import { twMerge } from "tailwind-merge"
import type { TicketPriority, TicketStatus } from "../../types/enums";

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}


const priorityStyles: Record<TicketPriority, string> = {
  LOW: "border-green-500 text-green-400 hover:bg-green-500/10 peer-checked:bg-green-600 peer-checked:text-white",
  MEDIUM: "border-yellow-500 text-yellow-400 hover:bg-yellow-500/10 peer-checked:bg-yellow-500 peer-checked:text-black",
  HIGH: "border-orange-500 text-orange-400 hover:bg-orange-500/10 peer-checked:bg-orange-600 peer-checked:text-white",
  CRITICAL: "border-red-600 text-red-500 hover:bg-red-600/10 peer-checked:bg-red-700 peer-checked:text-white",
};

export function getPriorityLabelStyles(priority: TicketPriority) {
  return cn(
    // base style
    "cursor-pointer rounded-lg px-4 py-2 capitalize text-xs font-medium border transition-all",
    
    priorityStyles[priority]
  )
}

const statusStyles: Record<TicketStatus, string> = {
  OPEN: "border-blue-500 text-blue-400 hover:bg-blue-500/10 peer-checked:bg-blue-600 peer-checked:text-white",
  IN_PROGRESS: "border-amber-500 text-amber-400 hover:bg-amber-500/10 peer-checked:bg-amber-600 peer-checked:text-white",
  RESOLVED: "border-green-500 text-green-400 hover:bg-green-500/10 peer-checked:bg-green-600 peer-checked:text-white",
  CLOSED: "border-gray-500 text-gray-400 hover:bg-gray-500/10 peer-checked:bg-gray-600 peer-checked:text-white",
}

export function getStatusLabelStyles(status: TicketStatus) {
  return cn(
    "cursor-pointer rounded-lg px-4 py-2 capitalize text-xs font-medium border transition-all",
    statusStyles[status]
  )
}
