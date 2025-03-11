import CreateDemonlistButton from "@/components/CreateDemonlistButton";
import ListOfDemonlists from "@/components/ListOfDemonlists";
import {useEffect, useState} from "react";
import {getDemonlistsForUserId} from "@/api/api";
import {Demonlist} from "@/lib/models";

const DemonlistListManager: React.FC<{ userId: string; accessToken: string }> = ({userId, accessToken}) => {
    const [demonlists, setDemonlists] = useState<Demonlist[]>([]);

     useEffect(() => {
        const fetchDemonlists = async () => {
            try {
                const data = await getDemonlistsForUserId(userId, accessToken);
                setDemonlists(data);
            } catch (error) {
                console.error("Error fetching demonlists: ", error);
            }
        };
        fetchDemonlists();
    }, [userId, accessToken]);

    const addDemonlistToState = (newDemonlist: Demonlist) => {
        setDemonlists((prev) => [...prev, newDemonlist])
    }

    return (
        <div>
            <CreateDemonlistButton userId={userId} accessToken={accessToken} onDemonlistCreated={addDemonlistToState}/>
            <ListOfDemonlists list={demonlists}/>
        </div>
    );
}

export default DemonlistListManager;
