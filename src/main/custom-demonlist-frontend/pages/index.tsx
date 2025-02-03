import React from "react";
import Layout from "@/layout/Layout";
import {
    AuthTokenPayload,
    extractFromAccessToken,
    getAccessTokenAndRefreshToken,
} from "@/api/auth";

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
    const accessToken = await getAccessTokenAndRefreshToken(context);
    const user = accessToken ? extractFromAccessToken(accessToken) : null;
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
