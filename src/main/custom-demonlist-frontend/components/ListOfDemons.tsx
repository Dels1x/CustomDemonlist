import {Demon} from "@/lib/models";
import React, {useState} from "react";
import {
    updateDemonAttempts,
    updateDemonAuthor, updateDemonDifficulty,
    updateDemonEnjoyment,
    updateDemonName,
    updateDemonPosition
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

    const handleDoubleClick = (demon: Demon, fieldName: string) => {
        let tempData = demon[fieldName as keyof Demon];
        if (!tempData) tempData = '';

        setData(String(tempData));
        setEditing({id: demon.id, field: fieldName})
    }

    const updateDemon = async (demon: Demon, fieldName: string, newValue?: string) => {
        const valueToUse = newValue ?? data;
        console.log("valueToUse: ", valueToUse);

        switch (fieldName) {
            case "name":
                await updateDemonName(demon.id, valueToUse, accessToken);
                demon.name = valueToUse;
                break;
            case "author":
                await updateDemonAuthor(demon.id, valueToUse, accessToken);
                demon.author = valueToUse;
                break;
            case "attemptsCount":
                if (valueToUse === '') {
                    break;
                }

                await updateDemonAttempts(demon.id, valueToUse, accessToken);
                demon.attemptsCount = Number(valueToUse);
                break;
            case "enjoymentRating":
                if (valueToUse === '') {
                    break;
                }

                await updateDemonEnjoyment(demon.id, valueToUse, accessToken);
                demon.enjoymentRating = Number(valueToUse);
                break;
            case "difficulty":
                if (valueToUse === '') {
                    break;
                }

                demon.difficulty = valueToUse;
                await updateDemonDifficulty(demon.id, valueToUse, accessToken);
                break;
            default:
                console.error("Unknown field: ", fieldName);
        }
    }

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const value = e.currentTarget.value;

        if (editing.field === "attemptsCount" || editing.field === "enjoymentRating") {
            if (/^\d*$/.test(value)) {
                const numValue = Number(value);

                if (numValue < MAX_INT) {
                    setData(value);
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

    return (
        <table>
            <tbody>
            <tr key="names">
                <td>X</td>
                <td>#</td>
                <td>Name</td>
                <td>Author</td>
                <td>Difficulty</td>
                <td>Attempts</td>
                <td>Enjoyment</td>
                <td>Completed at</td>
            </tr>
            {demons.map((demon) => (
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
                />
            ))}
            </tbody>
        </table>
    );
}

export default ListOfDemons;
