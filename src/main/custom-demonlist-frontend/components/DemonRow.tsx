import {Demon} from "@/lib/models";
import {useDrag, useDrop} from "react-dnd";
import React, {useRef} from "react";
import {useAuthContext} from "@/context/AuthContext";
import {deleteDemon} from "@/api/api";
import {useDemonlistContext} from "@/context/DemonlistContext";
import DeleteButton from "@/components/DeleteButton";

interface DemonRowProps {
    demonId: number;
    demons: Demon[]
    handleDoubleClick: (demon: Demon, fieldName: string) => void;
    handleChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
    handleBlur: (demon: Demon, fieldName: string) => void;
    handleKeyDown: (demon: Demon, fieldName: string, e: React.KeyboardEvent<HTMLInputElement>) => void;
    editing: { id: number | null, field: string | null };
    data: string;
    rearrangeDemonlistRequest: (id: number, target: number) => void;
    rearrangeDemonlist: (current: number, target: number) => void;
}

export default function DemonRow({
                                     demonId,
                                     demons,
                                     handleDoubleClick,
                                     handleChange,
                                     handleBlur,
                                     handleKeyDown,
                                     editing,
                                     data,
                                     rearrangeDemonlistRequest,
                                     rearrangeDemonlist
                                 }: DemonRowProps,) {
    const {accessToken} = useAuthContext()
    const {refreshDemonlists} = useDemonlistContext();
    const demon = demons[demonId];
    const ref = useRef<HTMLTableRowElement>(null);
    const [, drag] = useDrag(() => ({
        type: "ROW",
        item: {id: demon.id, placement: demon.placement},
        collect: (monitor) => ({
            isDragging: monitor.isDragging()
        }),
    }));

    const handleDeleteDemon = async () => {
        if (!accessToken) {
            return;
        }

        console.log('demon', demon);

        await deleteDemon(demon.id, accessToken)
        refreshDemonlists();
    }

    const [, drop] = useDrop({
        accept: "ROW",
        hover: (dragged: { id: number, placement: number }) => {
            if (!ref.current) return;
        },
        drop: (dragged: { id: number, placement: number }) => {
            if (!ref.current) return;

            console.log(demon.placement);
            console.log(dragged);
            console.log("REARRANGE!!!!");
            rearrangeDemonlistRequest(dragged.id, demon.placement);
            rearrangeDemonlist(dragged.placement, demon.placement);
        }
    })

    drag(drop(ref));

    return (
        <tr
            ref={ref}
            key={demon.id}>
            {['delete', 'placement', 'name', 'author', 'attemptsCount', 'enjoymentRating']
                .map((fieldName) => (
                    <td
                        onDoubleClick={() => handleDoubleClick(demon, fieldName)}
                    >
                        {editing.id === demon.id && editing.field === fieldName ?
                            (<input
                                type="text"
                                onChange={handleChange}
                                onBlur={() => handleBlur(demon, fieldName)}
                                onKeyDown={(e) => handleKeyDown(demon, fieldName, e)}
                                autoFocus
                                value={data}
                            />)
                            : fieldName === "delete" ? (
                                    <DeleteButton
                                        onDelete={handleDeleteDemon}
                                        label="X"
                                    />
                            ) :
                            demon[fieldName as keyof Demon]}
                    </td>
                ))}
        </tr>
    )
}
