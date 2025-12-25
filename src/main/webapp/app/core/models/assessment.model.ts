export interface Assessment extends AssessmentList {
    lockMint: boolean;
    randomizingQuestion: boolean;
    workSheetContentId?: number;
    assessmentQuestionCreateModels: any[]; // Define Question Structure if needed
    userWorkSheetAnswerModel?: any;
    ownerWorkSheetAnswerModel?: any;
}

export interface AssessmentList {
    id: number;
    title?: string; // Inherited from CreateModel
    dueDate?: string;
    assessmentType?: string;
    viewAnswersAfterSubmit?: boolean;

    assessmentStatus: 'NOT_STARTED' | 'IN_PROGRESS' | 'SUBMITTED' | 'EVALUATED';
    creationDate: string;
    lastModifiedDate: string;
    publishedDate: string;

    userCounter: number;
    corrected: number;
    alreadyTaken: boolean;
    totalGrade: number;
    totalPercentage: number;
    topThree: boolean;
    bestAnswer: boolean;
    rank: number;
    numberOfQuestions: number;
    limitedByTime: boolean;

    owner: number;
    isOwner: boolean;
    duration: number;
}
