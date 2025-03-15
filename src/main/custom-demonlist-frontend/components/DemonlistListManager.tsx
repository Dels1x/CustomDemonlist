import CreateDemonlistButton from "@/components/CreateDemonlistButton";
import ListOfDemonlists from "@/components/ListOfDemonlists";
import {useAuthContext} from "@/context/AuthContext";
import {useDemonlistContext} from "@/context/DemonlistContext";

const DemonlistListManager= () => {
    const {accessToken, user} = useAuthContext();
    const {refreshDemonlists} = useDemonlistContext();
    if (!user || !accessToken) return;

    return (
        <div>
            <CreateDemonlistButton onDemonlistCreated={refreshDemonlists}/>
            <ListOfDemonlists/>
        </div>
    );
}

export default DemonlistListManager;
