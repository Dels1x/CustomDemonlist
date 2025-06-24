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
                {displayDate}
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
