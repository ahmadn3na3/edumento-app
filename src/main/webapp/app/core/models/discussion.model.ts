export interface Discussion {
    id: string; // UUID often strings
    title: string;
    body: string;
    resourceUrl: string;
    ownerThumb: string;
    ownerName: string;
    ownerId: number;
    commentsCounter: number;
    creationDate: string;
    contentId: number;
    type: 'QUESTION' | 'DISCUSSION' | 'ANNOUNCEMENT'; // Inferred DiscussionType
    spaceId: number;
}
