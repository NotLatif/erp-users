import { fail } from "@sveltejs/kit";
import type { PageServerLoad } from "../$types";

export const load: PageServerLoad = async ({ fetch }) => {

    try {
        const response = await fetch('http://localhost:8080/erp-users-1.0-SNAPSHOT/api/inv/products', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        const products = await response.json();
    
        return {
            products,
            error: null
        };
    } catch (error) {
        console.error('Failed to load products:', error);

        let errmsg = 'Unable to fetch products.';
        if (error instanceof TypeError) {
            if (error.message.includes('fetch failed')) {
                errmsg = 'Network error: Unable to connect to the server.';
            } else if (error instanceof SyntaxError) {
                errmsg = 'Data error: Unable to parse server response.';
            } else {
                errmsg = 'Unexpected error, unable to fetch products.';
            }
        }

        return {
            products: [],
            error: errmsg
        }
    }
};

export const actions = {
    create: async ({ request }) => {
        const formData = await request.formData();

        const name = formData.get('name') as string;
        const price = parseFloat(formData.get('price') as string);
        const sku = formData.get('sku') as string;
        const description = formData.get('description') as string;

        if (!name || isNaN(price) || !sku || !description) {
            return fail(400, {
                error: 'Invalid input data. Please ensure all fields are filled correctly.'
            });
        }
        const body = {
            name,
            price,
            sku,
            description
        }

        try {
            const response = await fetch('http://localhost:8080/erp-users-1.0-SNAPSHOT/api/inv/products', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(body)
            });

            if (!response.ok) {
                throw new Error('Failed to add product');
            }

            return { success: true, newProduct: await response.json() };
        } catch (error) {
            console.error('Failed to add product:', error);
            return fail(500, {
                error: 'Failed to add product. Please try again later.'
            });
        }
    },

    edit: async ({ request }) => {
        const formData = await request.formData();
        const id = formData.get('id') as string;
        const name = formData.get('name') as string;
        const price = parseFloat(formData.get('price') as string);
        const sku = formData.get('sku') as string;
        const description = formData.get('description') as string;

        if (!id || !name || isNaN(price) || !sku || !description) {
            return fail(400, {
                error: 'Invalid input data. Please ensure all fields are filled correctly.'
            });
        }

        const body = {
            name,
            price,
            sku,
            description
        }

        try {
            const response = await fetch(`http://localhost:8080/erp-users-1.0-SNAPSHOT/api/inv/products/${id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(body)
            });

            if (!response.ok) {
                throw new Error('Failed to edit product');
            }

            return { success: true, updatedProduct: await response.json() };
        } catch (error) {
            console.error('Failed to edit product:', error);
            return fail(500, {
                error: 'Failed to edit product. Please try again later.'
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
            const response = await fetch(`http://localhost:8080/erp-users-1.0-SNAPSHOT/api/inv/products/${id}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            if (!response.ok) {
                throw new Error('Failed to delete stock entry');
            }

            return { success: true, deletedProductId: parseInt(id, 10) };
        } catch (error) {
            console.error('Failed to delete product:', error);
            return fail(500, {
                error: 'Failed to delete product. Please try again later.'
            });
        }
    }
};