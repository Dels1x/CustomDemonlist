import {Demon} from "@/lib/models";
import React, {ChangeEvent, useState} from "react";
import {
    updateDemonAttempts,
    updateDemonAuthor,
    updateDemonDateOfCompletion,
    updateDemonDifficulty,
    updateDemonEnjoyment,
    updateDemonName,
    updateDemonPosition,
    updateDemonWorstFail
} from "@/api/api";
import {useAuthContext} from "@/context/AuthContext";
import DemonRow from "@/components/DemonRow";

interface DemonlistProps {
    demons: Demon[];
    setDemons: React.Dispatch<React.SetStateAction<Demon[]>>;
}

const MAX_INT = 2147483647;

const ListOfDemons: React.FC<DemonlistProps> = ({demons, setDemons}) => {
    const {accessToken} = useAuthContext()
    if (!accessToken) return;

    console.log("DEMONS: ", demons);

    const [editing, setEditing] = useState<{ id: number | null, field: string | null }>({
        id: null,
        field: null
    });
    const [data, setData] = useState<string>('');
    const [sortField, setSortField] = useState<string | null>(null);
    const [sortOrder, setSortOrder] = useState<"asc" | "desc">("asc");

    const handleDoubleClick = (demon: Demon, fieldName: string) => {
        let tempData = demon[fieldName as keyof Demon];
        if (!tempData) tempData = '';

        setData(String(tempData));
        setEditing({id: demon.id, field: fieldName})
    }

    const updateDemon = async (demon: Demon, fieldName: string, newValue?: string) => {
        const valueToUse = newValue ?? data;
        let updated: Demon | null = null;
        console.log("valueToUse: ", valueToUse);

        switch (fieldName) {
            case "name":
                updated = await updateDemonName(demon.id, valueToUse, accessToken);
                console.log("updated: ", updated);
                break;
            case "author":
                updated = await updateDemonAuthor(demon.id, valueToUse, accessToken);
                break;
            case "attemptsCount":
                if (valueToUse === '') break;
                await updateDemonAttempts(demon.id, valueToUse, accessToken);
                updated = {...demon, attemptsCount: Number(valueToUse)};
                break;
            case "worstFail":
                if (valueToUse === '') break;
                await updateDemonWorstFail(demon.id, valueToUse, accessToken);
                updated = {...demon, worstFail: Number(valueToUse)};
                break;
            case "enjoymentRating":
                if (valueToUse === '') break;
                await updateDemonEnjoyment(demon.id, valueToUse, accessToken);
                updated = {...demon, enjoymentRating: Number(valueToUse)};
                break;
            case "difficulty":
                if (valueToUse === '') break;
                await updateDemonDifficulty(demon.id, valueToUse, accessToken);
                updated = {...demon, difficulty: valueToUse};
                break;
            case "dateOfCompletion":
                if (valueToUse === '') break;
                await updateDemonDateOfCompletion(demon.id, valueToUse, accessToken);
                updated = {...demon, dateOfCompletion: valueToUse};
                break;
            default:
                console.error("Unknown field: ", fieldName);
        }

        if (updated) {
            setDemons(prev =>
                prev.map(d => d.id === demon.id ? updated! : d)
            );
            setData(valueToUse);  // Always sync your input state after update
        }
    }


    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const value = e.currentTarget.value;

        if (editing.field === "attemptsCount" || editing.field === "enjoymentRating" || editing.field === "worstFail") {
            if (/^\d*$/.test(value)) {
                const numValue = Number(value);

                if (numValue < MAX_INT) {
                    if (editing.field === "attemptsCount" || numValue < 101) {
                        setData(value);
                    }
                }
            } else {
                console.log("Value: ", value);
            }
        } else {
            setData(value);
        }
    }

    const handleSelectDifficultyChange = (newDiff: string, demon: Demon) => {
        handleBlur(demon, "difficulty", newDiff);
    }

    const handleUpdateCompletionDate = async (e: ChangeEvent<HTMLInputElement>,
                                              demon: Demon,
                                              fieldName: string) => {
        if (!accessToken) {
            console.error("accessToken is missing", e);
            return;
        }

        await handleBlur(demon, fieldName, e.target.value);
    }

    const handleBlur = async (demon: Demon, fieldName: string, newValue?: string) => {
        await updateDemon(demon, fieldName, newValue);
        setEditing({id: null, field: null});
    }

    const handleKeyDown = async (demon: Demon, fieldName: string, e: React.KeyboardEvent<HTMLInputElement>) => {
        if (e && e.key !== "Enter") return;

        await updateDemon(demon, fieldName);
        setEditing({id: null, field: null});
    }

    async function rearrangeDemonlistRequest(id: number, target: number) {
        if (!accessToken) return;
        await updateDemonPosition(id, target, accessToken);
    }

    function rearrangeDemonlist(current: number, target: number) {
        setDemons((prevDemons) => {
            const newDemons = [...prevDemons.map(d => ({...d}))];
            const demon = newDemons.find(d => d.placement === current);
            if (!demon) return prevDemons;

            newDemons.sort((a, b) => a.placement - b.placement);

            newDemons.splice(current - 1, 1);
            newDemons.splice(target - 1, 0, demon);

            newDemons.forEach((d, index) => d.placement = index + 1);

            return newDemons;
        });
    }

    function deleteDemonLocally(targetDemon: Demon) {
        const targetId = targetDemon.id;
        const targetPlacement = targetDemon.placement;

        setDemons((prevDemons) => {
            return prevDemons
                .filter(demon => demon.id !== targetId)
                .map(demon => ({
                    ...demon,
                    placement: demon.placement > targetPlacement ? demon.placement - 1 : demon.placement
                }));
        })
    }

    const sortDemons = (field: keyof Demon) => {
        const newOrder = sortField === field && sortOrder === "asc" ? "desc" : "asc";

        setSortField(field);
        setSortOrder(newOrder);

        setDemons(prev => {
            return [...prev].sort((a, b) => {
                const aVal = a[field];
                const bVal = b[field];

                if (aVal == null) return 1;
                if (bVal == null) return -1;

                if (typeof aVal === "number" && typeof bVal === "number") {
                    return newOrder === "asc" ? aVal - bVal : bVal - aVal;
                }

                return newOrder === "asc" ? String(aVal).localeCompare(String(bVal)) :
                    String(bVal).localeCompare(String(aVal));
            });
        })
    }

    return (
        <table>
            <tbody>
            <tr key="names">
                <td>X</td>
                <td onClick={() => sortDemons("placement")}>#</td>
                <td onClick={() => sortDemons("name")}>Name</td>
                <td onClick={() => sortDemons("author")}>Author</td>
                <td onClick={() => sortDemons("difficulty")}>Difficulty</td>
                <td onClick={() => sortDemons("attemptsCount")}>Attempts</td>
                <td onClick={() => sortDemons("worstFail")}>Worst fail</td>
                <td onClick={() => sortDemons("enjoymentRating")}>Enjoyment</td>
                <td onClick={() => sortDemons("dateOfCompletion")}>Completed at</td>
                <td onClick={() => sortDemons("gddlTier")}>GDDL</td>
                <td onClick={() => sortDemons("aredlPlacement")}>AREDL</td>
            </tr>
            {demons.map((demon, index) => (
                <DemonRow
                    demonPlacement={demon.placement - 1}
                    demons={demons}
                    handleDoubleClick={handleDoubleClick}
                    handleChange={handleChange}
                    handleSelectChange={handleSelectDifficultyChange}
                    handleBlur={handleBlur}
                    handleKeyDown={handleKeyDown}
                    editing={editing}
                    data={data}
                    rearrangeDemonlistRequest={rearrangeDemonlistRequest}
                    rearrangeDemonlist={rearrangeDemonlist}
                    deleteDemonLocally={deleteDemonLocally}
                    handleUpdateCompletionDate={handleUpdateCompletionDate}
                    index={index}
                />
            ))}
            </tbody>
        </table>
    );
}

export default ListOfDemons;
