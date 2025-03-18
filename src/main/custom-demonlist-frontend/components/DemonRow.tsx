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
}

export default function DemonRow({
                                     demon,
                                     handleDoubleClick,
                                     handleChange,
                                     handleBlur,
                                     handleKeyDown,
                                     editing,
                                     data}: DemonRowProps) {
    const ref = useRef<HTMLTableRowElement>(null);
    const [{isDragging}, drag] = useDrag(() => ({
        type: "ROW",
        item: demon.id,
        collect: (monitor) => ({
            isDragging: monitor.isDragging()
        }),
    }));

    const [, drop] = useDrop({
        accept: "ROW",
        hover: (draggedItem: {id: number}) => {
            if( draggedItem.id === demon.id) return;
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
