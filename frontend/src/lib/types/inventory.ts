export type ProductDTO = {
    id: number;
    name: string;
    price: number;
    sku: string;
    description: string;
};

export type Warehouse = {
    id: number;
    name: string;
    location: string;
};

export type StockEntryDTO = {
    id: number;
    quantity: number;
    uat: string;
    product: ProductDTO;
    warehouse: Warehouse;
};

export type Inventory = {
    products: ProductDTO[];
    warehouses: Warehouse[];
    stockEntries: StockEntryDTO[];
};

export type CartItem = {
    productId: number;
    productName: string;
    quantity: number;
}

export type Order = {
    id: number;
    customer: string;
    cart: CartItem[];
    totalAmount?: number;
    status: 'PROCESSING' | 'SHIPPING' | 'DELIVERED' | 'CANCELED';
    updatedAt: string;
    orderNo: string;
}