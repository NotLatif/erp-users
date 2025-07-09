<script>
	import { enhance } from "$app/forms";
	let message = $state('');
	let messageType = $state('');

    let password = $state('');
</script>

<div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 to-blue-100">
	<form
		action="?/login"
        method="POST"
		use:enhance={({ formData }) => {
				// `formElement` is this `<form>` element
				// `formData` is its `FormData` object that's about to be submitted
				// `action` is the URL to which the form is posted
				// calling `cancel()` will prevent the submission
				// `submitter` is the `HTMLElement` that caused the form to be submitted
				formData.append('password', password);

				return async ({ result, update }) => {
			if (result.type === 'success') {
				message = 'Login successful!';
				messageType = 'success';
            } else if (result.type === 'failure') {
                message = 'Login failed: Invalid credentials';
                messageType = 'error';
			} else if (result.type === 'error') {
				message = 'Login failed: ' + (result.error || 'Unknown error');
				messageType = 'error';
			}
		}}}
		class="bg-white p-8 rounded-2xl shadow-xl w-full max-w-sm flex flex-col gap-6"
	>
		<div class="flex flex-col items-center mb-2">
			<!-- Avatar/Logo -->
			<div class="bg-blue-100 rounded-full p-3 mb-2">
				<svg class="w-10 h-10 text-blue-500" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
					<path stroke-linecap="round" stroke-linejoin="round" d="M12 11c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v3h16v-3c0-2.66-5.33-4-8-4z"/>
				</svg>
			</div>
			<h2 class="text-2xl font-bold text-center text-gray-800">Login</h2>
		</div>
		{#if message}
			<div class={`mb-2 text-center text-sm ${messageType === 'success' ? 'text-green-600' : 'text-red-600'}`}>
				{message}
			</div>
		{/if}
		<div class="relative">

			<input
				type="password"
				name="password"
				placeholder="Password"
				class="w-full p-3 mb-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-400 transition"
				required
			/>
		</div>
		<button
			type="submit"
			class="w-full bg-blue-500 text-white p-3 rounded-lg font-semibold hover:bg-blue-600 transition focus:outline-none focus:ring-2 focus:ring-blue-400"
		>
			Login
		</button>
	</form>
</div>