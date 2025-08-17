import React, { useMemo } from "react";
import styles from "@/styles/DeleteButton.module.css";

type DeleteButtonVariant = 'x' | 'trash' | 'dot' | 'line' | 'wide';

interface DeleteButtonProps {
    onDelete: () => void;
    disabled?: boolean;
    label?: string;
    variant?: DeleteButtonVariant;
    title?: string;
    'aria-label'?: string;
    className?: string;
}

const VARIANT_CLASS_MAP: Record<DeleteButtonVariant, string> = {
    x: styles.deleteButton,
    trash: styles.deleteButtonTrash,
    dot: styles.deleteButtonDot,
    line: styles.deleteButtonLine,
    wide: styles.deleteButtonWide,
} as const;

const TrashIcon: React.FC<{ className?: string }> = ({ className }) => (
    <svg className={className} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
        <path d="M3 6h18M8 6V4a2 2 0 012-2h4a2 2 0 012 2v2M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6M10 11v6M14 11v6"/>
    </svg>
);

const DeleteButton: React.FC<DeleteButtonProps> = ({
    onDelete,
    disabled = false,
    label = "×",
    variant = 'x',
    title = "Delete this item",
    'aria-label': ariaLabel = "Delete",
    className
}) => {
    const buttonClass = useMemo(() => {
        const baseClass = VARIANT_CLASS_MAP[variant];
        return className ? `${baseClass} ${className}` : baseClass;
    }, [variant, className]);

    const iconContent = useMemo(() => {
        switch (variant) {
            case 'trash':
                return <TrashIcon className={styles.trashIcon} />;
            case 'dot':
            case 'line':
                return null;
            case 'wide':
                return <span className={styles.wideButtonText}>{label}</span>;
            default:
                return <span className={styles.deleteIcon}>{label}</span>;
        }
    }, [variant, label]);

    return (
        <button
            type="button"
            onClick={onDelete}
            disabled={disabled}
            className={buttonClass}
            title={title}
            aria-label={ariaLabel}
        >
            {iconContent}
        </button>
    );
};

export default DeleteButton;
