import CreateDemonlistButton from "@/components/CreateDemonlistButton";
import ListOfDemonlists, {ListItem} from "@/components/ListOfDemonlists";
import {useState} from "react";

const DemonlistManager: React.FC<{ userId: string; accessToken: string }> = ({userId, accessToken}) => {
    const [demonlists, setDemonlists] = useState<ListItem[]>([]);

    const addDemonlistToState = (newDemonlist: ListItem) => {
        setDemonlists((prev) => [...prev, newDemonlist])
    }

    return (
        <div>
            <CreateDemonlistButton accessToken={accessToken} onDemonlistCreated={addDemonlistToState}/>
            <ListOfDemonlists list={demonlists}/>
        </div>
    );
}
