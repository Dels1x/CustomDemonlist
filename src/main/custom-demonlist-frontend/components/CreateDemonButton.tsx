import Image from "next/image";
import React, {useState} from "react";
import {countDemonsInDemonlist, createNewDemon} from "@/api/api";
import {Demon} from "@/lib/models";
import styles from "@/styles/CreateDemonButton.module.css"

interface CreateDemonButtonProps {
    demonlistId: number;
    accessToken: string;
    onDemonCreated: (demonlist: Demon) => void;
}

const CreateDemonButton: React.FC<CreateDemonButtonProps> = ({demonlistId, accessToken, onDemonCreated}) => {
    const [isLoading, setIsLoading] = useState(false);
    const [isSuccess, setIsSuccess] = useState(false);

    const handleClick = async () => {
        if (isLoading) return;

        setIsLoading(true);

        try {
            const count: number = await countDemonsInDemonlist(demonlistId, accessToken);
            const demon = {
                name: "Demon #" + (count + 1),
                author: "Author",
            };

            const newDemon: Demon = await createNewDemon(demon, demonlistId, accessToken);
            console.log("newDemon: " + JSON.stringify(newDemon, null, 2));
            onDemonCreated(newDemon);

            setIsSuccess(true);
            setTimeout(() => setIsSuccess(false), 600);
        } catch (error) {
            console.error("Error creating demon:", error);
        } finally {
            setIsLoading(false);
        }
    };

    const buttonClasses = [
        styles.createButton,
        isLoading ? styles.loading : '',
        isSuccess ? styles.success : ''
    ].filter(Boolean).join(' ');

    return (
        <div className={styles.container}>
            <button
                onClick={handleClick}
                className={buttonClasses}
                disabled={isLoading}
            >
                <Image
                    className={styles.plusIcon}
                    src={"/addplus.svg"}
                    alt={"Create"}
                    width={20}
                    height={20}
                />
                <span className={styles.buttonText}>
                    {isLoading ? 'Creating...' : 'New Demon'}
                </span>
            </button>
        </div>
    );
}

export default CreateDemonButton;
