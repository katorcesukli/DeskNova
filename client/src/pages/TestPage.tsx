import { useState } from "react";
import NavBar from "../components/layout/navbar";
import TicketCardDetail from "../components/ticket/ticket-card-detail";
import CreateTicketModal from "../../component/modal/create-ticket-modal";
import {useTickets} from '../../hooks/use-tickets'

export default function TestPage(){
    const token = localStorage.getItem('jwtToken');

    const {res, isLoading} = useTickets(token);
    const [createTicket, setCreateTicket] = useState(false);

    if(isLoading){
        return <div>Loading data...</div>
    }

    // why is this undefined
    const tickets = res?.content;
    
    return (
        <>
            <NavBar/>

            <section className="h-full w-full bg-secondary ">
                <div className="p-5 w-[750px] mx-auto border-red-500 border">
                    
                    <div className="flex items-center justify-between">
                        <header className="my-4">
                            <h2 className="text-lg">Tickets</h2>
                            <p className="text-muted-foreground text-sm">Manage your requests</p>
                        </header>
                        
                        <button className="hover:bg-green-600/80 hover:cursor-pointer text-sm font-medium bg-green-700 border border-green-600
                         p-2 rounded-sm"
                            onClick={() => setCreateTicket(true)}
                         >
                            Create Ticket
                        </button>

                    </div>

                    <section className="flex gap-1 items-center my-2">
                        <input className="flex-1 bg-card border rounded-sm p-2 text-sm font-light" placeholder="Search tickets" type="text" name="search" id="search" />
                            {/* Priority Filter */}
                            <select className="bg-card p-2 rounded-sm border text-sm" name="priority" id="priority">
                                <option value="ALL">All Priority</option>
                                <option value="LOW">Low</option>
                                <option value="MEDIUM">Medium</option>
                                <option value="HIGH">Resolved</option>
                                <option value="CRITICAL">Critical</option>
                            </select>
                            
                            {/* Status Filter */}
                            <select className="bg-card p-2 rounded-sm border text-sm" name="status" id="status">
                                <option value="ALL">All Status</option>
                                <option value="OPEN">Open</option>
                                <option value="IN_PROGRESS">In Progress</option>
                                <option value="RESOLVED">Resolved</option>
                                <option value="CLOSED">Closed</option>
                            </select>
                    </section>


                    {
                        tickets?.length !== 0 &&
                        tickets?.map((t) => {
                            return <TicketCardDetail key={t.id} ticket={t} />
                        })
                    }
                     

                    {/* create ticket modal section */}
                    {
                        createTicket && 
                        <CreateTicketModal 
                            onClose={() => setCreateTicket(false)}
                        />
                    }
                </div>
            </section>
        </>
    )
}