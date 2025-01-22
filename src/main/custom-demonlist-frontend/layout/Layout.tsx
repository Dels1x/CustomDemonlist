import React, {ReactNode} from "react";
import styles from "@/styles/Layout.module.css";
import Head from "next/head";
import Link from "next/link";
import ListOfDemonlists from "@/components/ListOfDemonlists";
import {AuthTokenPayload} from "@/api/auth";
import AddDemonlistButton from "@/components/AddDemonlistButton";

interface LayoutProps {
    children: ReactNode;
    user?: AuthTokenPayload
    accessToken?: string
    list?: any;
}

const Layout: React.FC<LayoutProps> = ({children, user, accessToken, list}) => {
    const isAuthenticated = user !== undefined;
    if (isAuthenticated) {
        console.log(JSON.stringify(user));
    }

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
                <AddDemonlistButton/>
                {
                    isAuthenticated ? (
                        <ListOfDemonlists list={list}/>
                    ) : null
                }
            </nav>
            <main>
                {children}
            </main>
        </div>
    );
};

export default Layout;
