import {Demonlist} from "@/lib/models";

interface DemonlistProps {
    demonlist: Demonlist;
}

const ListOfDemons: React.FC<DemonlistProps> = ({demonlist}) => {
    console.log(JSON.stringify(demonlist, null, 2));

    return (
        <table>
            <tbody>
            {demonlist.demons.map((demon) =>  (
                <tr key={demon.id}>
                    <td>{demon.name}</td>
                </tr>
            ))}
            </tbody>
        </table>
    );
}

export default ListOfDemons;