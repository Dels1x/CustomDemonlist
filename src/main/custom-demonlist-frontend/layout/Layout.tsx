import React, {ReactNode, useState} from "react";
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
    const [isMobileNavOpen, setIsMobileNavOpen] = useState(false); // ADDED: Mobile nav state

    if (isAuthenticated) {
        console.log(JSON.stringify(user));
    }

    console.log("=== Layout Debug ===");
    console.log("children:", children);
    console.log("==================");

    // ADDED: Toggle mobile nav
    const toggleMobileNav = () => {
        setIsMobileNavOpen(!isMobileNavOpen);
    };

    return (
        <div className={styles.container}> {/* CHANGED: Added container class */}
            <Head>
                <title>Custom Demonlist</title> {/* CHANGED: Better title */}
                <meta name="description" content="Create and manage your custom demon lists - The ultimate Geometry Dash community platform"/>
                <meta name="viewport" content="width=device-width, initial-scale=1"/>
                <link rel="icon" href="/favicon.ico" />
            </Head>

            {/* ADDED: Mobile navigation toggle */}
            <button
                className={styles.mobileNavToggle}
                onClick={toggleMobileNav}
                aria-label="Toggle navigation"
            >
                <svg className={styles.hamburgerIcon} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <line x1="3" y1="6" x2="21" y2="6"/>
                    <line x1="3" y1="12" x2="21" y2="12"/>
                    <line x1="3" y1="18" x2="21" y2="18"/>
                </svg>
            </button>

            {/* UPDATED: Enhanced navigation */}
            <nav className={`${styles.nav} ${isMobileNavOpen ? styles.open : ''}`}>
                {/* ADDED: Navigation header */}
                <div className={styles.navHeader}>
                    <Link href="/" className={styles.logo}>
                        DEMONLIST
                    </Link>
                    <div className={styles.tagline}>
                        Custom Demonlist yall
                    </div>
                </div>

                {/* ADDED: Navigation content */}
                <div className={styles.navContent}>
                    {/* ADDED: Main navigation section */}
                    <div className={styles.navSection}>
                        <div className={styles.navSectionTitle}>Navigation</div>
                        <div className={styles.navLinks}>
                            <Link href="/" className={styles.navLink}>
                                <svg className={styles.navIcon} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                                    <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>
                                    <polyline points="9,22 9,12 15,12 15,22"/>
                                </svg>
                                Home
                            </Link>

                            {isAuthenticated ? (
                                <Link href="/profile" className={styles.navLink}>
                                    <svg className={styles.navIcon} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                                        <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                                        <circle cx="12" cy="7" r="4"/>
                                    </svg>
                                    Profile
                                </Link>
                            ) : (
                                <Link href="/account" className={styles.navLink}>
                                    <svg className={styles.navIcon} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                                        <path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
                                        <circle cx="8.5" cy="7" r="4"/>
                                        <line x1="20" y1="8" x2="20" y2="14"/>
                                        <line x1="23" y1="11" x2="17" y2="11"/>
                                    </svg>
                                    Sign Up
                                </Link>
                            )}
                        </div>
                    </div>

                    {/* ADDED: Demonlist section for authenticated users */}
                    {accessToken && isAuthenticated && (
                        <div className={styles.navSection}>
                            <div className={styles.navSectionTitle}>Your Lists</div>
                            <DemonlistListManager/>
                        </div>
                    )}

                    {/* ADDED: User section at bottom */}
                    {isAuthenticated && user && (
                        <div className={styles.userSection}>
                            <div className={styles.userInfo}>
                                <div className={styles.userAvatar}>
                                    {user.username ? user.username.charAt(0).toUpperCase() : 'U'}
                                </div>
                                <div className={styles.userDetails}>
                                    <div className={styles.userName}>
                                        {user.username || 'User'}
                                    </div>
                                    <div className={styles.userStatus}>
                                        Online
                                    </div>
                                </div>
                            </div>
                        </div>
                    )}
                </div>
            </nav>

            {/* UPDATED: Main content area */}
            <main className={`${styles.mainContent} ${isMobileNavOpen ? styles.navOpen : ''}`}>
                {children}
            </main>
        </div>
    );
};

export default Layout;
