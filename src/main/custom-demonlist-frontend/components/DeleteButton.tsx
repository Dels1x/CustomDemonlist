import React from "react";
import styles from "@/styles/DeleteButton.module.css";

interface DeleteButtonProps {
    onDelete: () => void;
    disabled?: boolean;
    label?: string;
    variant?: 'x' | 'trash' | 'dot' | 'line' | 'wide';
}

const DeleteButton: React.FC<DeleteButtonProps> = ({
                                                       onDelete,
                                                       disabled = false,
                                                       label = "×",
                                                       variant = 'x'
                                                   }) => {
    const getButtonClass = () => {
        switch (variant) {
            case 'trash':
                return styles.deleteButtonTrash;
            case 'dot':
                return styles.deleteButtonDot;
            case 'line':
                return styles.deleteButtonLine;
            case 'wide':
                return styles.deleteButtonWide;
            default:
                return styles.deleteButton;
        }
    };

    const renderIcon = () => {
        switch (variant) {
            case 'trash':
                return (
                    <svg className={styles.trashIcon} viewBox="0 0 24 24">
                        <path d="M3 6h18M8 6V4a2 2 0 012-2h4a2 2 0 012 2v2M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6M10 11v6M14 11v6"/>
                    </svg>
                );
            case 'dot':
            case 'line':
                return null;
            case 'wide':
                return <span className={styles.wideButtonText}>{label}</span>;
            default:
                return <span className={styles.deleteIcon}>{label}</span>;
        }
    };

    return (
        <button
            onClick={onDelete}
            disabled={disabled}
            className={getButtonClass()}
            title="Delete this item"
            aria-label="Delete"
        >
            {renderIcon()}
        </button>
    );
};

export default DeleteButton;
