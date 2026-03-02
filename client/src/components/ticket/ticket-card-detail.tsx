export default function TicketCardDetail(){
    return (
        <div className="border-2 p-4 shadow-lg rounded-sm w-full flex flex-col gap-2">
            <div className="flex justify-between items-center">
                <p className="text-lg/5 font-medium">Ticket Example Sample</p>

                {/* todo change to either 3-dot option icon or eye icon */}
                <span className="text-md">🔍</span>
            </div>
            <p className="text-sm text-muted-foreground">Lorem ipsum dolor sit amet consectetur adipisicing elit. Laudantium, earum molestias labore quibusdam dolorum laborum veniam at iusto repellendus ex?</p>
            

            <div className="flex gap-1">
                <div className= "text-sm bg-red-400 w-fit p-1 text-foreground rounded-md border border-red-200">
                    <span>Low</span>
                </div>
                <div className="text-sm bg-green-400 w-fit p-1 text-foreground rounded-md border border-green-200">
                    <span>In Progress</span>
                </div>
                <div className="w-fit p-1 text-sm text-muted-foreground rounded-md border">
                    <span>Hardware</span>
                </div>
            </div>
            <div className="flex justify-between">
                <div className="flex gap-2 items-center bg-accent w-fit p-2 rounded-sm border-secondary inset-shadow-sm text-sm">
                    <span className="text-muted-foreground">Agent: </span>
                    <span>Juan Dela J. Cruz</span>

                </div>
                <button className="hover:bg-primary/90 hover:cursor-pointer bg-primary border-blue-400 border w-fit p-2 rounded-sm">
                    <span className="text-primary-foreground text-sm font-medium">Close Ticket</span>
                </button>
            </div>
        </div>
    );
}