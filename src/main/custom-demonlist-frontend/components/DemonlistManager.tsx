import React from "react";
import CreateDemonButton from "@/components/CreateDemonButton";

interface DemonlistManagerProps {
    accessToken: string;
    demonlist: any;
}

// TODO implement method with updating demonlist state
const DemonlistManager: React.FC<DemonlistManagerProps> = ({accessToken, demonlist}) => {
    return (
        <div>
            <CreateDemonButton demonlistId={demonlist.id} accessToken={accessToken} onDemonlistCreated={} />
        </div>
    )
}

export default DemonlistManager;
