import React from "react";
import {getDemonlist} from "@/api/api";
import {
    extractFromAccessToken,
    getAccessTokenAndRefreshToken,
} from "@/api/auth";
import Layout from "@/layout/Layout";

interface DemonlistProps {
    demonlist: any;
    user: any;
    accessToken: any;
}

const DemonlistPage: React.FC<DemonlistProps> = ({demonlist, user, accessToken}) => {
    console.log("Demonlist: " + JSON.stringify(demonlist));

    return (
        <Layout user={user} accessToken={accessToken}>
            <main>
                {`#${demonlist.id} - ${demonlist.name}`}
            </main>
        </Layout>
    );
}

export async function getServerSideProps(context: any) {
    const accessToken = await getAccessTokenAndRefreshToken(context);
    const user = accessToken ? extractFromAccessToken(accessToken) : null;
    const id = context.params!.id;

    try {
        const demonlist = await getDemonlist(id, accessToken);
        console.log("Demonlist: " + JSON.stringify(demonlist));

        return {
            props: {
                demonlist,
                user,
                accessToken,
            }
        }

    } catch (error) {
        throw  error;
    }
}

export default DemonlistPage;
