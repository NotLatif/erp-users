
// Extend the Locals interface to include 'user'
import { env } from '$env/dynamic/private';
import type { Handle } from '@sveltejs/kit';

declare module '@sveltejs/kit' {
	interface Locals {
		user?: string | null;
	}
}

export async function handle({event, resolve}) {

	// skip auth check if path is /login
	if (event.url.pathname === '/login') {
		console.log('Skipping auth check for /login');
		return resolve(event);
	}

	event.locals.passwd = event.cookies.get('passwd') || null;

	if (!event.locals.passwd) {
		console.log('No password found in cookies, redirecting to login');
		return Response.redirect(`${env.FRONTEND_URL}/login`, 302);
	}

	console.log('User in handle hook:', event.locals);

	const response = await resolve(event);

	return response;
};