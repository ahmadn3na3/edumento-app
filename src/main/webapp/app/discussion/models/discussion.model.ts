export enum DiscussionType {
    DISCUSSION = "DISCUSSION",
    INQUIRY = "INQUIRY",
}

export interface DiscussionSummary {
    id: string;
    title: string;
    body: string;
    resourceUrl?: string;
    ownerThumb?: string;
    ownerName?: string;
    ownerId?: number;
    commentsCounter: number;
    creationDate: string; // ZonedDateTime becomes string in JSON
    contentId?: number;
    type: DiscussionType;
    spaceId: number;
}

export interface DiscussionCreateModel {
    title: string;
    body: string;
    resourceUrl?: string;
    spaceId: number;
    contentId?: number;
    type: DiscussionType;
}
