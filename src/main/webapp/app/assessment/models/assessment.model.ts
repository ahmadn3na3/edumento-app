export enum AssessmentType {
    QUIZ = "QUIZ",
    ASSIGNMENT = "ASSIGNMENT",
    WORKSHEET = "WORKSHEET",
    PRACTICE = "PRACTICE",
    CHALLENGE = "CHALLENGE",
}

export enum AssessmentStatus {
    NOT_STARTED = "NOT_STARTED",
    STARTED = "STARTED",
    FINISHED = "FINISHED",
    PAUSED = "PAUSED",
    SUCCESS = "SUCCESS",
    FAIL = "FAIL",
    EVALUATED = "EVALUATED",
    NOT_EVALUATED = "NOT_EVALUATED",
    PARTIALLY_EVALUATED = "PARTIALLY_EVALUATED",
    NEW = "NEW",
    DELETED = "DELETED",
}

export interface AssessmentList {
    id: number;
    title: string;
    dueDate?: string; // ZonedDateTime
    assessmentStatus: AssessmentStatus;
    assessmentType: AssessmentType;
    totalGrade?: number;
    totalPercentage?: number;
    creationDate: string;
    spaceId: number;
    owner?: number;
    isOwner?: boolean;
    // Add other fields as needed for display
    userCounter?: number;
}
