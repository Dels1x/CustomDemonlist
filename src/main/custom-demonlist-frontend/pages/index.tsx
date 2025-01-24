import React from "react";
import Layout from "@/layout/Layout";
import {AuthTokenPayload, getAccessToken, getUserAndRefreshToken} from "@/api/auth";

interface HomeProps {
    user: AuthTokenPayload;
    list: any;
    accessToken: string;
}

const Home: React.FC<HomeProps> = ({user, accessToken}) => {
    return (
        <Layout user={user} accessToken={accessToken}>
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
    console.log("user: " + JSON.stringify(user));
    
    return {
        props: {
            user,
            accessToken,
        },
    }
}

export default Home;
