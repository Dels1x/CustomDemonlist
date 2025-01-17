import Layout from "@/layout/Layout";
import GoogleButton from "react-google-button";
import DiscordButton from "@/components/DiscordButton";
import React from "react";
import styles from "@/styles/Account.module.css";
import {extractTokenData} from "@/api/auth";
import {getCookie, refreshToken} from "@/api/api";

export default function Account() {
    const handleGoogleSignIn = () => {
        window.location.href = "https://accounts.google.com/o/oauth2/v2/auth/oauthchooseaccount?response_type=code&client_id=282635553570-0pf2544q5ksv6ajqns8qb8md775cfqn8.apps.googleusercontent.com&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Foauth2%2Fcallback%2Fgoogle&scope=openid%20email%20profile&state=random_state_string&access_type=offline&prompt=consent&service=lso&o2v=2&ddm=1&flowName=GeneralOAuthFlow";
    }

    return (
        <Layout>
            <div className={styles.authorizationBox}>
                <div>
                    Sign in / Create account
                </div>
                <GoogleButton
                    label='Sign in with Google'
                    onClick={handleGoogleSignIn}
                />
                <DiscordButton />
            </div>
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

    if (user) {
        return {
            redirect: {
                destination: "/",
                permanent: false,
            }
        }
    } else {
        return {
            props: {

            },
        };
    }
}
