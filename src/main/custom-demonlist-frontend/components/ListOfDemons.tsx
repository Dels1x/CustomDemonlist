import {Demon} from "@/lib/models";
import React, {useState} from "react";
import {
    updateDemonAttempts,
    updateDemonAuthor,
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

    const updateDemon = async (demon: Demon, fieldName: string) => {
        console.log("DATA: ", data);

        switch (fieldName) {
            case "name":
                await updateDemonName(demon.id, data, accessToken);
                demon.name = data;
                break;
            case "author":
                await updateDemonAuthor(demon.id, data, accessToken);
                demon.author = data;
                break;
            case "attemptsCount":
                if (data === '') {
                    break;
                }

                await updateDemonAttempts(demon.id, data, accessToken);
                demon.attemptsCount = Number(data);
                break;
            case "enjoymentRating":
                if (data === '') {
                    break;
                }

                await updateDemonEnjoyment(demon.id, data, accessToken);
                demon.enjoymentRating = Number(data);
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

    const handleBlur = async (demon: Demon, fieldName: string)=> {
        await updateDemon(demon, fieldName);
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

    async function rearrangeDemonlist(current: number, target: number) {
        setDemons((prevDemons) => {
            const newDemons = [...prevDemons.map(d => ({ ...d }))];
            const demon = newDemons.find(d => d.placement === current);
            if (!demon) return prevDemons;

            newDemons.sort((a, b) => a.placement - b.placement);

            newDemons.splice(current - 1, 1);
            newDemons.splice(target - 1, 0, demon);

            newDemons.forEach((d, index) => d.placement = index + 1);

            return newDemons;
        });
    }

    return (
        <table>
            <tbody>
            <tr key="names">
                <td>#</td>
                <td>Name</td>
                <td>Author</td>
                <td>Attempts</td>
                <td>Enjoyment</td>
            </tr>
            {demons.map((demon) => (
                <DemonRow
                    demonId={demon.placement - 1}
                    demons={demons}
                    handleDoubleClick={handleDoubleClick}
                    handleChange={handleChange}
                    handleBlur={handleBlur}
                    handleKeyDown={handleKeyDown}
                    editing={editing}
                    data={data}
                    rearrangeDemonlistRequest={rearrangeDemonlistRequest}
                    rearrangeDemonlist={rearrangeDemonlist}
                />
            ))}
            </tbody>
        </table>
    );
}

export default ListOfDemons;
