import Button from "../../src/components/ui/Button";
import Modal from "../../src/components/ui/Modal";
import { getPriorityLabelStyles } from "../../src/lib/utils";
import { TICKET_CATEGORY_VALUES, TICKET_PRIORITY_VALUES } from "../../types/enums";


export default function CreateTicketModal(
    {onClose} : { onClose: () => void }
){
    return (
        <Modal onClose={onClose}>
            <h3 className="font-semibold text-lg">Create New Ticket</h3>
            <form className="space-y-4 mt-4">
                <input type="text" name="title" placeholder="Title" className="bg-card border p-2 text-sm w-full rounded-sm" />
                <div>
                    <label className="block text-sm font-medium text-foreground mb-1">Description</label>
                    <textarea
                        placeholder="Describe your issue..."
                        className="bg-card w-full p-2 h-auto text-sm border rounded resize-none"
                        rows={3}
                        
                        required
                    />
                </div>
                <div className="flex flex-col gap-4">

                    {/* priority options */}
                    <div className="flex items-center gap-2 ">
                        <label className="block text-sm font-medium">Priority:</label>
                        <div className="flex gap-1">
                            {
                                TICKET_PRIORITY_VALUES.map((priority) => (
                                <div key={priority}>
                                <input
                                    type="radio"
                                    id={priority}
                                    name="priority"
                                    value={priority}
                                    className="peer sr-only"
                                />
                                <label
                                    htmlFor={priority}
                                    className={getPriorityLabelStyles(priority)}
                                >
                                    {priority.toLocaleLowerCase()}
                                </label>
                                </div>
                            ))}
                        </div>
                    </div>
                </div>

                {/* ticket category options */}
                <div className="space-y-2">
                    <label className="text-sm font-medium">Category:</label>
                    
                    <div className="flex flex-wrap gap-x-2 gap-y-3 my-2">
                        {
                            TICKET_CATEGORY_VALUES.map((category) => (
                            <div key={category}>
                                <input
                                    type="radio"
                                    id={category}
                                    name="category"
                                    value={category}
                                    className="peer sr-only"
                                />
                                <label
                                    htmlFor={category}
                                    className="cursor-pointer rounded-lg px-4 py-2 capitalize text-xs font-medium border transition-all peer-checked:bg-gray-700 peer-checked:text-white"
                                >
                                    {category.replaceAll("_", " ").toLocaleLowerCase()}
                                </label>
                            </div>
                        ))}
                    </div>
                </div>
                <div className="flex gap-1 justify-end">
                    <Button className="border-primary/55 border " type="submit" variant="primary">Submit Ticket</Button>
                </div>
            </form>
        </Modal>
    );
}
