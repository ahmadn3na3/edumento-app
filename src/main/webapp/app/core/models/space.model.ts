import { User } from './user.model';

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
    joinedStatus: 'NOT_JOINED' | 'JOINED' | 'REQUESTED'; // Inferred

    tags: string[];
    creationDate: string;
    lastModified: string;
    lastAccessed: string;

    // Relations
    creator: SpaceUser;
    community: SpaceUser[];
    role: 'OWNER' | 'ADMIN' | 'MEMBER' | 'GUEST'; // Inferred
    permissions: { [key: string]: number };
}

export interface SpaceUser {
    id: number;
    name: string;
    thumbnail: string;
    role: string;
}
