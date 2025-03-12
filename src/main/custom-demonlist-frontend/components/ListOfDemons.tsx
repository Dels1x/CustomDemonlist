import {Demon} from "@/lib/models";

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
                    <td>{demon.name}</td>
                </tr>
            ))}
            </tbody>
        </table>
    );
}

export default ListOfDemons;