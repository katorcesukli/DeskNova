import type { TicketResponse } from "../../../types";
import { getPriorityLabelStyles, getStatusLabelStyles } from "../../lib/utils";

export default function TicketCardDetail({ticket}: {ticket: TicketResponse}){
    
    return (
        <div className="border-2 p-4 shadow-lg rounded-sm w-full flex flex-col gap-2">
            <div className="flex justify-between items-center">
                <p className="text-lg/5 font-medium">{ticket.title}</p>

                {/* todo change to either 3-dot option icon or eye icon */}
                <span className="text-md">🔍</span>
            </div>
            <p className="text-sm text-muted-foreground">{ticket.description}</p>
            
            <div className="flex gap-1">
                <div className ={getPriorityLabelStyles(ticket.priority)}>
                    <span>{ticket.priority.toLocaleLowerCase()}</span>
                </div>
                <div className={getStatusLabelStyles(ticket.status)}>
                    <p>{ticket.status.toLocaleLowerCase()}</p>
                </div>
                <div className="text-xs px-4 py-2 text-foreground rounded-md border capitalize font-medium">
                    <span>{ticket.category.toLocaleLowerCase()}</span>
                </div>
            </div>
            <div className="flex justify-between">
                <div className="flex gap-2 items-center bg-accent w-fit p-2 rounded-sm border-secondary inset-shadow-sm text-sm">
                    <span className="text-muted-foreground">Agent: </span>
                    <span>{ticket.agent?.fullName ?? "Not Assigned"}</span>
                </div>
                <button className="hover:bg-primary/90 hover:cursor-pointer bg-primary border-blue-400 border w-fit p-2 rounded-sm">
                    <span className="text-primary-foreground text-sm font-medium">Close Ticket</span>
                </button>
            </div>
        </div>
    );
}