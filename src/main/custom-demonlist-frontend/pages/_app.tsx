import "@/styles/globals.css";
import type {AppProps} from "next/app";
import {AuthProvider} from "@/context/AuthContext";
import {DemonlistProvider} from "@/context/DemonlistContext";
import {DndProvider} from "react-dnd";
import {HTML5Backend} from "react-dnd-html5-backend";

export default function App({Component, pageProps}: AppProps) {
    const isAuthenticated = pageProps.user !== undefined && pageProps.user !== null;
    console.log("pageProps.user: ", pageProps.user);
    console.log("isAuthenticated: ", isAuthenticated);

    return (
        <AuthProvider accessToken={pageProps.accessToken} user={pageProps.user}>
            <DndProvider backend={HTML5Backend}>
                {isAuthenticated ? (
                    <DemonlistProvider accessToken={pageProps.accessToken} userId={pageProps.user.sub}>
                        <Component {...pageProps} />
                    </DemonlistProvider>
                ) : (
                    <Component {...pageProps} />
                )}
            </DndProvider>
        </AuthProvider>
    );
}