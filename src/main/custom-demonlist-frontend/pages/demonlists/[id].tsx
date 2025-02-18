import React from "react";
import {getDemonlist} from "@/api/api";
import {
    extractFromAccessToken,
    getAccessTokenAndRefreshToken,
} from "@/api/auth";
import Layout from "@/layout/Layout";
import DemonlistManager from "@/components/DemonlistManager";

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
                <div>
                    {`#${demonlist.id} - ${demonlist.name}`}

                    <DemonlistManager accessToken={accessToken} demonlist={demonlist} />
                </div>
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
        throw error;
    }
}

export default DemonlistPage;
