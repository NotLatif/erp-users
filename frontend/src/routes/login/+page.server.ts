import { fail } from '@sveltejs/kit';

export const actions = {
    login: async ({ request, cookies }) => {
        const formData = await request.formData();
        const password = formData.get('password');

        console.log('Login attempt with password:', password)

        if (password === 'supersecret') {
            cookies.set('passwd', 'supersecret', { httpOnly: true, path: '/' });
            return { success: true };
        }

        return fail(400, {
            error: 'Invalid password. Please try again.'
        });
    }
};