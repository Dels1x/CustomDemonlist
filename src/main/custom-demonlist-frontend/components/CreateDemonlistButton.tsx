import Image from "next/image";
import React, {useState} from "react";
import {countDemonlistsByUser, createNewDemonlist} from "@/api/api";
import {Demonlist} from "@/lib/models";
import {useAuthContext} from "@/context/AuthContext";
import styles from "@/styles/CreateDemonlistButton.module.css";

interface CreateDemonlistButtonProps {
    onDemonlistCreated: (demonlist: Demonlist) => void;
    variant?: 'default' | 'sidebar';
}

const CreateDemonlistButton: React.FC<CreateDemonlistButtonProps> = ({
                                                                         onDemonlistCreated,
                                                                         variant = 'default'
                                                                     }) => {
    const {accessToken, user} = useAuthContext();

    const [isLoading, setIsLoading] = useState(false);
    const [isSuccess, setIsSuccess] = useState(false);

    if (!user || !accessToken) return null;

    const handleClick = async () => {
        if (isLoading) return;

        setIsLoading(true);

        try {
            const demonlistCount = await countDemonlistsByUser(user.sub, accessToken);

            const demonlist: Demonlist = {
                id: -1,
                personId: -1,
                name: "Demonlist #" + (Number(demonlistCount) + 1),
                isPublic: true,
                isMulti: false,
                createdAt: new Date(),
                demons: [],
            };

            await createNewDemonlist(demonlist, accessToken);
            onDemonlistCreated(demonlist);

            setIsSuccess(true);
            setTimeout(() => setIsSuccess(false), 600);
        } catch (error) {
            console.error("Error creating demonlist:", error);
        } finally {
            setIsLoading(false);
        }
    };

    const getButtonClass = () => {
        const baseClasses = [];

        switch (variant) {
            case 'sidebar':
                baseClasses.push(styles.sidebarButton);
                break;
            default:
                baseClasses.push(styles.createButton, styles.demonlist);
                break;
        }

        if (isLoading) baseClasses.push(styles.loading);
        if (isSuccess) baseClasses.push(styles.success);

        return baseClasses.join(' ');
    };

    const renderIcon = () => {
        if (variant === 'sidebar') {
            return (
                <svg className={styles.sidebarIcon} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <line x1="12" y1="5" x2="12" y2="19"/>
                    <line x1="5" y1="12" x2="19" y2="12"/>
                </svg>
            );
        }

        return (
            <Image
                className={styles.plusIcon}
                src={"/addplus.svg"}
                alt={"Create"}
                width={22}
                height={22}
            />
        );
    };

    const getButtonText = () => {
        if (variant === 'sidebar') {
            return isLoading ? 'Creating...' : 'Create New List';
        }
        return isLoading ? 'Creating...' : 'New Demonlist';
    };

    return (
        <div className={variant === 'sidebar' ? '' : styles.container}>
            <button
                onClick={handleClick}
                className={getButtonClass()}
                disabled={isLoading}
                title={isLoading ? "Creating your demonlist..." : "Create a new demonlist"}
            >
                {renderIcon()}
                <span className={variant === 'sidebar' ? styles.sidebarText : styles.buttonText}>
                    {getButtonText()}
                </span>
            </button>
        </div>
    );
};

export default CreateDemonlistButton;
