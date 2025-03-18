import {Demon} from "@/lib/models";
import {useDrag} from "react-dnd";
import React from "react";

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
    const [{isDragging}, drag] = useDrag(() => ({
        type: "ROW",
        item: demon.id,
        collect: (monitor) => ({
            isDragging: monitor.isDragging()
        }),
    }));

    return (
        <tr
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
