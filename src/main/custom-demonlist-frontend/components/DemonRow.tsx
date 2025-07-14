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
    demon: Demon
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
    index: number,
    isAuthorizedToEdit: boolean,
}

export default function DemonRow({
                                     demonPlacement,
                                     demon,
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
                                     handleUpdateCompletionDate,
                                     index,
                                     isAuthorizedToEdit,
                                 }: DemonRowProps,) {
    const {accessToken} = useAuthContext()

    console.log("DEMONROW demonId: ", demonPlacement);

    if (!demon) {
        console.log("Demon is undefined");
        return null;
    }

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
        item: {id: demon.id, fromPlacement: demon.placement},
        collect: (monitor) => ({
            isDragging: monitor.isDragging()
        }),
    }));

    const [, drop] = useDrop({
        accept: "ROW",
        drop: (dragged: { id: number, fromPlacement: number }) => {
            if (!ref.current) return;

            const toPlacement = demon.placement;

            if (dragged.fromPlacement === toPlacement) return;

            rearrangeDemonlist(dragged.fromPlacement, toPlacement);
            rearrangeDemonlistRequest(dragged.id, toPlacement);
        }
    })

    drag(drop(ref));

    const handleDeleteDemon = async () => {
        if (!accessToken) {
            return;
        }

        console.log('demon', demon);

        await deleteDemon(demon.id, accessToken)
        deleteDemonLocally(demon);
    }

    function renderField(fieldName: string, demon: Demon): React.ReactNode {
        switch (fieldName) {
            case "delete":
                return <DeleteButton onDelete={handleDeleteDemon} label="X"/>;
            case "difficulty":
                return (
                    <DropdownWithImages
                        options={DIFFICULTIES}
                        selected={demon.difficulty || "N/A"}
                        onSelect={(newDiff) => handleSelectChange(newDiff, demon)}
                    />
                );
            case "dateOfCompletion":
                return (
                    <CompletionDateInput
                        selectedDate={demon.dateOfCompletion}
                        onInput={(e) => handleUpdateCompletionDate(e, demon, fieldName)}
                    />
                );
            case "worstFail":
                return demon.worstFail != null ? `${demon.worstFail}%` : '';
            case "enjoymentRating":
                return demon.enjoymentRating != null ? `${demon.enjoymentRating}/100` : '';
            case "gddlTier":
                return demon.gddlTier ? `Tier ${demon.gddlTier}` : '';
            case "aredlPlacement":
                return demon.aredlPlacement == null || demon.aredlPlacement === -1 ? '' : `#${demon.aredlPlacement}`;
            default:
                return demon[fieldName as keyof Demon]?.toString() || '';
        }
    }

    return (
        <tr
            ref={ref}
        >
            {['delete', 'placement', 'name', 'author', 'difficulty', 'attemptsCount', 'worstFail', 'enjoymentRating',
                'dateOfCompletion', 'gddlTier', 'aredlPlacement']
                .map((fieldName) => {
                    const isEditable = ['name', 'author', 'attemptsCount', 'worstFail', 'enjoymentRating'].includes(fieldName);

                    if (!isAuthorizedToEdit && fieldName === 'delete') {
                        return null;
                    }

                    return (
                        <td onDoubleClick={isEditable ? () => handleDoubleClick(demon, fieldName) : undefined}>
                            {editing.id === demon.id && editing.field === fieldName && isAuthorizedToEdit ? (
                                <input
                                    type="text"
                                    onChange={handleChange}
                                    onBlur={() => handleBlur(demon, fieldName)}
                                    onKeyDown={(e) => handleKeyDown(demon, fieldName, e)}
                                    autoFocus
                                    value={data}
                                />
                            ) : (
                                fieldName === "placement" ? demon.placement === index + 1 ? `#${demon.placement}` : `#${index + 1} (${demon.placement})` : renderField(fieldName, demon)
                            )}
                        </td>

                    )
                })}
        </tr>
    )
}
