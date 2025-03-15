import CreateDemonlistButton from "@/components/CreateDemonlistButton";
import ListOfDemonlists from "@/components/ListOfDemonlists";
import {useEffect, useState} from "react";
import {getDemonlistsForUserId} from "@/api/api";
import {Demonlist} from "@/lib/models";
import {useAuthContext} from "@/context/AuthContext";

const DemonlistListManager= () => {
    const {accessToken, user} = useAuthContext();
    console.log(JSON.stringify(user, null, 2))
    if (!user || !accessToken) return;

    const [demonlists, setDemonlists] = useState<Demonlist[]>([]);

     useEffect(() => {
        const fetchDemonlists = async () => {
            try {
                    const data = await getDemonlistsForUserId(user.sub, accessToken);
                    setDemonlists(data);
            } catch (error) {
                console.error("Error fetching demonlists: ", error);
            }
        };
        fetchDemonlists();
    }, [user.sub, accessToken]);

    const addDemonlistToState = (newDemonlist: Demonlist) => {
        setDemonlists((prev) => [...prev, newDemonlist])
    }

    return (
        <div>
            <CreateDemonlistButton onDemonlistCreated={addDemonlistToState}/>
            <ListOfDemonlists list={demonlists}/>
        </div>
    );
}

export default DemonlistListManager;
