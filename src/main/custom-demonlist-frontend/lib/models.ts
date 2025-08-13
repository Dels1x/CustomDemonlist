export interface Person {
    id: number;
    username: string;
    pfpUrl?: string | null;
    email?: string | null;
    createdAt: string; // ISO timestamp string
    discordId?: string | null;
    googleId?: string | null;
}

export interface Demon {
    id: number;
    demonlistId: number;
    name: string;
    author?: string;
    placement: number;
    initialPlacement?: number | null;
    difficulty?: string | null;
    worstFail?: number | null;
    dateOfCompletion?: string | null; // ISO date string
    gddpDifficulty?: string | null;
    nlwTier?: string | null;
    gddlTier?: string | null;
    aredlPlacement?: number | null;
    attemptsCount?: number | null;
    enjoymentRating?: number | null;
}

export interface Demonlist {
    id: number;
    personId: number;
    name: string;
    isPublic: boolean;
    isMulti: boolean;
    createdAt: Date;
    demons: Demon[];
}
