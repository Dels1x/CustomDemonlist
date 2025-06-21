import React, {ChangeEvent} from "react";

interface CompletionDateInputProps {
    selectedDate?: string | null;
    onInput: (date: ChangeEvent<HTMLInputElement>) => void;
}

export default function CompletionDateInput({selectedDate, onInput}: CompletionDateInputProps) {
    return (<input
        type="date"
        value={selectedDate ?? ''}
        onChange={(e) => onInput(e)}
    />)
}
