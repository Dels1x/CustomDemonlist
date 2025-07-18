"use client"

import {useEffect, useRef, useState} from "react";
import Image from "next/image";
import {DIFFICULTY_MAP} from "@/constants/difficultyMap";

interface DropdownWithImagesProps {
    options: string[],
    selected: string,
    onSelect: (value: string) => void,
    isAuthorizedToEdit: boolean,
}

export default function DropdownWithImages({options, selected, onSelect, isAuthorizedToEdit}: DropdownWithImagesProps) {
    const [isOpen, setIsOpen] = useState(false);
    const dropdownRef = useRef<HTMLDivElement>(null);

    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
            if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
                setIsOpen(false);
            }
        };
        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, []);

    return (
        <div className="relative inline-block w-40" ref={dropdownRef}>
            <button
                onClick={() => setIsOpen(!isOpen)}
                className="w-full flex items-center justify-between border px-2 py-1 bg-black hover:bg-gray-700"
            >
                <div className="flex items-center gap-2">
                    <Image
                        src={`/difficulty/${selected === "N/A" ? "N-A" : selected}.webp`}
                        alt={selected}
                        width={20}
                        height={20}
                    />
                    <span className="text-sm">{DIFFICULTY_MAP.get(selected)}</span>
                </div>
                {isAuthorizedToEdit && (<span>▼</span>)}
            </button>

            {isOpen && isAuthorizedToEdit && (
                <div className="absolute left-0 z-50 w-full bg-black border mt-1 shadow-md">
                    {options.map((opt) => (
                        <div
                            key={opt}
                            onClick={() => {
                                onSelect(opt);
                                setIsOpen(false);
                            }}
                            className="flex items-center gap-2 px-2 py-1 hover:bg-gray-700 cursor-pointer"
                        >
                            <Image
                                src={`/difficulty/${opt === "N/A" ? "N-A" : opt}.webp`}
                                alt={opt}
                                width={20}
                                height={20}
                            />
                            <span className="text-sm">{DIFFICULTY_MAP.get(opt)}</span>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}
