export enum ContentStatus {
    NOT_UPLOAD = 'NOT_UPLOAD',
    UPLOADING = 'UPLOADING',
    UPLOADED = 'UPLOADED',
    READY = 'READY',
    ERROR = 'ERROR'
}

export interface Content {
    id: number;
    fileName: string;
    folderName?: string;
    status: ContentStatus;
    favorite?: boolean;
    creationDate: string;
    owner?: boolean;
    newContent?: boolean;
    numberOfViews?: number;
    numberOfAnnotation?: number;
    // Add other fields as needed
}
