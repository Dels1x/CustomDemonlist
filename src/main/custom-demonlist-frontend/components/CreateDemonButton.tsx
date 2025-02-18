import Image from "next/image";
import React from "react";
import {countDemonsInDemonlist, createNewDemon} from "@/api/api";
import {ListItem} from "@/components/ListOfDemonlists";

interface CreateDemonlistButtonProps {
    demonlistId: string;
    accessToken: string;
    onDemonlistCreated: (demonlist: ListItem) => void;
}

const CreateDemonlistButton: React.FC<CreateDemonlistButtonProps> = ({demonlistId, accessToken, onDemonlistCreated}) => {
    const handleClick = async () => {
        const count = countDemonsInDemonlist(demonlistId, accessToken)
        const demon = {
            name: "Demon #" + (Number(count) + 1),
            author: "Author",
        };

        await createNewDemon(demon, demonlistId, accessToken);
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
