import React from "react";
import CreateDemonButton from "@/components/CreateDemonButton";
import ListOfDemons from "@/components/ListOfDemons";
import {Demon, Demonlist} from "@/lib/models";

interface DemonlistManagerProps {
    accessToken: string;
    demonlist: Demonlist;
}

// TODO implement method with updating demonlist state
const DemonlistManager: React.FC<DemonlistManagerProps> = ({accessToken, demonlist}) => {
    const addDemonToState = (newDemon: Demon) => {

    }

    return (
        <div>
            <ListOfDemons demonlist={demonlist} />
            <CreateDemonButton demonlistId={demonlist.id} accessToken={accessToken} onDemonCreated={addDemonToState} />
        </div>
    )
}

export default DemonlistManager;
