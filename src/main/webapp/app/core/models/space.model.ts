import { User } from "./user.model";

export interface Space {
    id: number;
    name: string;
    description: string;
    color: string;
    image: string;
    thumbnail: string;
    isPrivate: boolean;
    rating: number;

    // Status flags
    newContent: boolean;
    newAssessment: boolean;
    newComments: boolean;
    favorite: boolean;
    owner: boolean;

    // Counts & Settings
    communitySize: number;
    contentSize: number;
    joinRequestsAllowed: boolean;
    autoWifiSyncAllowed: boolean;
    showCommunity: boolean;
    allowRecommendation: boolean;
    allowLeave: boolean;
    joinedStatus: JoinedStatus;

    tags: string[];
    creationDate: string;
    lastModified: string;
    lastAccessed: string;

    // Relations
    creator: SpaceUser;
    community: SpaceUser[];
    role: "VIEWER" | "COLLABORATOR" | "EDITOR" | "CO_OWNER" | "OWNER";
    permissions: { [key: string]: number };
}

export type JoinedStatus = "JOINED" | "PENDING" | "REFUSED" | "NOT_JOINED";

export interface SpaceUser {
    id: number;
    name: string;
    thumbnail: string;
    role: string;
}

export interface SpaceCreate {
    name: string;
    description?: string;
    color?: string;
    image?: string;
    thumbnail?: string;
    price?: number;
    paid?: boolean;
    isPrivate?: boolean;
    tags?: string[];
    joinRequestsAllowed?: boolean;
    autoWifiSyncAllowed?: boolean;
    showCommunity?: boolean;
    allowRecommendation?: boolean;
    allowLeave?: boolean;
    ownerId?: number;
    creationDate?: string;
}
