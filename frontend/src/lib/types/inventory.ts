export type Product = {
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

export type StockEntry = {
    id: number;
    quantity: number;
    uat: string;
    product: Product;
    warehouse: Warehouse;
};

export type Inventory = {
    products: Product[];
    warehouses: Warehouse[];
    stockEntries: StockEntry[];
};