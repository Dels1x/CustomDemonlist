import React from "react";
import Layout from "@/layout/Layout";
import {AuthTokenPayload, getUserAndRefreshToken} from "@/api/auth";

interface HomeProps {
    user: AuthTokenPayload;
}

const Home: React.FC<HomeProps> = ({user}) => {
    return (
        <Layout>
            <main>
                {user ? "Welcome, #" + user.sub + " - " + user.username : ""}
            </main>
        </Layout>
    );
};

export async function getServerSideProps(context: any) {
    let user = await getUserAndRefreshToken(context);

    console.log("user: " + JSON.stringify(user));
    return {
        props: {
            user,
        },
    }
}

export default Home;
