import React from "react";
import Layout from "@/layout/Layout";
import {AuthTokenPayload, extractTokenData} from "@/api/auth";
import {getCookie, refreshToken} from "@/api/api";

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

    if (!user) {
        let token = getCookie("refresh-token", context.req);
        console.info("token: ", token);

        if (token != '') {
            let accessToken = await refreshToken(token);
            context.res.setHeader('Set-Cookie', `access-token=${accessToken}; HttpOnly; Path=/; Max-Age=3600; Secure`);
            user = extractTokenData(context.req);
        }
    }

    return {
        props: {
            user,
        },
    }
}

export default Home;
