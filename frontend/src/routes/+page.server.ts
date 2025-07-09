import { env } from "$env/dynamic/private";
import type { PageServerLoad } from "./$types";

export const load : PageServerLoad = async ({ fetch }) => {

    try {
         const response = await fetch(`${env.BACKEND_URL}/api/inv/stocks?limit=-1`, {
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
        const cart : { productId: number; quantity: number; }[] = JSON.parse(formData.get('cart') as string);

        console.log('Checkout cart:', cart);

        type RequestBody = {
            orderNo: string;
            customer: string;
            cart: {
                productId: number;
                quantity: number;
            }[],
            status: 'PROCESSING' | 'SHIPPING' | 'DELIVERED' | 'CANCELED';
        }

        const body: RequestBody = {
            orderNo: 'ORD-' + Date.now(),
            customer: 'John Doe',
            cart: cart.map(item => ({
                productId: item.productId,
                quantity: item.quantity
            })),
            status: 'PROCESSING'
        }

        console.log('Checkout body:', body);

        // Process the checkout (e.g., call an API to create an order)
        try {
            const response = await fetch(`${env.BACKEND_URL}/api/orders`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(body)
            });

            const result = await response.json();
            return { success: result.success };
        } catch (error) {
            console.error('Checkout failed:', error);
            return { success: false, error: 'Checkout failed' };
        }
    }
}