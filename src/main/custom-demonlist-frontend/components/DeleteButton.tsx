import React from "react";

interface DeleteButtonProps {
    onDelete: () => void;
    disabled?: boolean;
    label?: string;
}

const DeleteButton: React.FC<DeleteButtonProps> = ({ onDelete, disabled = false, label = "Delete" }) => {
    return (
        <button
            onClick={onDelete}
            disabled={disabled}
            className="bg-red-600 hover:bg-red-700 text-white font-semibold py-2 px-4 rounded disabled:opacity-50 disabled:cursor-not-allowed"
        >
            {label}
        </button>
    );
};

export default DeleteButton;