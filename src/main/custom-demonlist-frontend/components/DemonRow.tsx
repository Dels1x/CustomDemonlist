import {Demon} from "@/lib/models";
import {useDrag, useDrop} from "react-dnd";
import React, {useRef} from "react";
import {useAuthContext} from "@/context/AuthContext";
import {deleteDemon} from "@/api/api";
import DeleteButton from "@/components/DeleteButton";

interface DemonRowProps {
    demonPlacement: number,
    demons: Demon[],
    handleDoubleClick: (demon: Demon, fieldName: string) => void,
    handleChange: (e: React.ChangeEvent<HTMLInputElement>) => void,
    handleBlur: (demon: Demon, fieldName: string) => void,
    handleKeyDown: (demon: Demon, fieldName: string, e: React.KeyboardEvent<HTMLInputElement>) => void,
    editing: { id: number | null, field: string | null },
    data: string,
    rearrangeDemonlistRequest: (id: number, target: number) => void,
    rearrangeDemonlist: (current: number, target: number) => void,
    deleteDemonLocally: (targetId: number) => void
}

export default function DemonRow({
                                     demonPlacement,
                                     demons,
                                     handleDoubleClick,
                                     handleChange,
                                     handleBlur,
                                     handleKeyDown,
                                     editing,
                                     data,
                                     rearrangeDemonlistRequest,
                                     rearrangeDemonlist,
                                     deleteDemonLocally
                                 }: DemonRowProps,) {
    const {accessToken} = useAuthContext()
    const demon = demons.find(d => d.placement === demonPlacement + 1);

    console.log("DEMONROW demonId: ", demonPlacement);

    if (!demon) {
        console.log("Demon is undefined");
        return null;
    }

    console.log("DEMONROW ID: ", demon.id);

    const DIFFICULTIES = [
        'N/A', 'AUTO', 'EASY', 'NORMAL', 'HARD', 'HARDER', 'INSANE',
        'EASY_DEMON', 'MEDIUM_DEMON', 'HARD_DEMON', 'INSANE_DEMON', 'EXTREME_DEMON'
    ];

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
        deleteDemonLocally(demon.id);
    }

    const [, drop] = useDrop({
        accept: "ROW",
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
            {['delete', 'placement', 'name', 'author', 'difficulty', 'attemptsCount', 'enjoymentRating']
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
                                ) : fieldName === "difficulty" ? (
                                    <select>
                                        {DIFFICULTIES.map((diff) => (
                                            <option key={diff} value={diff}>{diff}</option>
                                        ))}
                                    </select>
                                ) :
                                demon[fieldName as keyof Demon]}
                    </td>
                ))}
        </tr>
    )
}
