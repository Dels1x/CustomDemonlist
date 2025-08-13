import React, { useEffect, useState } from "react"; // ADDED: useEffect, useState
import { useAuthContext } from "@/context/AuthContext";
import Layout from "@/layout/Layout";
import { extractFromAccessToken, getAccessTokenAndRefreshToken } from "@/api/auth";
import styles from "@/styles/Profile.module.css";
import Link from "next/link";
import CreateDemonlistButton from "@/components/CreateDemonlistButton";
import {Demonlist, Person} from "@/lib/models";
import {getDemonlistsForUserId, getUser, getUserStats} from "@/api/api";
import {useDemonlistContext} from "@/context/DemonlistContext";

interface UserStats {
    totalDemonlists: number;
    totalDemons: number;
    publicLists: number;
    totalLikes: number;
}

interface ProfileProps {
    user: any;
    accessToken: string;
}

const Profile: React.FC<ProfileProps> = ({ user: serverUser, accessToken }) => {
    const { user } = useAuthContext();
    const { refreshDemonlists } = useDemonlistContext();

    const currentUser = user || serverUser;

    const [profileData, setProfileData] = useState<Person | null>(null);
    const [demonlists, setDemonlists] = useState<Demonlist[]>([]);
    const [stats, setStats] = useState<UserStats | null>(null);
    const [isLoading, setIsLoading] = useState<boolean>(true);

    console.log("==== PROFILE LOGS ====")
    console.log("USER: ", currentUser);
    console.log("PROFILE: ", profileData);

    useEffect(() => {
        const fetchUserData = async () => {
            if (!currentUser?.sub || !accessToken) return;

            setIsLoading(true);
            try {
                const [profile, lists, userStats] = await Promise.all([
                    getUser(currentUser.sub),
                    getDemonlistsForUserId(currentUser.sub, accessToken),
                    getUserStats(currentUser.sub)
                ]);

                setProfileData(profile);
                setDemonlists(lists);
                setStats(userStats);
            } catch (error) {
                console.error("Error fetching user data:", error);
            } finally {
                setIsLoading(false);
            }
        };

        fetchUserData();
    }, [currentUser?.sub, accessToken]);

    // Helper functions
    const formatDate = (dateString: string): string => {
        return new Date(dateString).toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });
    };

    const getInitials = (username: string): string => {
        return username ? username.charAt(0).toUpperCase() : 'U';
    };

    const handleDemonlistCreated = (newDemonlist: Demonlist): void => {
        setDemonlists(prev => [...prev, newDemonlist]);
        if (stats) {
            setStats(prev => prev ? {
                ...prev,
                totalDemonlists: prev.totalDemonlists + 1
            } : null);
        }

        refreshDemonlists();
    };

    return (
        <Layout>
            <div className={styles.profileContainer}>
                {/* Profile Header */}
                <div className={styles.profileHeader}>
                    <div className={styles.profileInfo}>
                        <div className={styles.profilePicture}>
                            {profileData?.pfpUrl ? (
                                <img
                                    src={profileData.pfpUrl}
                                    alt="Profile"
                                    className={styles.profilePictureImage}
                                />
                            ) : (
                                getInitials(profileData?.username || currentUser?.username || 'User')
                            )}
                        </div>

                        <div className={styles.profileDetails}>
                            <div className={styles.profileName}>
                                {profileData?.username || currentUser?.username || 'User'}
                                <span className={styles.userIdBadge}>#{profileData?.id || currentUser?.sub}</span>
                            </div>

                            {profileData?.createdAt && (
                                <div className={styles.joinDate}>
                                    <svg className={styles.calendarIcon} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                                        <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
                                        <line x1="16" y1="2" x2="16" y2="6"/>
                                        <line x1="8" y1="2" x2="8" y2="6"/>
                                        <line x1="3" y1="10" x2="21" y2="10"/>
                                    </svg>
                                    Joined {formatDate(profileData.createdAt)}
                                </div>
                            )}

                            {/* Stats */}
                            {stats && (
                                <div className={styles.profileStats}>
                                    <div className={styles.statItem}>
                                        <div className={styles.statValue}>{stats.totalDemonlists}</div>
                                        <div className={styles.statLabel}>Lists</div>
                                    </div>
                                    <div className={styles.statItem}>
                                        <div className={styles.statValue}>{stats.totalDemons}</div>
                                        <div className={styles.statLabel}>Demons</div>
                                    </div>
                                    <div className={styles.statItem}>
                                        <div className={styles.statValue}>{stats.totalLikes}</div>
                                        <div className={styles.statLabel}>Likes</div>
                                    </div>
                                    <div className={styles.statItem}>
                                        <div className={styles.statValue}>{stats.publicLists}</div>
                                        <div className={styles.statLabel}>Public</div>
                                    </div>
                                </div>
                            )}
                        </div>
                    </div>
                </div>

                {/* Demonlists Section */}
                <div className={styles.demonlistsSection}>
                    <div className={styles.sectionTitle}>
                        <svg className={styles.sectionIcon} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                            <ellipse cx="12" cy="5" rx="9" ry="3"/>
                            <path d="M21 12c0 1.66-4 3-9 3s-9-1.34-9-3"/>
                            <path d="M3 5v14c0 1.66 4 3 9 3s9-1.34 9-3V5"/>
                        </svg>
                        Your Demonlists
                    </div>

                    <CreateDemonlistButton onDemonlistCreated={handleDemonlistCreated} />

                    {isLoading ? (
                        <div className={styles.loading}>
                            <div className={styles.loadingSpinner}></div>
                            <div className={styles.loadingText}>Loading your demonlists...</div>
                        </div>
                    ) : demonlists.length > 0 ? (
                        <div className={styles.demonlistsGrid}>
                            {demonlists.map((demonlist: Demonlist) => (
                                <Link
                                    key={demonlist.id}
                                    href={`/demonlists/${demonlist.id}`}
                                    className={styles.demonlistCard}
                                >
                                    <div className={styles.demonlistHeader}>
                                        <span className={styles.demonlistId}>#{demonlist.id}</span>
                                        <span className={demonlist.isPublic ? styles.publicBadge : styles.privateBadge}>
                                            {demonlist.isPublic ? 'Public' : 'Private'}
                                        </span>
                                    </div>

                                    <div className={styles.demonlistName}>
                                        {demonlist.name}
                                    </div>

                                    <div className={styles.demonlistMeta}>
                                        <span className={styles.demonCount}>
                                            {demonlist.demons?.length || 0} demons
                                        </span>
                                        <span className={styles.createdDate}>
                                           {String(demonlist.createdAt)}
                                        </span>
                                    </div>
                                </Link>
                            ))}
                        </div>
                    ) : (
                        <div className={styles.emptyState}>
                            <svg className={styles.emptyIcon} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                                <circle cx="12" cy="12" r="10"/>
                                <path d="M12 6v6l4 2"/>
                            </svg>
                            <div className={styles.emptyTitle}>No demonlists yet</div>
                            <div className={styles.emptyDescription}>
                                Create your first demonlist to get started!<br/>
                                Track your demon completions and share them with the community.
                            </div>
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

    // Redirect to login if not authenticated
    if (!user || !accessToken) {
        return {
            redirect: {
                destination: '/account',
                permanent: false,
            },
        };
    }

    return {
        props: {
            user,
            accessToken: accessToken || null,
        },
    }
}

export default Profile;
