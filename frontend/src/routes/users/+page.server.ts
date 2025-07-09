import { fail } from '@sveltejs/kit';
import type { Actions, PageServerLoad } from './$types';
import { env } from '$env/dynamic/private';

export const load: PageServerLoad = async ({ fetch }) => {

    try {
        const response = await fetch(`${env.BACKEND_URL}/api/users`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        const users = await response.json();
    
        return {
            users,
            error: null
        };
    } catch (error) {
        console.error('Failed to load users:', error);

        let errmsg = 'Unable to fetch users.';
        if (error instanceof TypeError) {
            if (error.message.includes('fetch failed')) {
                errmsg = 'Network error: Unable to connect to the server.';
            } else {
                errmsg = 'Unexpected error, unable to fetch users.';
            }
        }

        return {
            users: [],
            error: errmsg
        }
    }
};

export const actions = {
    create: async ({ request }) => {

        const formData = await request.formData();
        const username = formData.get('username') as string;
        const email = formData.get('email') as string;
        const role = formData.get('role') as string;
        
        console.log('Creating user:', { username, email, role });

        const response = await fetch(`${env.BACKEND_URL}/api/users`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, email }) // Role not yet implemented
        });
        if (!response.ok) {
            console.warn('Failed to create user:', response.statusText);
            return fail(400, {
                message: 'Failed to create user. Please try again.'
            });
        }
        return { success: true, newUser: await response.json() };
    },

    update: async ({ request }) => {
        const formData = await request.formData();

        const id = formData.get('id') as string;
        const username = formData.get('username') as string;
        const email = formData.get('email') as string;
        
        console.log('Updating user:', { id, username, email });

        const response = await fetch(`${env.BACKEND_URL}/api/users/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, email })
        });
        if (!response.ok) {
            console.warn('Failed to update user:', response.statusText);
            return fail(400, {
                message: 'Failed to update user. Please try again.'
            });
        }
        return { success: true, updatedUser: await response.json() };
    },

    delete: async ({ request }) => {
        const formData = await request.formData();
        const id = formData.get('id') as string;

        console.log('Deleting user with ID:', id);
        const response = await fetch(`${env.BACKEND_URL}/api/users/${id}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        if (!response.ok) {
            console.warn('Failed to delete user:', response.statusText);
            return fail(400, {
                message: 'Failed to delete user. Please try again.'
            });
        }
        return { success: true, deletedUserId: id };
    }
} satisfies Actions;
