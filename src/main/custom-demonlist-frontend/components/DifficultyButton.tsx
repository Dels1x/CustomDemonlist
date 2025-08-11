"use client"

import {useEffect, useRef, useState} from "react";
import Image from "next/image";
import {DIFFICULTY_MAP} from "@/constants/difficultyMap";
import styles from "@/styles/DifficultyButton.module.css";
import {createPortal} from "react-dom";

interface DifficultyButtonProps {
    options: string[],
    selected: string,
    onSelect: (value: string) => void,
    isAuthorizedToEdit: boolean,
}

export default function DifficultyButton({options, selected, onSelect, isAuthorizedToEdit}: DifficultyButtonProps) {
    const [isOpen, setIsOpen] = useState(false);
    const [dropdownPosition, setDropdownPosition] = useState({ top: 0, left: 0 });
    const buttonRef = useRef<HTMLButtonElement>(null);
    const dropdownRef = useRef<HTMLDivElement>(null);

    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
            if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node) &&
                buttonRef.current && !buttonRef.current.contains(event.target as Node)) {
                setIsOpen(false);
            }
        };

        const handleScroll = () => {
            if (isOpen) {
                setIsOpen(false); // Close dropdown on scroll
            }
        };

        if (isOpen) {
            document.addEventListener("mousedown", handleClickOutside);
            window.addEventListener("scroll", handleScroll);
        }

        return () => {
            document.removeEventListener("mousedown", handleClickOutside);
            window.removeEventListener("scroll", handleScroll);
        };
    }, [isOpen]);

    const handleToggleDropdown = () => {
        if (!isAuthorizedToEdit) return;

        if (!isOpen && buttonRef.current) {
            const rect = buttonRef.current.getBoundingClientRect();
            setDropdownPosition({
                top: rect.bottom + window.scrollY + 4,
                left: rect.left + window.scrollX - 84 // Center the dropdown (200px width / 2 - 16px button / 2)
            });
        }
        setIsOpen(!isOpen);
    };

    const handleOptionSelect = (opt: string) => {
        onSelect(opt);
        setIsOpen(false);
    };

    const getDifficultyClass = (difficulty: string) => {
        switch (difficulty) {
            case 'EXTREME_DEMON':
                return styles.extremeDemon;
            case 'INSANE_DEMON':
                return styles.insaneDemon;
            case 'HARD_DEMON':
                return styles.hardDemon;
            case 'MEDIUM_DEMON':
                return styles.mediumDemon;
            case 'EASY_DEMON':
                return styles.easyDemon;
            case 'INSANE':
                return styles.insane;
            case 'HARDER':
                return styles.harder;
            case 'HARD':
                return styles.hard;
            case 'NORMAL':
                return styles.normal;
            case 'EASY':
                return styles.easy;
            case 'AUTO':
                return styles.auto;
            default:
                return '';
        }
    };

    const buttonClasses = [
        styles.imageButton,
        !isAuthorizedToEdit ? styles.imageButtonDisabled : ''
    ].filter(Boolean).join(' ');

    // Only render dropdown if we're in the browser (not SSR)
    const dropdown = isOpen && isAuthorizedToEdit && typeof window !== 'undefined' ? (
        <div
            ref={dropdownRef}
            className={styles.portalDropdown}
            style={{
                position: 'fixed',
                top: dropdownPosition.top,
                left: dropdownPosition.left,
                zIndex: 10000
            }}
        >
            {options.map((opt) => (
                <div
                    key={opt}
                    onClick={() => handleOptionSelect(opt)}
                    className={styles.option}
                >
                    <Image
                        className={styles.optionIcon}
                        src={`/difficulty/${opt === "N/A" ? "N-A" : opt}.webp`}
                        alt={opt}
                        width={24}
                        height={24}
                    />
                    <span className={`${styles.optionText} ${getDifficultyClass(opt)}`}>
                        {DIFFICULTY_MAP.get(opt)}
                    </span>
                </div>
            ))}
        </div>
    ) : null;

    return (
        <>
            <button
                ref={buttonRef}
                onClick={handleToggleDropdown}
                className={buttonClasses}
                disabled={!isAuthorizedToEdit}
            >
                <Image
                    className={styles.difficultyIcon}
                    src={`/difficulty/${selected === "N/A" ? "N-A" : selected}.webp`}
                    alt={selected}
                    width={48}
                    height={48}
                />
            </button>

            {dropdown && createPortal(dropdown, document.body)}
        </>
    );
}
