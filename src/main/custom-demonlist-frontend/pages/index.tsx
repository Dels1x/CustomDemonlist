import React from "react";
import Layout from "@/layout/Layout";
import {
    extractFromAccessToken,
    getAccessTokenAndRefreshToken,
} from "@/api/auth";
import {useAuthContext} from "@/context/AuthContext";


const Home = () => {
    const {user} = useAuthContext();

    return (
        <Layout>
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
