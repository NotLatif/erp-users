import { fail } from "@sveltejs/kit";
import type { PageServerLoad } from "./$types";
import { env } from "$env/dynamic/private";

export const load: PageServerLoad = async ({ fetch }) => {

    console.log(env.BACKEND_URL)
    try {
        const response = await fetch(`${env.BACKEND_URL}/api/inv/warehouses`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        const warehouses = await response.json();

        return {
            warehouses,
            error: null
        };
    } catch (error) {
        console.error('Failed to load warehouses:', error);

        let errmsg = 'Unable to fetch warehouses.';
        if (error instanceof TypeError) {
            if (error.message.includes('fetch failed')) {
                errmsg = 'Network error: Unable to connect to the server.';
            } else if (error instanceof SyntaxError) {
                errmsg = 'Data error: Unable to parse server response.';
            } else {
                errmsg = 'Unexpected error, unable to fetch warehouses.';
            }
        }

        return {
            warehouses: [],
            error: errmsg
        }
    }
};

export const actions = {
    create: async ({ request }) => {
        const formData = await request.formData();

        const name = formData.get('name') as string;
        const location = formData.get('location') as string;

        if (!name || !location) {
            return fail(400, {
                error: 'Invalid input data. Please ensure all fields are filled correctly.'
            });
        }
        const body = {
            name,
            location
        }

        try {
            const response = await fetch(`${env.BACKEND_URL}/api/inv/warehouses`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(body)
            });

            if (!response.ok) {
                throw new Error('Failed to add warehouse');
            }

            return { success: true, newWarehouse: await response.json() };
        } catch (error) {
            console.error('Failed to add warehouse:', error);
            return fail(500, {
                error: 'Failed to add warehouse. Please try again later.'
            });
        }
    },

    edit: async ({ request }) => {
        const formData = await request.formData();
        const id = formData.get('id') as string;
        const name = formData.get('name') as string;
        const location = formData.get('location') as string;


        if (!id || !name || !location) {
            return fail(400, {
                error: 'Invalid input data. Please ensure all fields are filled correctly.'
            });
        }

        const body = {
            name,
            location
        }

        try {
            const response = await fetch(`${env.BACKEND_URL}/api/inv/warehouses/${id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(body)
            });

            if (!response.ok) {
                throw new Error('Failed to edit warehouse');
            }

            return { success: true, updatedWarehouse: await response.json() };
        } catch (error) {
            console.error('Failed to edit warehouse:', error);
            return fail(500, {
                error: 'Failed to edit warehouse. Please try again later.'
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
            const response = await fetch(`${env.BACKEND_URL}/api/inv/warehouses/${id}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            if (!response.ok) {
                throw new Error('Failed to delete warehouse');
            }

            return { success: true, deletedWarehouseId: parseInt(id, 10) };
        } catch (error) {
            console.error('Failed to delete warehouse:', error);
            return fail(500, {
                error: 'Failed to delete warehouse. Please try again later.'
            });
        }
    }
};