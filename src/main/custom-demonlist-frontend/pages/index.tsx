import React from "react";
import Layout from "@/layout/Layout";
import {AuthTokenPayload, getAccessToken, getUserAndRefreshToken} from "@/api/auth";
import {getDemonlistsForUserId} from "@/api/api";

interface HomeProps {
    user: AuthTokenPayload;
    list: any;
}

const Home: React.FC<HomeProps> = ({user, list}) => {
    return (
        <Layout user={user} list={list}>
            <main>
                {user ? "Welcome, #" + user.sub + " - " + user.username : ""}
            </main>
        </Layout>
    );
};

export async function getServerSideProps(context: any) {
    const user = await getUserAndRefreshToken(context);
    const accessToken = getAccessToken(context.req);
    console.log("accessToken: ", JSON.stringify(accessToken));
    const list = user !== null && accessToken !== null ? await getDemonlistsForUserId(user.sub, accessToken) : null
    console.log("list of demonlists: ", JSON.stringify(list));

    console.log("user: " + JSON.stringify(user));
    return {
        props: {
            user,
            list,
        },
    }
}

export default Home;
