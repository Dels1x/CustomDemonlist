import Image from "next/image";
import React from "react";
import {countDemonlistsByUser, createNewDemonlist} from "@/api/api";
import {ListItem} from "@/components/ListOfDemonlists";

interface CreateDemonlistButtonProps {
    userId: string;
    accessToken: string;
    onDemonlistCreated: (demonlist: ListItem) => void;
}

const CreateDemonlistButton: React.FC<CreateDemonlistButtonProps> = ({userId, accessToken, onDemonlistCreated}) => {
    const handleClick = async () => {
        const demonlistCount = await countDemonlistsByUser(userId, accessToken);

        const demonlist = {
            name: "Demonlist #" + (Number(demonlistCount) + 1),
            isPublic: true,
            isMulti: false,
            id: null,
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
                New Demon
            </button>
        </div>
    )
}

export default CreateDemonlistButton;
