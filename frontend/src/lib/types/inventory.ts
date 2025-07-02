export type Product = {
    id: number;
    name: string;
    price: number;
    sku: number;
    description: string;
};

export type Warehouse = {
    id: number;
    name: string;
    location: string;
};

export type StockEntry = {
    id: number;
    quantity: number;
    updatedAt: string;
    productId: number;
    warehouseId: number;
};

export type Inventory = {
    products: Product[];
    warehouses: Warehouse[];
    stockEntries: StockEntry[];
};