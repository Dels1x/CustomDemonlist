import {Demon} from "@/lib/models";
import React, {useState} from "react";
import {updateDemonAttempts, updateDemonAuthor, updateDemonEnjoyment, updateDemonName} from "@/api/api";
import {useAuthContext} from "@/context/AuthContext";

interface DemonlistProps {
    demons: Demon[];
}

const ListOfDemons: React.FC<DemonlistProps> = ({demons}) => {
    const {accessToken} = useAuthContext()

    if (!accessToken) return;

    const [editing, setEditing] = useState<{ id: number | null, field: string | null }>({
        id: null,
        field: null
    });
    const [data, setData] = useState<string>('');

    const handleDoubleClick = (id: number, fieldName: string) => {
        setEditing({id: id, field: fieldName})
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
                await updateDemonAttempts(demon.id, data, accessToken);
                demon.attemptsCount = Number(data);
                break;
            case "enjoymentRating":
                await updateDemonEnjoyment(demon.id, data, accessToken);
                demon.enjoymentRating = Number(data);
                break;
            default:
                console.error("Unknown field: ", fieldName);
        }
    }

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setData(e.currentTarget.value);
    }

    const handleBlur = async (demon: Demon, fieldName: string)=> {
        await updateDemon(demon, fieldName);
        setEditing({id: null, field: null});
    }

    return (
        <table>
            <tbody>
            <tr key="names">
                <td>#</td>
                <td>Name</td>
                <td>Author</td>
                <td>Difficulty</td>
                <td>Attempts</td>
                <td>Enjoyment</td>
            </tr>
            {demons.map((demon) => (
                <tr key={demon.id}>
                    {['placement', 'name', 'author', 'difficulty', 'attemptsCount', 'enjoymentRating']
                        .map((fieldName) => (
                            <td
                                onDoubleClick={() => handleDoubleClick(demon.id, fieldName)}
                            >
                                {editing.id === demon.id && editing.field === fieldName ?
                                    (<input
                                        onChange={handleChange}
                                        onBlur={() => handleBlur(demon, fieldName)}
                                    />)
                                    :
                                    demon[fieldName as keyof Demon]}
                            </td>
                        ))}
                </tr>
            ))}
            </tbody>
        </table>
    );
}

export default ListOfDemons;