export interface User {
    id: number;
    login: string;
    fullName: string;
    userType: 'USER' | 'ADMIN' | 'SUPER_ADMIN'; // Inferred from usage
    email: string;
    activated: boolean;
    lang: string;
    gender: 'MALE' | 'FEMALE';
    mobile: string;
    profession: string;
    country: string;
    image: string;
    userStatus: string;
    interests: string;
    birthDate: string; // ISO Date string
    notification: boolean;
    emailNotification: boolean;
    autoJoin: boolean;

    // From UserModel
    spacesCount: number;
    school: string;

    // Permissions
    permissions: { [key: string]: any };
    spaceRolePermission: { [key: string]: { [key: string]: any } };
}
