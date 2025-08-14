import React from "react";
import Link from "next/link";
import {useDemonlistContext} from "@/context/DemonlistContext";
import styles from "@/styles/ListOfDemonlists.module.css";
import {useRouter} from "next/router";
import CreateDemonlistButton from "@/components/CreateDemonlistButton";
import {Demonlist} from "@/lib/models";

const ListOfDemonlists = () => {
    const {demonlists, refreshDemonlists} = useDemonlistContext();
    const router = useRouter();
    const currentId = router.query.id;

    if (!demonlists || demonlists.length === 0) {
        return (
            <div className={styles.emptyState}>
                <svg className={styles.emptyStateIcon} viewBox="0 0 24 24" fill="none" stroke="currentColor"
                     strokeWidth="2">
                    <path
                        d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"/>
                </svg>
                <div className={styles.emptyStateText}>No lists yet</div>
                <div className={styles.emptyStateSubtext}>Create your first demonlist</div>
            </div>
        );
    }

    const handleDemonlistCreated = () => {
        refreshDemonlists();
    };

    return (
        <div className={styles.demonlistsContainer}>
            {demonlists.map((item) => (
                <Link key={item.id} href={`/demonlists/${item.id}`} className={styles.demonlistLink}>
                    <div className={`${styles.demonlistItem} ${currentId === String(item.id) ? styles.active : ''}`}>
                        <svg className={styles.listIcon} viewBox="0 0 24 24" fill="none" stroke="currentColor"
                             strokeWidth="2">
                            {item.isMulti ? (
                                // Icon for multi-player lists
                                <g>
                                    <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
                                    <circle cx="9" cy="7" r="4"/>
                                    <path d="M23 21v-2a4 4 0 0 0-3-3.87"/>
                                    <path d="M16 3.13a4 4 0 0 1 0 7.75"/>
                                </g>
                            ) : item.isPublic ? (
                                // Icon for public lists
                                <g>
                                    <circle cx="12" cy="12" r="10"/>
                                    <path d="M2 12h20"/>
                                    <path
                                        d="M12 2a15.3 15.3 0 0 1 4 10 15.3 15.3 0 0 1-4 10 15.3 15.3 0 0 1-4-10 15.3 15.3 0 0 1 4-10z"/>
                                </g>
                            ) : (
                                // Icon for private lists
                                <g>
                                    <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                                    <path d="M7 11V7a5 5 0 0110 0v4"/>
                                </g>
                            )}
                        </svg>
                        <span className={styles.listName}>{item.name}</span>
                        {item.demons && item.demons.length > 0 && (
                            <span className={styles.listStats}>{item.demons.length}</span>
                        )}
                    </div>
                </Link>
            ))}
            <div className={styles.sidebarButtonWrapper}>
                <CreateDemonlistButton onDemonlistCreated={handleDemonlistCreated} variant="sidebar"/>
            </div>
        </div>
    );
};

export default ListOfDemonlists;
