import "@/styles/globals.css";
import type {AppProps} from "next/app";
import {AuthProvider} from "@/context/AuthContext";
import {DemonlistProvider} from "@/context/DemonlistContext";
import {DndProvider} from "react-dnd";
import {HTML5Backend} from "react-dnd-html5-backend";

export default function App({Component, pageProps}: AppProps) {
    const isAuthenticated = pageProps.user !== undefined;

    return (
        <AuthProvider accessToken={pageProps.accessToken} user={pageProps.user}>
            {isAuthenticated ? (
                <DemonlistProvider accessToken={pageProps.accessToken} userId={pageProps.user.sub}>
                    <DndProvider backend={HTML5Backend}>
                        <Component {...pageProps} />
                    </DndProvider>
                </DemonlistProvider>
            ) : (
                <Component {...pageProps} />
            )}
        </AuthProvider>
    );
}
