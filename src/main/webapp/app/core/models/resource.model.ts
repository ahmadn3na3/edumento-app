export interface Resource {
    id: number;
    name: string;
    shelf: string;
    checkSum: string;
    spaceId: number;
    contentLength: number;
    ext: string;
    tags: string[];
    type: 'VIDEO' | 'AUDIO' | 'IMAGE' | 'PDF' | 'DOCUMENT' | 'OTHER'; // Inferred ContentType
    thumbnail: string;
    contentUrl: string;
    allowUseOriginal: boolean;

    // From ContentModel
    favorite: boolean;
    favoriteDate: string;
    fileName: string;
    folderName: string;
    lastAccess: string;
    newContent: boolean;
    status: 'UPLOADED' | 'CONVERTED' | 'FAILED' | 'PROCESSING'; // Inferred ContentStatus
    newAnnotation: boolean;
    owner: boolean;
    numberOfViews: number;
    numberOfAnnotation: number;

    creator: any; // Using any for ContentUserModel to avoid circular dep for now, or define simple interface
    lastModifiedDate: string;
    creationDate: string;
}
