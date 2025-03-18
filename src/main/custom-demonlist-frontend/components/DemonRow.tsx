import {Demon} from "@/lib/models";
import {useDrag, useDrop} from "react-dnd";
import React, {useRef} from "react";

interface DemonRowProps {
    demon: Demon;
    handleDoubleClick: (demon: Demon, fieldName: string) => void;
    handleChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
    handleBlur: (demon: Demon, fieldName: string) => void;
    handleKeyDown: (demon: Demon, fieldName: string, e: React.KeyboardEvent<HTMLInputElement>) => void;
    editing: {id: number | null, field: string | null};
    data: string;
    rearrangeDemonlist: (id: number, target: number) => void;
}

export default function DemonRow({
                                     demon,
                                     handleDoubleClick,
                                     handleChange,
                                     handleBlur,
                                     handleKeyDown,
                                     editing,
                                     data,
                                     rearrangeDemonlist}: DemonRowProps) {
    const ref = useRef<HTMLTableRowElement>(null);
    const [{isDragging}, drag] = useDrag(() => ({
        type: "ROW",
        item: {id: demon.id, placement: demon.placement},
        collect: (monitor) => ({
            isDragging: monitor.isDragging()
        }),
    }));

    const [, drop] = useDrop({
        accept: "ROW",
        drop: (dragged: {id: number, placement: number}) => {
            if (!ref.current) return;

            console.log(demon.placement);
            console.log(dragged);
            console.log("REARRANGE!!!!");
            rearrangeDemonlist(dragged.id, demon.placement);
        }
    })

    drag(drop(ref));

    return (
        <tr
            ref={ref}
            key={demon.id}>
            {['placement', 'name', 'author', 'attemptsCount', 'enjoymentRating']
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
                            :
                            demon[fieldName as keyof Demon]}
                    </td>
                ))}
        </tr>
    )
}
