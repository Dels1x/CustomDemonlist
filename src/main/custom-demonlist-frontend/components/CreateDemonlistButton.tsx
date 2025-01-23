import Image from "next/image";
import React from "react";
import {createNewDemonlist} from "@/api/api";
import {ListItem} from "@/components/ListOfDemonlists";

interface CreateDemonlistButtonProps {
    accessToken: string;
    onDemonlistCreated: (demonlist: ListItem) => void;
}

const CreateDemonlistButton: React.FC<CreateDemonlistButtonProps> = ({accessToken, onDemonlistCreated}) => {
     const handleClick = async () => {
        const demonlist = {
            name: "New Demonlist",
            isPublic: true,
            isMulti: false,
        };

         await createNewDemonlist(demonlist, accessToken);
    };

    return (
        <div>
            <button onClick={handleClick}>
                <Image
                    src={"addplus.svg"}
                    alt={"Create"}
                    width={25}
                    height={25}/>
                New Demonlist
            </button>
        </div>
    )
}

export default CreateDemonlistButton;
