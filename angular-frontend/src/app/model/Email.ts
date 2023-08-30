import { Order } from "./Order";

export class Email {
    from: string;
    to: string;
    subject: string;
    body: string;
    order: Order;
    sendedAt: Date;
}