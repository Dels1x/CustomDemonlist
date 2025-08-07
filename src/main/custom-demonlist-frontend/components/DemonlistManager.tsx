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
    console.log("=== DemonlistManager Debug ===");
    console.log("accessToken:", accessToken);
    console.log("demonlist:", demonlist);
    console.log("isEditable:", isEditable);
    console.log("demonlist.demons:", demonlist?.demons);
    console.log("demonlist.demons length:", demonlist?.demons?.length);
    console.log("============================");

    if (!demonlist || !demonlist.id) {
        console.error("DemonlistManager: Demonlist ID is missing or invalid");
        return <div>Error: Invalid demonlist</div>;
    }

    const [demons, setDemons] = useState<Demon[]>(demonlist.demons || []);
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
