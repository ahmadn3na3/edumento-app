export interface ResponseModel<T> {
    code: number;
    message: string;
    data: T;
    messageData?: any;
    dateTime?: string;
}

export interface PageResponseModel<T> extends ResponseModel<T> {
    totalPages: number;
    page: number;
    size: number;
}
