import React from 'react';
import styles from "@/styles/DiscordButton.module.css";
import Image from "next/image";

interface DiscordButtonProps {
    label?: string;
}

const DiscordButton: React.FC<DiscordButtonProps> = ({label = "Sign in with Discord"}) => {
    const handleClick = () => {
        window.location.href = "https://discord.com/oauth2/authorize?client_id=1310700970316922990&response_type=code&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Foauth2%2Fcallback%2Fdiscord&scope=identify+email"
    }
    return (
        <div className={styles.button}>
            <Image
                src={"discord_logo.svg"}
                alt="Discord"
                width={20}
                height={20}
                unoptimized={true}/>
            <button onClick={handleClick}>
                {label}
            </button>
        </div>
    );
};

export default DiscordButton;
