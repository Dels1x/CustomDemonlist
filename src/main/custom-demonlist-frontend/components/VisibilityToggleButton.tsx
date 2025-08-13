import React, {useState} from "react";
import styles from '@/styles/VisibilityToggleButton.module.css';


interface VisibilityToggleButtonProps {
    isPublic: boolean;
    onToggle: (newVisibility: boolean) => Promise<void>;
    disabled?: boolean;
    className?: string;
}

const VisibilityToggleButton: React.FC<VisibilityToggleButtonProps> = ({
                                                                           isPublic,
                                                                           onToggle,
                                                                           disabled = false,
                                                                           className = ''
                                                                       }) => {
    const [isLoading, setIsLoading] = useState(false);
    const [isAnimating, setIsAnimating] = useState(false);

    const handleClick = async () => {
        if (disabled || isLoading) return;

        setIsAnimating(true);
        setIsLoading(true);

        try {
            await onToggle(!isPublic);
        } catch (error) {
            console.error('Error toggling visibility:', error);
        } finally {
            setIsLoading(false);
            // Keep animation active a bit longer for visual feedback
            setTimeout(() => setIsAnimating(false), 300);
        }
    };

    return (
        <button
            className={`${styles.toggleButton} ${isPublic ? styles.public : styles.private} ${isAnimating ? styles.animating : ''} ${className}`}
            onClick={handleClick}
            disabled={disabled || isLoading}
            title={isPublic ? 'Make Private' : 'Make Public'}
            aria-label={isPublic ? 'Make demonlist private' : 'Make demonlist public'}
        >
            <div className={styles.iconContainer}>
                {isLoading ? (
                    <svg className={styles.loadingIcon} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                        <path d="M21 12a9 9 0 11-6.219-8.56"/>
                    </svg>
                ) : (
                    <svg className={styles.lockIcon} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                        {/* Lock body - always present */}
                        <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>

                        {isPublic ? (
                            // OPENED lock - shackle is disconnected from the lock body
                            <>
                                <path
                                    d="M17 7a5 5 0 0 0-10 0v5"
                                    className={`${styles.shackle} ${styles.openShackle}`}
                                />
                                <circle cx="12" cy="16" r="1.5" className={styles.keyhole}/>
                            </>
                        ) : (
                            // CLOSED lock - shackle is fully connected to lock body
                            <>
                                <path
                                    d="M7 11V7a5 5 0 0 1 10 0v4"
                                    className={`${styles.shackle} ${styles.closedShackle}`}
                                />
                                <path
                                    d="M17 11V7"
                                    className={`${styles.shackle} ${styles.closedShackle}`}
                                />
                                <circle cx="12" cy="16" r="1" className={styles.keyhole}/>
                                <rect x="11.5" y="17" width="1" height="2" className={styles.keyholeSlot}/>
                            </>
                        )}
                    </svg>
                )}
            </div>
            <span className={styles.buttonText}>
                {isPublic ? 'Public' : 'Private'}
            </span>
        </button>
    );
};

export default VisibilityToggleButton;
