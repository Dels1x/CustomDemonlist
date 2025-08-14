import React, {useEffect} from "react";
import {deleteDemonlist, getDemonlist, updateDemonlistName, updateDemonlistVisibility} from "@/api/api";
import {
    extractFromAccessToken,
    getAccessTokenAndRefreshToken,
} from "@/api/auth";
import Layout from "@/layout/Layout";
import DemonlistManager from "@/components/DemonlistManager";
import {useOptionalDemonlistContext} from "@/context/DemonlistContext";
import DeleteButton from "@/components/DeleteButton";
import {useRouter} from "next/router";
import styles from "@/styles/Demonlist.module.css"
import VisibilityToggleButton from "@/components/VisibilityToggleButton";

interface DemonlistProps {
    demonlist: any;
    user: any;
    accessToken: any;
}

const DemonlistPage: React.FC<DemonlistProps> = ({demonlist, accessToken, user}) => {
    const demonlistContext = useOptionalDemonlistContext();
    const refreshDemonlists = demonlistContext?.refreshDemonlists || (() => Promise.resolve());

    const [isEditing, setEditing] = React.useState(false);
    const isEditable = user ? user.sub === String(demonlist.person.id) : false;
    const [name, setName] = React.useState(demonlist.name);
    const router = useRouter();

    console.log("=== DemonlistPage Debug ===");
    console.log("demonlist:", demonlist);
    console.log("accessToken:", accessToken);
    console.log("user:", user);
    console.log("========================");


    useEffect(() => {
        setName(demonlist.name);
        setEditing(false);
    }, [demonlist]);

    const doubleClick = () => {
        setEditing(true);
    }

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setName(e.target.value);
    }

    const handleBlur = async () => {
        await saveNameToDatabase();
        setEditing(false);
        refreshDemonlists();
    }

    const handleKeyDown = async (e: React.KeyboardEvent<HTMLInputElement>) => {
        if (e && e.key !== "Enter") return;

        await saveNameToDatabase();
        setEditing(false);
        refreshDemonlists();
    }

    const saveNameToDatabase = async () => {
        if (name !== demonlist.name) {
            await updateDemonlistName(demonlist.id, name, accessToken);
            demonlist.name = name;
        }
    }

    const handleDeleteDemonlist = async () => {
        if (isEditable) {
            await deleteDemonlist(demonlist.id, accessToken);
            refreshDemonlists();
            router.push("/");
        }
    }

    const handleVisibilityToggle = async (newVisibility: boolean) => {
        if (!isEditable || !accessToken) return;

        try {
            await updateDemonlistVisibility(demonlist.id, newVisibility, accessToken);
            demonlist.isPublic = newVisibility;
            await refreshDemonlists();
        } catch (error) {
            console.error('Failed to update visibility:', error);
        }
    }

    return (
        <Layout>
            <main className={styles.demonlist}>
                <div className={styles.header}>
                    <div className={styles.titleSection}>
                        <span className={styles.idBadge}>#{demonlist.id}</span>
                        {isEditing ? (
                            <input
                                className={styles.titleInput}
                                type="text"
                                autoFocus
                                value={name}
                                onChange={handleChange}
                                onBlur={handleBlur}
                                onKeyDown={handleKeyDown}
                            />
                        ) :(
                            <span
                                className={styles.titleText}
                                onDoubleClick={doubleClick}
                                title={isEditable ? "Double-click to edit" : undefined}
                            >
                                {name}
                            </span>
                        )}
                    </div>

                    <div className={styles.actionsSection}>
                        {isEditable && (
                            <>
                                <VisibilityToggleButton
                                    isPublic={demonlist.isPublic}
                                    onToggle={handleVisibilityToggle}
                                />
                                <DeleteButton
                                    onDelete={handleDeleteDemonlist}
                                    label={`Delete ${name}`}
                                    variant='wide'
                                />
                            </>
                        )}
                    </div>
                </div>

                <div className={styles.statusBar}>
                    <div className={styles.statusItem}>
                        <svg className={styles.statusIcon} viewBox="0 0 24 24" fill="none" stroke="currentColor"
                             strokeWidth="2">
                            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                            <circle cx="12" cy="7" r="4"/>
                        </svg>
                        <span>Created by <span className={styles.creatorName}>{demonlist.person.username}</span></span>
                    </div>

                    <div className={styles.statusItem}>
                        <svg className={styles.statusIcon} viewBox="0 0 24 24" fill="none" stroke="currentColor"
                             strokeWidth="2">
                            <ellipse cx="12" cy="5" rx="9" ry="3"/>
                            <path d="M21 12c0 1.66-4 3-9 3s-9-1.34-9-3"/>
                            <path d="M3 5v14c0 1.66 4 3 9 3s9-1.34 9-3V5"/>
                        </svg>
                        <span><span className={styles.demonCount}>{demonlist.demons?.length || 0}</span> demons registered</span>
                    </div>

                    <div className={styles.statusItem}>
                        {demonlist.isPublic ? (
                            <svg className={styles.statusIcon} viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                 strokeWidth="2">
                                <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                                <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
                            </svg>
                        ) : (
                            <svg className={styles.statusIcon} viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                 strokeWidth="2">
                                <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                                <path d="M7 11V7a5 5 0 0 1 5-5 5 5 0 0 1 5 5v4"/>
                                <line x1="12" y1="16" x2="12" y2="18"/>
                            </svg>
                        )}
                        <span className={demonlist.isPublic ? styles.publicBadge : styles.privateBadge}>
            {demonlist.isPublic ? 'Public Access' : 'Private Vault'}
        </span>
                    </div>
                </div>

                <div className={styles.content}>
                    <DemonlistManager
                        accessToken={accessToken}
                        demonlist={demonlist}
                        isEditable={isEditable}
                    />
                </div>
            </main>
        </Layout>
    );
}

export async function getServerSideProps(context: any) {
    const accessToken = await getAccessTokenAndRefreshToken(context);
    const user = accessToken ? extractFromAccessToken(accessToken) : null;
    const id = context.params!.id;

    const demonlist = await getDemonlist(id, accessToken);
    console.log("Demonlist: " + JSON.stringify(demonlist));

    if (!demonlist) {
        return {
            notFound: true,
        };
    }

    return {
        props: {
            demonlist,
            user,
            accessToken: accessToken || null,
        }
    }
}

export default DemonlistPage;
