import {Demon} from "@/lib/models";
import React from "react";

interface DemonlistProps {
    demons: Demon[];
}

const ListOfDemons: React.FC<DemonlistProps> = ({demons}) => {
    console.log(demons);

    return (
        <table>
            <tbody>
            {demons.map((demon) =>  (
                <tr key={demon.id}>
                    <td>{demon.placement}.</td>
                    <td>{demon.name}</td>
                    <td>{demon.author}</td>
                    <td>{demon.difficulty}</td>
                    <td>{demon.attemptsCount}</td>
                    <td>{demon.enjoymentRating}</td>
                </tr>
            ))}
            </tbody>
        </table>
    );
}

export default ListOfDemons;