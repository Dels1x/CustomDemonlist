import {Demon} from "@/lib/models";
import React, {useState} from "react";
import {updateDemonAttempts, updateDemonAuthor, updateDemonEnjoyment, updateDemonName} from "@/api/api";
import {useAuthContext} from "@/context/AuthContext";
import {useDrag} from "react-dnd";
import DemonRow from "@/components/DemonRow";

interface DemonlistProps {
    demons: Demon[];
}

const MAX_INT = 2147483647;

const ListOfDemons: React.FC<DemonlistProps> = ({demons}) => {
    const {accessToken} = useAuthContext()

    if (!accessToken) return;

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

    // TODO dnd

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
                    demon={demon}
                    handleDoubleClick={handleDoubleClick}
                    handleChange={handleChange}
                    handleBlur={handleBlur}
                    handleKeyDown={handleKeyDown}
                    editing={editing}
                    data={data}
                />
            ))}
            </tbody>
        </table>
    );
}

export default ListOfDemons;
