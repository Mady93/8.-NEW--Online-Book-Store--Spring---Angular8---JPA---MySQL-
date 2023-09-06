import { Discount } from "./Discount";

export class Book {
    id: number;
    isActive: boolean;
    name: string;
    author: string;
    price: number;
    picByte: string;
    isAdded: boolean;
    discount: Discount;
}