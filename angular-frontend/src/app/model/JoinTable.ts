import { Book } from "./Book";
import { Order } from "./Order";

export class JoinTable {
    id: number;
    book: Book = new Book();
    order: Order = new Order();
    quantity: number;
    price: number;
    addedAt: Date;
}