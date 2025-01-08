import React from 'react';
import styles from "@/styles/DiscordButton.module.css";

interface DiscordButtonProps {
    label?: string;
}

const DiscordButton: React.FC<DiscordButtonProps> = ({ label = "Sign in with Discord" }) => {
    const handleClick = () => {
        window.location.href = "https://discord.com/oauth2/authorize?client_id=1310700970316922990&response_type=code&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Foauth2%2Fcallback%2Fdiscord&scope=identify+email"
    }
    return (
        <button
            onClick={handleClick}
            className={styles.button}
        >
            {label}
        </button>
    );
};

export default DiscordButton;