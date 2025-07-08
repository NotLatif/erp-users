import { fail } from "@sveltejs/kit";
import type { PageServerLoad } from "./$types";

export const load: PageServerLoad = async ({ fetch }) => {

    try {
        const headers = {
            'Content-Type': 'application/json'
        };

        const init : RequestInit = {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        };

        const [stockEntries, warehouses, products, orders] = await Promise.all([
            fetch('http://localhost:8080/erp-users-1.0-SNAPSHOT/api/inv/stocks', init)      .then(res => res.json()),
            fetch('http://localhost:8080/erp-users-1.0-SNAPSHOT/api/inv/warehouses', init)  .then(res => res.json()),            
            fetch('http://localhost:8080/erp-users-1.0-SNAPSHOT/api/inv/products?limit=-1', init)    .then(res => res.json()),
            fetch('http://localhost:8080/erp-users-1.0-SNAPSHOT/api/orders', init)          .then(res => res.json())
        ]);


        return {
            stockEntries,
            warehouses,
            products,
            orders,
            error: null
        };
    } catch (error) {
        console.error('Failed to load stock entries:', error);

        let errmsg = 'Unable to fetch stock entries.';
        if (error instanceof TypeError) {
            if (error.message.includes('fetch failed')) {
                errmsg = 'Network error: Unable to connect to the server.';
            } else if (error instanceof SyntaxError) {
                errmsg = 'Data error: Unable to parse server response.';
            } else {
                errmsg = 'Unexpected error, unable to fetch stock entries.';
            }
        }

        return {
            stockEntries: [],
            error: errmsg
        }
    }
};

export const actions = {
    add: async ({ request }) => {
        const formData = await request.formData();
        const productId = formData.get('product_id') as string;
        const warehouseId = formData.get('warehouse_id') as string;
        const quantity = parseInt(formData.get('quantity') as string, 10);
        
        
        if (!productId || !warehouseId || isNaN(quantity)) {
            return fail(400, {
                error: 'Invalid input data. Please ensure all fields are filled correctly.'
            });
        }

        const now = new Date(Date.now());
        const pad = (n: number) => n.toString().padStart(2, '0');
        const uat = `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())}`;

        const body = {
            quantity,
            uat,
            product: {
                id: parseInt(productId, 10)
            },
            warehouse: {
                id: parseInt(warehouseId, 10)
            }
        }

        try {
            const response = await fetch('http://localhost:8080/erp-users-1.0-SNAPSHOT/api/inv/stocks', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(body)
            });

            if (!response.ok) {
                throw new Error('Failed to add stock entry');
            }

            return { success: true, newStockEntry: await response.json() };
        } catch (error) {
            console.error('Failed to add stock entry:', error);
            return fail(500, {
                error: 'Failed to add stock entry. Please try again later.'
            });
        }
    },
    
    edit: async ({ request }) => {
        const formData = await request.formData();
        const id = formData.get('id') as string;
        const productId = formData.get('product_id') as string;
        const warehouseId = formData.get('warehouse_id') as string;
        const quantity = parseInt(formData.get('quantity') as string, 10);
        
        if (!id || !productId || !warehouseId || isNaN(quantity)) {
            return fail(400, {
                error: 'Invalid input data. Please ensure all fields are filled correctly.'
            });
        }

        const now = new Date(Date.now());
        const pad = (n: number) => n.toString().padStart(2, '0');
        const uat = `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())}`;

        const body = {
            quantity,
            product: {
                id: parseInt(productId, 10)
            },
            warehouse: {
                id: parseInt(warehouseId, 10)
            },
            uat
        }

        try {
            const response = await fetch(`http://localhost:8080/erp-users-1.0-SNAPSHOT/api/inv/stocks/${id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(body)
            });

            if (!response.ok) {
                throw new Error('Failed to edit stock entry');
            }

            return { success: true, updatedStockEntry: await response.json() };
        } catch (error) {
            console.error('Failed to edit stock entry:', error);
            return fail(500, {
                error: 'Failed to edit stock entry. Please try again later.'
            });
        }
    },

    delete: async ({ request }) => {
        const formData = await request.formData();
        const id = formData.get('id') as string;

        if (!id) {
            return fail(400, {
                error: 'Invalid input data. Please ensure the ID is provided.'
            });
        }

        try {
            const response = await fetch(`http://localhost:8080/erp-users-1.0-SNAPSHOT/api/inv/stocks/${id}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            if (!response.ok) {
                throw new Error('Failed to delete stock entry');
            }

            return { success: true, deletedStockId: parseInt(id, 10) };
        } catch (error) {
            console.error('Failed to delete stock entry:', error);
            return fail(500, {
                error: 'Failed to delete stock entry. Please try again later.'
            });
        }
    }
};