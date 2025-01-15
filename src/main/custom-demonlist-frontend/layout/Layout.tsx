import React, {ReactNode} from "react";
import styles from "@/styles/Layout.module.css";
import Head from "next/head";
import Link from "next/link";

interface LayoutProps {
    children: ReactNode;
}

const Layout: React.FC<LayoutProps> = ({children}) => {
    return (
        <div>
            <Head>
                <title>Page example</title>
                <meta name="description" content="Custom Demonlist"/>
                <meta name="viewport" content="width=device-width, initial-scale=1"/>
            </Head>
            <nav className={styles.nav}>
                <Link href="/account">
                    Sign up
                </Link>
            </nav>
            <main>
                {children}
            </main>
        </div>
    );
};

export default Layout;
