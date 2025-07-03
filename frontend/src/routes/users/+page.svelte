<script lang="ts">
	import { enhance } from '$app/forms';
	import Footer from '$lib/components/Footer.svelte';
	import Header from '$lib/components/Header.svelte';
	import { fade } from 'svelte/transition';

	type User = {
		id: number;
		username: string;
		email: string;
	};

	// site backend data
	let {data} = $props();

	let users : User[] = $state<User[]>(data.users);


	// Adding user
	let newUser = $state({
		username: '',
		email: ''
	});
	let addingUser = $state(false);

	// editing user
	let editingUser = $state<User | null>(null);

	// deleting user
	let deletingUser = $state<User | null>(null);

	// notification management (appears for 2 seconds)
	let actionSuccess = $state("");
	function setActionSuccess(message: string) {
		actionSuccess = message;
		setTimeout(() => {
			actionSuccess = "";
		}, 2000);
	}

	let actionFailure = $state("");
	function setActionFailure(message: string) {
		actionFailure = message;
		setTimeout(() => {
			actionFailure = "";
		}, 2000);
	}


	function editUser(id: number) {
		const foundUser = users.find(user => user.id === id);
		if (foundUser) {
			editingUser = { ...foundUser };
		} else {
			setActionFailure(`User with ID ${id} not found.`);
			return;
		}
		
	}

	function deleteUser(id: number) {
		const foundUser = users.find(user => user.id === id);
		if (foundUser) {
			deletingUser = { ...foundUser };
		} else {
			setActionFailure(`User with ID ${id} not found.`);
		}
	}
</script>

{#if actionSuccess}
	<div 
		class="fixed bottom-4 right-4 z-50 bg-green-500 text-white px-4 py-2 rounded-lg shadow-md"
		transition:fade={{ duration: 200 }}
	>
		{actionSuccess}
	</div>
{/if}

{#if actionFailure}
	<div 
		class="fixed bottom-4 right-4 z-50 bg-red-500 text-white px-4 py-2 rounded-lg shadow-md"
		transition:fade={{ duration: 200 }}
	>
		{actionFailure}
	</div>
{/if}

{#if addingUser}
	<div class="fixed inset-0 z-50 flex items-center justify-center bg-gray-900/50">
		<div class="bg-white p-6 rounded-lg shadow-md max-w-md w-full">
			<h2 class="mb-4 text-xl font-semibold text-gray-700">Add New User</h2>
			<form action="?/create" method="POST" use:enhance={({ formData }) => {
				// `formElement` is this `<form>` element
				// `formData` is its `FormData` object that's about to be submitted
				// `action` is the URL to which the form is posted
				// calling `cancel()` will prevent the submission
				// `submitter` is the `HTMLElement` that caused the form to be submitted
				formData.append('username', newUser.username);
				formData.append('email', newUser.email);
				formData.append('role', 'User'); 
				return async ({ result, update }) => {
					// `result` is an `ActionResult` object
					// `update` is a function which triggers the default logic that would be triggered if this callback wasn't set
					if (result.type === 'failure') {
						console.error('Error adding user', result);
						setActionFailure(typeof result.data?.message === 'string' ? result.data.message : 'Failed to add user.');
					} else if (result.type === 'success') {
						
						if (result.data) {
							setActionSuccess('User added successfully!');

							const userData = result.data as { newUser: { id: number, username: string, email: string } };

							users.push({
								id: userData.newUser.id,
								username: userData.newUser.username,
								email: userData.newUser.email
							});
						} else {
							setActionFailure('Failed to add user. No data returned.');
						}
					} else {
						console.error('Unexpected result type', result);
						setActionFailure('Unexpected error.');
					}

					addingUser = false; // Close the form after submission
					newUser = { username: '', email: '' }; // Reset form fields

					update(); // Trigger the default form update logic					
				};
			}}>
				<div class="mb-4">
					<label for="username" class="block text-sm font-medium text-gray-700">Username</label>
					<input
						id="username"
						type="text"
						bind:value={newUser.username}
						class="mt-1 block w-full rounded border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
						placeholder="Enter username"
					/>
				</div>

				<div class="mb-4">
					<label for="email" class="block text-sm font-medium text-gray-700">Email</label>
					<input
						id="email"
						type="email"
						bind:value={newUser.email}
						class="mt-1 block w-full rounded border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
						placeholder="Enter email"
					/>
				</div>
				
				<div class="flex justify-end">
					<button
						type="button"
						class="mr-2 rounded bg-gray-300 px-4 py-2 text-gray-700 transition hover:bg-gray-400"
						onclick={() => { addingUser = false; }}
					>
						Cancel
					</button>

					<button
						type="submit"
						class="rounded bg-blue-600 px-4 py-2 text-white transition hover:bg-blue-700"
					>
						Add User
					</button>
				</div>
			</form>
		</div>
	</div>
{/if}

{#if editingUser !== null}
	<div class="fixed inset-0 z-50 flex items-center justify-center bg-gray-900/50">
		<div class="bg-white p-6 rounded-lg shadow-md max-w-md w-full">
			<h2 class="mb-4 text-xl font-semibold text-gray-700">Edit User</h2>
			<form action="?/update" method="POST" use:enhance={({ formData, cancel }) => {
				if (!editingUser) {
					setActionFailure('No user selected for editing.');
					cancel();
					return;
				}
				formData.append('id', editingUser.id.toString());
				formData.append('username', editingUser.username);
				formData.append('email', editingUser.email);
				formData.append('role', 'User'); 
				return async ({ result, update }) => {
					if (result.type === 'failure') {
						console.error('Error editing user', result);
						setActionFailure(typeof result.data?.message === 'string' ? result.data.message : 'Failed to edit user.');
					} else if (result.type === 'success') {
						
						if (result.data) {
							setActionSuccess('User edited successfully!');

							const userData = result.data as { updatedUser: { id: number, username: string, email: string } };

							const idx = users.findIndex(u => u.id === userData.updatedUser.id);
							if (idx !== -1) {
								users[idx] = {
									id: userData.updatedUser.id,
									username: userData.updatedUser.username,
									email: userData.updatedUser.email
								};
							} else {
								setActionFailure(`User with ID ${userData.updatedUser.id} not found in the list.`);
							}
						} else {
							setActionFailure('Failed to edit user. No data returned.');
						}
					} else {
						console.error('Unexpected result type', result);
						setActionFailure('Unexpected error.');
					}

					editingUser = null; // Close the form after submission and reset editingUser data

					update();			
				};
			}}>
				<div class="mb-4">
					<label for="edit-username" class="block text-sm font-medium text-gray-700">Username</label>
					<input
						id="edit-username"
						type="text"
						bind:value={editingUser.username}
						class="mt-1 block w-full rounded border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
						placeholder="Enter username"
					/>
				</div>
				<div class="mb-4">
					<label for="edit-email" class="block text-sm font-medium text-gray-700">Email</label>
					<input
						id="edit-email"
						type="email"
						bind:value={editingUser.email}
						class="mt-1 block w-full rounded border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
						placeholder="Enter email"
					/>
				</div>
				<div class="flex justify-end">
					<button
						type="button"
						class="mr-2 rounded bg-gray-300 px-4 py-2 text-gray-700 transition hover:bg-gray-400"
						onclick={() => { editingUser = null; }}
					>
						Cancel
					</button>

					<button
						type="submit"
						class="rounded bg-blue-600 px-4 py-2 text-white transition hover:bg-blue-700"
					>
						Update User
					</button>
				</div>
			</form>
		</div>
	</div>
{/if}

{#if deletingUser !== null}
	<div class="fixed inset-0 z-50 flex items-center justify-center bg-gray-900/50">
		<div class="bg-white p-6 rounded-lg shadow-md max-w-md w-full">
			<h2 class="mb-4 text-xl font-semibold text-gray-700">Delete User</h2>
			<p class="mb-4">Are you sure you want to delete user <strong>{deletingUser.username}</strong>?</p>
			
			<form action="?/delete" method="POST" use:enhance={({ formData, cancel }) => {
				if (!deletingUser) {
					setActionFailure('No user selected for deletion.');
					cancel();
					return;
				}
				formData.append('id', deletingUser.id.toString());

				return async ({ result, update }) => {
					if (result.type === 'failure') {
						console.error('Error deleting user', result);
						setActionFailure(typeof result.data?.message === 'string' ? result.data.message : 'Failed to delete user.');
					} else if (result.type === 'success') {
						
						if (result.data) {
							setActionSuccess('User deleted successfully!');

							const deletedId = Number(result.data.deletedUserId);
							const idx = users.findIndex(user => user.id === deletedId);
							if (idx !== -1) {
								users.splice(idx, 1);
							} else {
								setActionFailure(`User with ID ${deletedId} not found in the list.`);
							}
						} else {
							setActionFailure('Failed to delete user. No data returned.');
						}
					} else {
						console.error('Unexpected result type', result);
						setActionFailure('Unexpected error.');
					}

					deletingUser = null; // Close the form after submission and reset deletingUser data

					update();			
				};
			}}>
				<div class="flex justify-end">
					<button
						type="button"
						class="mr-2 rounded bg-gray-300 px-4 py-2 text-gray-700 transition hover:bg-gray-400"
						onclick={() => { deletingUser = null; }}
					>
						Cancel
					</button>

					<button
						type="submit"
						class="rounded bg-red-600 px-4 py-2 text-white transition hover:bg-red-700"
					>
						Delete User
					</button>
				</div>
			</form>
		</div>
	</div>
{/if}


<div class="container mx-auto px-4 py-8">
	<Header data={{title: "Users", description: "Manage your system users here."}} />

    <div class="bg-white rounded-lg shadow-md p-6 mb-6">
        <div class="flex justify-between items-center mb-4">
            <h2 class="text-xl font-semibold text-gray-700">User List</h2>
            <button
                class="rounded bg-blue-600 px-4 py-2 text-white shadow transition hover:bg-blue-700"
                onclick={()=> {addingUser = true;}}
            >
                + Add User
            </button>
        </div>

        <div class="overflow-x-auto">
            <table class="min-w-full divide-y divide-gray-200">
                <thead class="bg-gray-50">
                    <tr>
                        <th
                            class="px-6 py-3 text-left text-xs font-medium tracking-wider text-gray-500 uppercase"
                            >ID</th
                        >
                        <th
                            class="px-6 py-3 text-left text-xs font-medium tracking-wider text-gray-500 uppercase"
                            >Name</th
                        >
                        <th
                            class="px-6 py-3 text-left text-xs font-medium tracking-wider text-gray-500 uppercase"
                            >Email</th
                        >
                        <th
                            class="px-6 py-3 text-left text-xs font-medium tracking-wider text-gray-500 uppercase"
                            >Role</th
                        >
                        <th class="px-6 py-3"></th>
                    </tr>
                </thead>
                {#if data.error}
                    <tbody class="bg-red-100">
                        <tr>
                            <td colspan="5" class="px-6 py-4 text-center text-red-600">
                                {data.error}
                            </td>
                        </tr>
                    </tbody>
                {:else if users.length === 0}
                    <tbody class="bg-gray-100">
                        <tr>
                            <td colspan="5" class="px-6 py-4 text-center text-gray-500">
                                No users found.
                            </td>
                        </tr>
                    </tbody>
                {:else}
                <tbody class="divide-y divide-gray-200 bg-white">
                    {#each users as user (user.id)}
                        <tr transition:fade>
                            <td class="px-6 py-4 whitespace-nowrap">{user.id}</td>
                            <td class="px-6 py-4 whitespace-nowrap">{user.username}</td>
                            <td class="px-6 py-4 whitespace-nowrap">{user.email}</td>
                            <td class="px-6 py-4 whitespace-nowrap">
                                <span class="px-2 py-1 rounded-full text-xs bg-blue-100 text-blue-800">User</span>
                            </td>
                            <td class="flex gap-2 px-6 py-4 whitespace-nowrap">
                                <button
                                    class="rounded bg-yellow-400 px-3 py-1 text-white transition hover:bg-yellow-500"
                                    onclick={() => editUser(user.id)}>Edit</button
                                >
                                <button
                                    class="rounded bg-red-500 px-3 py-1 text-white transition hover:bg-red-600"
                                    onclick={() => deleteUser(user.id)}>Delete</button
                                >
                            </td>
                        </tr>
                    {/each}
                </tbody>
                {/if}
            </table>
        </div>
    </div>

</div>

<Footer />
