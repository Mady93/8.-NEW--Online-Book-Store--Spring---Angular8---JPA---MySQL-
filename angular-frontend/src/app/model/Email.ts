import { User } from "./User ";

export class Email {
    from: string;
    to: string;
    subject: string;
    body: string;
    user: User;
    sendedAt: Date;
}