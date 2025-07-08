import type { ProductDTO } from "$lib/types/inventory";
import type { PageServerLoad } from "./$types";

export const load : PageServerLoad = async ({ fetch }) => {

    try {
         const response = await fetch('http://localhost:8080/erp-users-1.0-SNAPSHOT/api/inv/stocks?limit=-1', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        const stocks = await response.json();

        return {
            stocks,
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
            stocks: [],
            error: errmsg
        };
    }
}

export const actions = {
    checkout: async ({ request }) => {
        const formData = await request.formData();
        const cart = JSON.parse(formData.get('cart') as string);

        return { success: true, cart };

        // Process the checkout (e.g., call an API to create an order)
        try {
            const response = await fetch('http://localhost:8080/erp-users-1.0-SNAPSHOT/api/inv/checkout', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ cart })
            });

            const result = await response.json();
            return { success: result.success };
        } catch (error) {
            console.error('Checkout failed:', error);
            return { success: false, error: 'Checkout failed' };
        }
    }
}