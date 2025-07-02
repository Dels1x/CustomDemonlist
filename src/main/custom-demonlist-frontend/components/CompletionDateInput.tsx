import React, {ChangeEvent, useRef} from "react";

interface CompletionDateInputProps {
    selectedDate?: string | null;
    onInput: (date: ChangeEvent<HTMLInputElement>) => void;
}

export default function CompletionDateInput({selectedDate, onInput}: CompletionDateInputProps) {
    const hiddenInputRef = useRef<HTMLInputElement>(null);

    const displayDate = selectedDate
        ? new Date(selectedDate).toLocaleDateString()
        : "Pick a date";

    function formatDate(dateStr: string) {
        if (displayDate === "Pick a date") {
            return displayDate;
        }

        const [month, day, year] = dateStr.split("/").map(Number);
        const months = ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August',
            'September', 'October', 'November', 'December'];

        return `${months[month - 1]} ${day}${ordinalSuffix(day)} ${year}`;
    }

    function ordinalSuffix(day: number) {
        if (day > 10 && day < 14) {
            return 'th';
        }

        switch (day % 10) {
            case 1: return 'st';
            case 2: return 'nd';
            case 3: return 'rd';
            default: return 'th';
        }
    }

    const handleButtonClick = () => {
        console.log("hello from handleButtonClick CompletionDateInput");
        hiddenInputRef.current?.showPicker?.();
        hiddenInputRef.current?.click();
    }

    console.log("displayDate", displayDate);

    return (
        <div>
            <button
                onClick={handleButtonClick}
            >
                {formatDate(displayDate)}
            </button>
            <input
                ref={hiddenInputRef}
                type="date"
                value={selectedDate ?? ''}
                onChange={(e) => onInput(e)}
                className="absolute w-0 h-0 opacity-0 pointer-events-none"
            />
        </div>
    )
}
