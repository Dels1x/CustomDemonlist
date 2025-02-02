import React from "react";
import {getDemonlist} from "@/api/api";

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

    try {
        const demonlist = await getDemonlist(id);

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
