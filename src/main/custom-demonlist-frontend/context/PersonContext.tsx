import React, { createContext, useContext, useEffect, useState } from 'react';
import { Person } from '@/lib/models';

import { useAuthContext } from './AuthContext';
import {getUser} from "@/api/api";

interface PersonContextType {
    person: Person | null;
    isLoading: boolean;
    error: string | null;
    refetchPerson: () => Promise<void>;
}

const PersonContext = createContext<PersonContextType | null>(null);

export const PersonProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const { user } = useAuthContext();
    const [person, setPerson] = useState<Person | null>(null);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const fetchPerson = async () => {
        if (!user?.sub) {
            setPerson(null);
            setError(null);
            return;
        }

        setIsLoading(true);
        setError(null);
        try {
            const personData = await getUser(parseInt(user.sub));
            setPerson(personData);
        } catch (err) {
            setError('Failed to fetch user data');
            console.error('Error fetching person data:', err);
            setPerson(null);
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        fetchPerson();
    }, [user?.sub]);

    const refetchPerson = async () => {
        await fetchPerson();
    };

    return (
        <PersonContext.Provider value={{ person, isLoading, error, refetchPerson }}>
    {children}
    </PersonContext.Provider>
);
};

export const usePersonContext = () => {
    const context = useContext(PersonContext);

    if (!context) {
        throw new Error("usePersonContext must be used within a PersonProvider");
    }

    return context;
};