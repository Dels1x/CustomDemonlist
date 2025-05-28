import Image from "next/image";
import React from "react";
import {countDemonsInDemonlist, createNewDemon} from "@/api/api";
import {Demon} from "@/lib/models";

interface CreateDemonlistButtonProps {
    demonlistId: number;
    accessToken: string;
    onDemonCreated: (demonlist: Demon) => void;
}

const CreateDemonlistButton: React.FC<CreateDemonlistButtonProps> = ({demonlistId, accessToken, onDemonCreated}) => {
    const handleClick = async () => {
        const count: number = await countDemonsInDemonlist(demonlistId, accessToken)
        const demon = {
            name: "Demon #" + (count + 1),
            author: "Author",
        };

        const newDemon: Demon = await createNewDemon(demon, demonlistId, accessToken);
        console.log("newDemon: " + JSON.stringify(newDemon, null, 2));
        onDemonCreated(newDemon);
    };

    return (
        <div>
            <button onClick={handleClick} className='flex items-center gap-1.5'>
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
