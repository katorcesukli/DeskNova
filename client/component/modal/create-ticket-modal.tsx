import Button from "../../src/components/ui/Button";
import Modal from "../../src/components/ui/Modal";
import { getPriorityLabelStyles } from "../../src/lib/utils";
import { TICKET_CATEGORY_VALUES, TICKET_PRIORITY_VALUES } from "../../types/enums";
import { useForm, type SubmitHandler } from 'react-hook-form';
import { InsertTicketSchema, type InsertTicketType } from '../../types/models';
import { zodResolver } from '@hookform/resolvers/zod';
import { useCreateTicket } from "../../hooks/use-tickets";
import type { ApiErrorResponse } from "../../types";
import { useQueryClient } from "@tanstack/react-query";



export default function CreateTicketModal(
    {onClose} : { onClose: () => void }
){

    const token = localStorage.getItem('jwtToken');

    const {register, handleSubmit, setError, reset, formState: { errors }} = useForm<InsertTicketType>({
        resolver: zodResolver(InsertTicketSchema),
        defaultValues: {
            status: "OPEN",
            priority: 'LOW',
            category: 'GENERAL'
        }
    })

    const createTicket = useCreateTicket();
    const _queryClient = useQueryClient();
    

    const onSubmit: SubmitHandler<InsertTicketType> = (data) => {
        createTicket.mutate(data,{
            onError: (error: ApiErrorResponse<string | Record<string, string>>) => {
                if(typeof error.message === "object"){
                    Object.entries(error.message).forEach(([field, message]) => {
                        setError(field as keyof InsertTicketType, {
                            type: "server",
                            message
                        })
                    })
                }
            },
            onSuccess: () => {
                console.log("Successfully created new ticket");
                reset();
                onClose();
                _queryClient.invalidateQueries({queryKey: ['tickets', token]})
            }
        });
    }



    return (
        <Modal onClose={onClose}>
            <h3 className="font-semibold text-lg">Create New Ticket</h3>
            <form onSubmit={handleSubmit(onSubmit)} className="space-y-4 mt-4">
                <input {...register('title')} type="text" name="title" placeholder="Title" className="bg-card border p-2 text-sm w-full rounded-sm" />
                {
                    errors.title &&
                    <p className="text-xs text-red-400">{errors.title?.message}</p>
                }
                <div>
                    <label className="block text-sm font-medium text-foreground mb-1">Description</label>
                    <textarea
                        {...register('description')}
                        placeholder="Describe your issue..."
                        className="bg-card w-full p-2 h-auto text-sm border rounded resize-none"
                        rows={3}
                    />
                    <p className="text-xs text-red-400">{errors.description?.message}</p>
                </div>
                <div className="flex flex-col gap-4">

                    {/* priority options */}
                    <div className="flex items-center gap-2 ">
                        <p className="block text-sm font-medium">Priority:</p>
                        <div className="flex gap-1">
                            {
                                TICKET_PRIORITY_VALUES.map((priority) => (
                                <div key={priority}>
                                <input
                                    {...register('priority')}
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
                    <p className="text-xs text-red-400">{errors.priority?.message}</p>
                </div>

                {/* ticket category options */}
                <div className="space-y-2">
                    <p className="text-sm font-medium">Category:</p>
                    
                    <div className="flex flex-wrap gap-x-2 gap-y-3 my-2">
                        {
                            TICKET_CATEGORY_VALUES.map((category) => (
                            <div key={category}>
                                <input
                                    {...register('category')}
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
                    <p className="text-xs text-red-400">{errors.category?.message}</p>
                </div>
                
                {/* Server side errors
                if
                <ErrorBox message={}> */}


                <div className="flex gap-1 justify-end">
                    <Button className="border-primary/55 border " type="submit" variant="primary">Submit Ticket</Button>
                </div>
            </form>
        </Modal>
    );
}
