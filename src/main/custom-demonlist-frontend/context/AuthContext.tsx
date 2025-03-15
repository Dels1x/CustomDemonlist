import React, {createContext, useContext} from "react";
import {AuthTokenPayload} from "@/api/auth";

interface AuthContextType {
    user: AuthTokenPayload | null;
    accessToken: string | null;
}

const AuthContext = createContext<AuthContextType | null>(null);

export const AuthProvider: React.FC<{
    children: React.ReactNode,
    accessToken: string | null,
    user: AuthTokenPayload | null
}> = ({
          children,
          accessToken,
          user
      }) => {
    return (
        <AuthContext.Provider value={{user, accessToken}}>
            {children}
        </AuthContext.Provider>
    )
}

export const useAuthContext = () => {
    const context = useContext(AuthContext);

    if (!context) {
        throw new Error("useAuthContext must be used within an AuthProvider");
    }

    return context;
}
