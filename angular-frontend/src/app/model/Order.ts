import { User } from "./User ";

export class Order {
    id: number;
    user: User;
    createdAt: Date;
    state: string;
    isActive: boolean;
    edit: boolean;
    editBy: string;
    editFrom: string;
    editDate: Date;
}