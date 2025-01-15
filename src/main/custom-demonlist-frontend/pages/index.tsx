import React from "react";
import Layout from "@/layout/Layout";
import {AuthTokenPayload, extractTokenData} from "@/api/auth";

interface HomeProps {
    user: AuthTokenPayload;
}

const Home: React.FC<HomeProps> = ({user}) => {
    return (
        <Layout>
            <main>
                {user ? "Welcome, " + user.username : ""}
            </main>
        </Layout>
    );
};

export async function getServerSideProps(context: any) {
    let user = extractTokenData(context.req);

    return {
        props: {
            user,
        },
    }
}

export default Home;
