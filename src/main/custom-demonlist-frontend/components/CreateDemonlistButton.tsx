import Image from "next/image";
import React from "react";
import {countDemonlistsByUser, createNewDemonlist} from "@/api/api";
import {Demonlist} from "@/lib/models";
import {useAuthContext} from "@/context/AuthContext";

interface CreateDemonlistButtonProps {
    onDemonlistCreated: (demonlist: Demonlist) => void;
}

const CreateDemonlistButton: React.FC<CreateDemonlistButtonProps> = ({onDemonlistCreated}) => {
    const {accessToken, user} = useAuthContext();
    if (!user || !accessToken) return;

    const handleClick = async () => {
        const demonlistCount = await countDemonlistsByUser(user.sub, accessToken);

        const demonlist: Demonlist = {
            id: -1, // placeholder,
            personId: -1, // placeholder
            name: "Demonlist #" + (Number(demonlistCount) + 1),
            isPublic: true,
            isMulti: false,
            demons: [],
        };

        await createNewDemonlist(demonlist, accessToken);
        onDemonlistCreated(demonlist);
    };

    return (
        <div>
            <button onClick={handleClick} className='flex items-center gap-1.5'>
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
