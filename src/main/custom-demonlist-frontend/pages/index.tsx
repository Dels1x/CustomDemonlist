import Head from "next/head";
import styles from "@/styles/Home.module.css";
import GoogleButton from "react-google-button";

export default function Home() {
  return (
    <>
      <Head>
        <title>Create Next App</title>
        <meta name="description" content="Custom Demonlist" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />
      </Head>

        <main>
            <a  target="_blank"
                href="https://accounts.google.com/o/oauth2/v2/auth/oauthchooseaccount?response_type=code&client_id=282635553570-0pf2544q5ksv6ajqns8qb8md775cfqn8.apps.googleusercontent.com&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Foauth2%2Fcallback%2Fgoogle&scope=openid%20email%20profile&state=random_state_string&access_type=offline&prompt=consent&service=lso&o2v=2&ddm=1&flowName=GeneralOAuthFlow">
                <GoogleButton
                    label='Sign in with Google'
                    onClick={() => { console.log('Google button clicked') }}
                />
            </a>
        </main>
    </>
  );
}
