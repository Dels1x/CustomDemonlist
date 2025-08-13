import Image from "next/image";
import React, {useState} from "react";
import {countDemonlistsByUser, createNewDemonlist} from "@/api/api";
import {Demonlist} from "@/lib/models";
import {useAuthContext} from "@/context/AuthContext";
import styles from "@/styles/CreateDemonlistButton.module.css";

interface CreateDemonlistButtonProps {
    onDemonlistCreated: (demonlist: Demonlist) => void;
}

const CreateDemonlistButton: React.FC<CreateDemonlistButtonProps> = ({onDemonlistCreated}) => {
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
                id: -1, // placeholder,
                personId: -1, // placeholder
                name: "Demonlist #" + (Number(demonlistCount) + 1),
                isPublic: true,
                isMulti: false,
                createdAt: new Date(), // placeholder
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

    const buttonClasses = [
        styles.createButton,
        styles.demonlist,
        isLoading ? styles.loading : '',
        isSuccess ? styles.success : ''
    ].filter(Boolean).join(' ');

    return (
        <div className={styles.container}> {}
            <button
                onClick={handleClick}
                className={buttonClasses}
                disabled={isLoading}
                title={isLoading ? "Creating your demonlist..." : "Create a new demonlist"}
            >
                <Image
                    className={styles.plusIcon}
                    src={"/addplus.svg"}
                    alt={"Create"}
                    width={22}
                    height={22}
                />
                <span className={styles.buttonText}>
                    {isLoading ? 'Creating...' : 'New Demonlist'}
                </span>
            </button>
        </div>
    );
};
export default CreateDemonlistButton;
