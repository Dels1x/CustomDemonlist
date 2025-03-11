import Image from "next/image";
import React from "react";
import {countDemonlistsByUser, createNewDemonlist} from "@/api/api";
import {Demonlist} from "@/lib/models";

interface CreateDemonlistButtonProps {
    userId: string;
    accessToken: string;
    onDemonlistCreated: (demonlist: Demonlist) => void;
}

const CreateDemonlistButton: React.FC<CreateDemonlistButtonProps> = ({userId, accessToken, onDemonlistCreated}) => {
    const handleClick = async () => {
        const demonlistCount = await countDemonlistsByUser(userId, accessToken);

        const demonlist: Demonlist = {
            name: "Demonlist #" + (Number(demonlistCount) + 1),
            isPublic: true,
            isMulti: false
        };

        await createNewDemonlist(demonlist, accessToken);
        onDemonlistCreated(demonlist);
    };

    return (
        <div>
            <button onClick={handleClick}>
                <Image
                    src={"/addplus.svg"}
                    alt={"Create"}
                    width={25}
                    height={25}/>
                New Demonlist
            </button>
        </div>
    )
}

export default CreateDemonlistButton;
