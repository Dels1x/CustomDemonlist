import React, {useEffect, useState} from "react";
import CreateDemonButton from "@/components/CreateDemonButton";
import ListOfDemons from "@/components/ListOfDemons";
import {Demon, Demonlist} from "@/lib/models";

interface DemonlistManagerProps {
    accessToken: string;
    demonlist: Demonlist;
    isEditable: boolean;
}

const DemonlistManager: React.FC<DemonlistManagerProps> = ({accessToken, demonlist, isEditable}) => {
    if (!demonlist.id) {
        console.error("Demonlist ID is missing or invalid");
        return;
    }

    const [demons, setDemons] = useState<Demon[]>([]);
    useEffect(() => {
        setDemons(demonlist.demons);
    }, [demonlist.demons]);

    const addDemonToState = (newDemon: Demon) => {
        setDemons((prev) => [...prev, newDemon]);
    }

    return (
        <div>
            <ListOfDemons
                demons={demons}
                setDemons={setDemons}
                isEditable={isEditable}
            />
            {isEditable && <CreateDemonButton
                demonlistId={demonlist.id}
                accessToken={accessToken}
                onDemonCreated={addDemonToState}
            />}
        </div>
    )
}

export default DemonlistManager;
