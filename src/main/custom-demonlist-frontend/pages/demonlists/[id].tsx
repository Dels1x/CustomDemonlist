import React from "react";
import {getDemonlist} from "@/api/api";
import {getAccessToken} from "@/api/auth";

interface Demon {
    id: number;
    name: string;
    difficulty: string;
    placement: number;
    initial_placement: number;
}

interface DemonlistProps {
    demonlist: Demon[];
    demonlistName: string;
    id: number;
    person_id: number;
    is_public: boolean;
    is_multi: boolean;
}

const DemonlistPage: React.FC<DemonlistProps> = ({demonlist, demonlistName, id, person_id, is_public, is_multi}) => {
    return (
        <div>
            {`#${id} - ${demonlistName}`}
        </div>
    );
}

export async function getServerSideProps(context: any) {
    const id = context.params!.id;
    const accessToken = getAccessToken(context.req);

    try {
        const demonlist = await getDemonlist(id, accessToken);

        return {
            props: {
                demonlist,
            }
        }

    } catch (error) {
        throw  error;
    }
}

export default DemonlistPage;
