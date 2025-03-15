import "@/styles/globals.css";
import type {AppProps} from "next/app";
import {AuthProvider} from "@/context/AuthContext";
import {DemonlistProvider} from "@/context/DemonlistContext";

export default function App({Component, pageProps}: AppProps) {
    console.log("pageProps: ", pageProps.user.sub);

    return (
        <AuthProvider accessToken={pageProps.accessToken} user={pageProps.user}>
            <DemonlistProvider accessToken={pageProps.accessToken} userId={pageProps.user.sub}>
                <Component {...pageProps} />
            </DemonlistProvider>
        </AuthProvider>
    );
}
