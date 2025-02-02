import React from "react";
import {getDemonlist} from "@/api/api";
import {getAccessToken} from "@/api/auth";

interface DemonlistProps {
    demonlist: any;
}

const DemonlistPage: React.FC<DemonlistProps> = ({demonlist}) => {
    console.log("Demonlist: " + JSON.stringify(demonlist));

    return (
        <div>
            {`#${demonlist.id} - ${demonlist.name}`}
        </div>
    );
}

export async function getServerSideProps(context: any) {
    const id = context.params!.id;
    const accessToken = getAccessToken(context.req);

    try {
        const demonlist = await getDemonlist(id, accessToken);
        console.log("Demonlist: " + JSON.stringify(demonlist));

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
