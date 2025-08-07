import React, {ReactNode} from "react";
import styles from "@/styles/Layout.module.css";
import Head from "next/head";
import Link from "next/link";
import DemonlistListManager from "@/components/DemonlistListManager";
import {useAuthContext} from "@/context/AuthContext";

interface LayoutProps {
    children: ReactNode;
}

const Layout: React.FC<LayoutProps> = ({children}) => {
    const {user, accessToken} = useAuthContext();
    const isAuthenticated = user !== null;
    if (isAuthenticated) {
        console.log(JSON.stringify(user));
    }

    console.log("=== Layout Debug ===");
    console.log("children:", children);
    console.log("==================");

    return (
        <div>
            <Head>
                <title>Page example</title>
                <meta name="description" content="Custom Demonlist"/>
                <meta name="viewport" content="width=device-width, initial-scale=1"/>
            </Head>
            <nav className={styles.nav}>
                {
                    isAuthenticated ? (
                        <Link href="/profile">Profile</Link>
                    ) : (
                        <Link href="/account">Sign up</Link>)
                }
                {accessToken && isAuthenticated && <DemonlistListManager/>}
            </nav>
            <main>
                {children}
            </main>
        </div>
    );
};

export default Layout;
