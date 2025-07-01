import {Demon} from "@/lib/models";
import {useDrag, useDrop} from "react-dnd";
import React, {useRef} from "react";
import {useAuthContext} from "@/context/AuthContext";
import {deleteDemon} from "@/api/api";
import DeleteButton from "@/components/DeleteButton";
import DropdownWithImages from "@/components/DropdownWithImages";
import CompletionDateInput from "@/components/CompletionDateInput";

interface DemonRowProps {
    demonPlacement: number,
    demons: Demon[],
    handleDoubleClick: (demon: Demon, fieldName: string) => void,
    handleChange: (e: React.ChangeEvent<HTMLInputElement>) => void,
    handleSelectChange: (newDiff: string, demon: Demon) => void,
    handleBlur: (demon: Demon, fieldName: string) => void,
    handleKeyDown: (demon: Demon, fieldName: string, e: React.KeyboardEvent<HTMLInputElement>) => void,
    editing: { id: number | null, field: string | null },
    data: string,
    rearrangeDemonlistRequest: (id: number, target: number) => void,
    rearrangeDemonlist: (current: number, target: number) => void,
    deleteDemonLocally: (targetDemon: Demon) => void,
    handleUpdateCompletionDate: (e: React.ChangeEvent<HTMLInputElement>, demon: Demon, fieldName: string) => void,
}

export default function DemonRow({
                                     demonPlacement,
                                     demons,
                                     handleDoubleClick,
                                     handleChange,
                                     handleSelectChange,
                                     handleBlur,
                                     handleKeyDown,
                                     editing,
                                     data,
                                     rearrangeDemonlistRequest,
                                     rearrangeDemonlist,
                                     deleteDemonLocally,
                                     handleUpdateCompletionDate
                                 }: DemonRowProps,) {
    const {accessToken} = useAuthContext()
    const demon = demons.find(d => d.placement === demonPlacement + 1);

    console.log("DEMONROW demonId: ", demonPlacement);
    console.log("The demon: ", demon);

    if (!demon) {
        console.log("Demon is undefined");
        return null;
    }

    console.log("DEMONROW ID: ", demon.id);

    const DIFFICULTIES = [
        'N/A', 'EXTREME_DEMON', 'INSANE_DEMON', 'HARD_DEMON', 'MEDIUM_DEMON', 'EASY_DEMON',
        'OBSIDIAN_DEMON', 'AZURITE_DEMON', 'AMETHYST_DEMON', 'ONYX_DEMON', 'PEARL_DEMON', 'DIAMOND_DEMON',
        'RUBY_DEMON', 'EMERALD_DEMON', 'JADE_DEMON', 'SAPPHIRE_DEMON', 'PLATINUM_DEMON',
        'AMBER_DEMON', 'GOLD_DEMON', 'SILVER_DEMON', 'BRONZE_DEMON', 'BEGINNER_DEMON',
        'INSANE', 'HARDER', 'HARD', 'NORMAL', 'EASY', 'AUTO'
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
        deleteDemonLocally(demon);
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
            {['delete', 'placement', 'name', 'author', 'difficulty', 'attemptsCount', 'worstFail', 'enjoymentRating', 'dateOfCompletion', 'gddlTier', 'aredlPlacement']
                .map((fieldName) => {
                    const isEditable = ['name', 'author', 'attemptsCount', 'worstFail', 'enjoymentRating'].includes(fieldName);

                    return (
                        <td
                            onDoubleClick={isEditable ? () => handleDoubleClick(demon, fieldName) : undefined}
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
                                        <DropdownWithImages
                                            options={DIFFICULTIES}
                                            selected={demon.difficulty ? demon.difficulty : "N/A"}
                                            onSelect={(newDiff) => handleSelectChange(newDiff, demon)}
                                        />
                                    ) : fieldName === "dateOfCompletion" ? (
                                        <CompletionDateInput
                                            selectedDate={demon.dateOfCompletion}
                                            onInput={(e) => handleUpdateCompletionDate(e, demon, fieldName)}
                                        />
                                    ) :
                                    fieldName === "worstFail" && demon[fieldName] ? `${demon[fieldName as keyof Demon]}%` :
                                        fieldName === "enjoymentRating" && demon[fieldName] ? `${demon[fieldName as keyof Demon]}/100` :
                                            demon[fieldName as keyof Demon]}
                        </td>
                    )
                })}
        </tr>
    )
}
