import {Demonlist} from "@/lib/models";
import React, {createContext, useContext, useEffect, useState} from "react";
import {getDemonlistsForUserId} from "@/api/api";

interface DemonlistContextType {
    demonlists: Demonlist[];
    updateDemonlists: (updatedDemonlist: Demonlist) => void;
    refreshDemonlists: () => Promise<void>;
}

const DemonlistContext = createContext<DemonlistContextType | null>(null);

export const DemonlistProvider:
    React.FC<{
        userId: string,
        accessToken: string,
        children: React.ReactNode
    }> = ({userId, accessToken, children}) => {
    const [demonlists, setDemonlists] = useState<Demonlist[]>([]);

    const refreshDemonlists = async () => {
        try {
            const demonlists = await getDemonlistsForUserId(userId, accessToken);
            setDemonlists(demonlists);
        } catch (error) {
            console.error(error);
        }
    }

    useEffect(() => {
        refreshDemonlists();
    }, [userId, accessToken])

    const updateDemonlists = (updatedDemonlist: Demonlist) => {
        setDemonlists((prev) =>
            prev.map((d) => (d.id === updatedDemonlist.id ? updatedDemonlist : d)));
    };

    return (
        <DemonlistContext.Provider value={{demonlists, updateDemonlists, refreshDemonlists}}>
            {children}
        </DemonlistContext.Provider>
    )
}

export const useDemonlistContext = () => {
    const context = useContext(DemonlistContext);

    if (!context) {
        throw new Error("useDemonlistContext must be used as within a DemonlistProvider");
    }

    return context;
}


export const useOptionalDemonlistContext = () => {
    return useContext(DemonlistContext);
}
