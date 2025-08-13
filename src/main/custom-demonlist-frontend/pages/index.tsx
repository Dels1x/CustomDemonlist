import React from "react";
import Layout from "@/layout/Layout";
import {
    extractFromAccessToken,
    getAccessTokenAndRefreshToken,
} from "@/api/auth";
import {useAuthContext} from "@/context/AuthContext";
import styles from "@/styles/Home.module.css";
import Link from "next/link";

interface HomeProps {
    user: any;
    accessToken: string | null;
}

const Home: React.FC<HomeProps> = ({ user: serverUser }) => {
    const { user } = useAuthContext();
    const currentUser = user || serverUser;
    const isAuthenticated = !!currentUser;

    return (
        <Layout>
            <div className={styles.container}>
                <div className={styles.backgroundShapes}></div>
                <div className={styles.neonGrid}></div>
                <div className={`${styles.floatingOrb} ${styles.floatingOrb1}`}></div>
                <div className={`${styles.floatingOrb} ${styles.floatingOrb2}`}></div>
                <div className={`${styles.floatingOrb} ${styles.floatingOrb3}`}></div>
                {/* Main Hero Section */}
                <div className={styles.heroSection}>
                    <h1 className={styles.heroTitle}>
                        Welcome to Custom Demonlist
                    </h1>
                    <p className={styles.heroSubtitle}>
                        Create and manage your personalized Geometry Dash demonlists.
                        Track your progress, organize your completions, and share your achievements
                        with the community.
                    </p>

                    {/* Features Section */}
                    <div className={styles.featuresGrid}>
                        <div className={styles.featureCard}>
                            <svg className={styles.featureIcon} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                                <ellipse cx="12" cy="5" rx="9" ry="3"/>
                                <path d="M21 12c0 1.66-4 3-9 3s-9-1.34-9-3"/>
                                <path d="M3 5v14c0 1.66 4 3 9 3s9-1.34 9-3V5"/>
                            </svg>
                            <h3 className={styles.featureTitle}>Custom Lists</h3>
                            <p className={styles.featureDescription}>
                                Create unlimited demonlists with your own ranking system and difficulty ratings.
                            </p>
                        </div>

                        <div className={styles.featureCard}>
                            <svg className={styles.featureIcon} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                                <path d="M9 12l2 2 4-4"/>
                                <path d="M21 12c.552 0 1-.448 1-1V8c0-.552-.448-1-1-1s-1 .448-1 1v3c0 .552.448 1 1 1z"/>
                                <path d="M3 12c-.552 0-1-.448-1-1V8c0-.552.448-1 1-1s1 .448 1 1v3c0 .552-.448 1-1 1z"/>
                                <path d="M12 21c.552 0 1-.448 1-1v-3c0-.552-.448-1-1-1s-1 .448-1 1v3c0 .552.448 1 1 1z"/>
                                <path d="M12 3c-.552 0-1 .448-1 1v3c0 .552.448 1 1 1s1-.448 1-1V4c0-.552-.448-1-1-1z"/>
                            </svg>
                            <h3 className={styles.featureTitle}>Track Progress</h3>
                            <p className={styles.featureDescription}>
                                Monitor your completion stats, attempts, worst fails, and enjoyment ratings.
                            </p>
                        </div>

                        <div className={styles.featureCard}>
                            <svg className={styles.featureIcon} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                                <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
                                <circle cx="9" cy="7" r="4"/>
                                <path d="M23 21v-2a4 4 0 0 0-3-3.87"/>
                                <path d="M16 3.13a4 4 0 0 1 0 7.75"/>
                            </svg>
                            <h3 className={styles.featureTitle}>Share & Discover</h3>
                            <p className={styles.featureDescription}>
                                Make your lists public to share with others or keep them private for personal use.
                            </p>
                        </div>
                    </div>

                    {/* Action Buttons */}
                    <div className={styles.heroActions}>
                        {!isAuthenticated ? (
                            <Link href="/account" className={styles.heroButton}>
                                Get Started
                            </Link>
                        ) : (
                            <Link href="/profile" className={styles.heroButton}>
                                Go to Your Profile
                            </Link>
                        )}
                        <a
                            href="https://discord.gg/yourdiscordserver"
                            target="_blank"
                            rel="noopener noreferrer"
                            className={styles.discordButton}
                        >
                            <svg className={styles.discordIcon} viewBox="0 0 24 24" fill="currentColor">
                                <path d="M20.317 4.37a19.791 19.791 0 0 0-4.885-1.515.074.074 0 0 0-.079.037c-.21.375-.444.864-.608 1.25a18.27 18.27 0 0 0-5.487 0 12.64 12.64 0 0 0-.617-1.25.077.077 0 0 0-.079-.037A19.736 19.736 0 0 0 3.677 4.37a.07.07 0 0 0-.032.027C.533 9.046-.32 13.58.099 18.057a.082.082 0 0 0 .031.057 19.9 19.9 0 0 0 5.993 3.03.078.078 0 0 0 .084-.028c.462-.63.874-1.295 1.226-1.994a.076.076 0 0 0-.041-.106 13.107 13.107 0 0 1-1.872-.892.077.077 0 0 1-.008-.128 10.2 10.2 0 0 0 .372-.292.074.074 0 0 1 .077-.01c3.928 1.793 8.18 1.793 12.062 0a.074.074 0 0 1 .078.01c.12.098.246.197.373.292a.077.077 0 0 1-.006.127 12.299 12.299 0 0 1-1.873.892.077.077 0 0 0-.041.107c.36.698.772 1.362 1.225 1.993a.076.076 0 0 0 .084.028 19.839 19.839 0 0 0 6.002-3.03.077.077 0 0 0 .032-.054c.5-5.177-.838-9.674-3.549-13.66a.061.061 0 0 0-.031-.03zM8.02 15.33c-1.183 0-2.157-1.085-2.157-2.419 0-1.333.956-2.419 2.157-2.419 1.21 0 2.176 1.096 2.157 2.42 0 1.333-.956 2.418-2.157 2.418zm7.975 0c-1.183 0-2.157-1.085-2.157-2.419 0-1.333.955-2.419 2.157-2.419 1.21 0 2.176 1.096 2.157 2.42 0 1.333-.946 2.418-2.157 2.418z"/>
                            </svg>
                            Join Discord
                        </a>
                    </div>

                    {/* User Welcome (if authenticated) */}
                    {isAuthenticated && (
                        <div className={styles.userWelcome}>
                            <p className={styles.welcomeText}>
                                Welcome back, <span className={styles.username}>{currentUser.username}</span>!
                            </p>
                        </div>
                    )}
                </div>
            </div>
        </Layout>
    );
};

export async function getServerSideProps(context: any) {
    const accessToken = await getAccessTokenAndRefreshToken(context);
    const user = accessToken ? extractFromAccessToken(accessToken) : null;

    return {
        props: {
            user,
            accessToken: accessToken || null,
        },
    }
}

export default Home;
