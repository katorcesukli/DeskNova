import NavBar from "../components/layout/navbar";
import TicketCardDetail from "../components/ticket/ticket-card-detail";

export default function TestPage(){
    return (
        <>
            <NavBar role="Client"/>

            <section className="h-screen w-full bg-secondary ">
                <div className="p-5 w-[750px] mx-auto border-red-500 border">
                    
                    <div className="flex items-center justify-between">
                        <header className="my-4">
                            <h2 className="text-lg">Tickets</h2>
                            <p className="text-muted-foreground text-sm">Manage your requests</p>
                        </header>
                        
                        <button className="hover:bg-green-600/80 hover:cursor-pointer text-sm font-medium bg-green-700 border border-green-600
                         p-2 rounded-sm">
                            Create Ticket
                        </button>

                    </div>

                    {/* ticket cards */}
                    <TicketCardDetail />
                </div>
            </section>
        </>
    )
}