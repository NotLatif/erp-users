<script lang="ts">
	import { enhance } from '$app/forms';
	import Footer from '$lib/components/Footer.svelte';
	import Header from '$lib/components/Header.svelte';
	import type { Warehouse } from '$lib/types/inventory.js';
	import { fade } from 'svelte/transition';


    let { data } = $props();

    let warehouses : Warehouse[] = $state<Warehouse[]>(data.warehouses || []);

	let addingWarehouse = $state(false);
	let newWarehouse = $state({
		id: 0,
		name: '',
		location: '',
	});

	let editingWarehouse: Warehouse | null = $state(null);
	let deletingWarehouse: Warehouse | null = $state(null);


	let actionSuccess = $state("");
	let actionFailure = $state("");

	function setActionSuccess(message: string) {
		actionSuccess = message;
		setTimeout(() => actionSuccess = "", 2000);
	}
	function setActionFailure(message: string) {
		actionFailure = message;
		setTimeout(() => actionFailure = "", 2000);
	}

    function addWarehouse() {
        newWarehouse = {
			id: 0,
			name: '',
			location: '',
		};

        addingWarehouse = true;
    }

	function editWarehouse(id: number) {
		const found = warehouses.find(w => w.id === id);
		if (found) editingWarehouse = { ...found };
		else setActionFailure("Warehouse not found.");
	}
	function deleteWarehouse(id: number) {
		const found = warehouses.find(w => w.id === id);
		if (found) deletingWarehouse = { ...found };
		else setActionFailure("Warehouse not found.");
	}
</script>

{#if actionSuccess}
	<div transition:fade class="fixed bottom-4 right-4 z-50 bg-green-500 text-white px-4 py-2 rounded-lg shadow-md">
		{actionSuccess}
	</div>
{/if}
{#if actionFailure}
	<div transition:fade class="fixed bottom-4 right-4 z-50 bg-red-500 text-white px-4 py-2 rounded-lg shadow-md">
		{actionFailure}
	</div>
{/if}


{#if addingWarehouse}
	<div class="fixed inset-0 z-50 flex items-center justify-center bg-gray-900/50">
		<div class="bg-white p-6 rounded-lg shadow-md max-w-md w-full">
			<h2 class="mb-4 text-xl font-semibold text-gray-700">Add New Warehouse</h2>
			<form action="?/create" method="POST" use:enhance={({ formData }) => {
				formData.append('name', newWarehouse.name);
				formData.append('location', newWarehouse.location);

				return async ({ result, update }) => {
                    console.log(result);
					if (result.type === 'failure') {
						console.error('Error adding warehouse', result);
						setActionFailure(typeof result.data?.error === 'string' ? result.data.error : 'Failed to add warehouse.');
					} else if (result.type === 'error') {
                        console.error('Unexpected error', result);
                        setActionFailure('Unexpected error. Please try again later.');
                    } else if (result.type === 'success') {

						if (result.data) {
							setActionSuccess('Warehouse added successfully!');

							const warehouseData = result.data as { newWarehouse: Warehouse };

							console.log('New warehouse data:', warehouseData);

							warehouses.push({
                                id: warehouseData.newWarehouse.id,
                                name: warehouseData.newWarehouse.name,
                                location: warehouseData.newWarehouse.location
							});
						} else {
							setActionFailure('Failed to add warehouse. No data returned.');
						}
					} else {
						console.error('Unexpected result type', result);
						setActionFailure('Unexpected error.');
					}

					addingWarehouse = false; // Close the form after submission
					newWarehouse = { id: 0, name: '', location: '' }; // Reset form fields

					update(); // Trigger the default form update logic					
				};
			}}>
                <div class="mb-4">
					<label for="name" class="block text-sm font-medium text-gray-700">Name</label>
					<input
						id="name"
						type="text"
						bind:value={newWarehouse.name}
						class="mt-1 block w-full rounded border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
						placeholder="Enter warehouse name"
					/>
				</div>
                <div class="mb-4">
					<label for="location" class="block text-sm font-medium text-gray-700">Location</label>
					<input
						id="location"
						type="text"
						bind:value={newWarehouse.location}
						class="mt-1 block w-full rounded border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
						placeholder="Enter warehouse location"
					/>
				</div>
				
				<div class="flex justify-end">
					<button
						type="button"
						class="mr-2 rounded bg-gray-300 px-4 py-2 text-gray-700 transition hover:bg-gray-400"
						onclick={() => { addingWarehouse = false; }}
					>
						Cancel
					</button>

					<button
						type="submit"
						class="rounded bg-blue-600 px-4 py-2 text-white transition hover:bg-blue-700"
					>
						Add Warehouse
					</button>
				</div>
			</form>
		</div>
	</div>
{/if}

{#if editingWarehouse}
<div class="fixed inset-0 z-50 flex items-center justify-center bg-gray-900/50">
		<div class="bg-white p-6 rounded-lg shadow-md max-w-md w-full">
			<h2 class="mb-4 text-xl font-semibold text-gray-700">Edit Warehouse</h2>
			<form action="?/edit" method="POST" use:enhance={({ formData }) => {
                if (!editingWarehouse) {
                    setActionFailure('No warehouse selected for editing.');
                    return;
                }
                formData.append('id', editingWarehouse.id.toString());
				formData.append('name', editingWarehouse.name);
				formData.append('location', editingWarehouse.location);
				

				return async ({ result, update }) => {
                    console.log(result);
					if (result.type === 'failure') {
						console.error('Error editing warehouse', result);
						setActionFailure(typeof result.data?.error === 'string' ? result.data.error : 'Failed to edit warehouse.');
					} else if (result.type === 'error') {
                        console.error('Unexpected error', result);
                        setActionFailure('Unexpected error. Please try again later.');
                    } else if (result.type === 'success') {

						if (result.data) {
							setActionSuccess('Warehouse edited successfully!');

							const warehouseData = result.data as { updatedWarehouse: Warehouse };

                            const idx = warehouses.findIndex(w => w.id === warehouseData.updatedWarehouse.id);
                            if (idx !== -1) {
                                warehouses[idx] = {
                                    id: warehouseData.updatedWarehouse.id,
                                    name: warehouseData.updatedWarehouse.name,
                                    location: warehouseData.updatedWarehouse.location
                                }
                            } else {
                                setActionFailure(`Warehouse with ID ${warehouseData.updatedWarehouse.id} not found in the list.`);
                            }
						} else {
							setActionFailure('Failed to edit warehouse. No data returned.');
						}
					} else {
						console.error('Unexpected result type', result);
						setActionFailure('Unexpected error.');
					}

					editingWarehouse = null; // Close the form after submission

					update(); // Trigger the default form update logic					
				};
			}}>
                <div class="mb-4">
					<label for="edit-name" class="block text-sm font-medium text-gray-700">Name</label>
					<input
						id="edit-name"
						type="text"
						bind:value={editingWarehouse.name}
						class="mt-1 block w-full rounded border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
						placeholder="Enter warehouse name"
					/>
				</div>
                <div class="mb-4">
					<label for="edit-location" class="block text-sm font-medium text-gray-700">Location</label>
					<input
						id="edit-location"
						type="text"
						bind:value={editingWarehouse.location}
						class="mt-1 block w-full rounded border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
						placeholder="Enter warehouse location"
					/>
				</div>

				<div class="flex justify-end">
					<button
						type="button"
						class="mr-2 rounded bg-gray-300 px-4 py-2 text-gray-700 transition hover:bg-gray-400"
						onclick={() => { editingWarehouse = null; }}
					>
						Cancel
					</button>

					<button
						type="submit"
						class="rounded bg-blue-600 px-4 py-2 text-white transition hover:bg-blue-700"
					>
						Update Warehouse
					</button>
				</div>
			</form>
		</div>
	</div>
{/if}

{#if deletingWarehouse}
    <div class="fixed inset-0 z-50 flex items-center justify-center bg-gray-900/50">
        <div class="bg-white p-6 rounded-lg shadow-md max-w-md w-full">
            <h2 class="mb-4 text-xl font-semibold text-gray-700">Delete Warehouse</h2>
            <p class="mb-4">Are you sure you want to delete <strong>{deletingWarehouse.name}</strong> in {deletingWarehouse.location}?</p>
            <form action="?/delete" method="POST" use:enhance={({ formData }) => {
                if (!deletingWarehouse) {
                    setActionFailure('No warehouse selected for deletion.');
                    return;
                }
                formData.append('id', deletingWarehouse.id.toString());
                return async ({ result, update }) => {
                    if (result.type === 'failure') {
                        console.error('Error deleting warehouse', result);
                        setActionFailure(typeof result.data?.error === 'string' ? result.data.error : 'Failed to delete warehouse.');
                    } else if (result.type === 'error') {
                        console.error('Unexpected error', result);
                        setActionFailure('Unexpected error. Please try again later.');
                    } else if (result.type === 'success') {

                        if (result.data) {
                            setActionSuccess('Warehouse deleted successfully!');

                            const warehouseData = result.data as { deletedWarehouseId: number };

                            const idx = warehouses.findIndex(w => w.id === warehouseData.deletedWarehouseId);
                            if (idx !== -1) {
                                warehouses.splice(idx, 1);
                            } else {
                                setActionFailure(`Warehouse with ID ${warehouseData.deletedWarehouseId} not found in the list.`);
                            }
                        } else {
                            setActionFailure('Failed to delete warehouse. No data returned.');
                        }
                    } else {
                        console.error('Unexpected result type', result);
                        setActionFailure('Unexpected error.');
                    }

                    deletingWarehouse = null; // Close the form after submission

                    update(); // Trigger the default form update logic					
                };
            }}>
                <div class="flex justify-end">
                    <button
                        type="button"
                        class="mr-2 rounded bg-gray-300 px-4 py-2 text-gray-700 transition hover:bg-gray-400"
                        onclick={() => { deletingWarehouse = null; }}
                    >
                        Cancel
                    </button>

                    <button
                        type="submit"
                        class="rounded bg-red-600 px-4 py-2 text-white transition hover:bg-red-700"
                    >
                        Delete Warehouse
                    </button>
                </div>
            </form>
        </div>
    </div>
{/if}

<div class="container mx-auto px-4 py-8">
	<Header data={{ title: "Warehouses", description: "Manage your warehouses here." }} />

    <div class="bg-white rounded-lg shadow-md p-6 mb-6">
        <div class="flex justify-between items-center mb-4">
            <h2 class="text-xl font-semibold text-gray-700">Warehouse List</h2>
            <button 
                class="rounded bg-blue-600 px-4 py-2 text-white shadow transition hover:bg-blue-700" 
                onclick={() => addWarehouse()}>
                + Add Warehouse
            </button>
        </div>
        
        <div class="overflow-x-auto">
            <table class="min-w-full divide-y divide-gray-200">
                <thead class="bg-gray-50">
                    <tr>
                        <th class="px-6 py-3 text-left text-xs font-medium tracking-wider text-gray-500 uppercase">ID</th>
                        <th class="px-6 py-3 text-left text-xs font-medium tracking-wider text-gray-500 uppercase">Name</th>
                        <th class="px-6 py-3 text-left text-xs font-medium tracking-wider text-gray-500 uppercase">Location</th>
                        <th class="px-6 py-3"></th>
                    </tr>
                </thead>
                {#if data.error }
                    <tbody class="bg-red-100">
                        <tr>
                            <td colspan="7" class="px-6 py-4 text-center text-red-600">
                                {data.error}
                            </td>
                        </tr>
                    </tbody>
                {:else if warehouses.length === 0}
                    <tbody class="bg-gray-100">
                        <tr>
                            <td colspan="4" class="px-6 py-4 text-center text-gray-500">No warehouses found.</td>
                        </tr>
                    </tbody>
                {:else}
                    <tbody class="divide-y divide-gray-200 bg-white">
                        {#each warehouses as warehouse (warehouse.id)}
                            <tr>
                                <td class="px-6 py-4 whitespace-nowrap">{warehouse.id}</td>
                                <td class="px-6 py-4 whitespace-nowrap">{warehouse.name}</td>
                                <td class="px-6 py-4 whitespace-nowrap">{warehouse.location}</td>
                                <td class="flex gap-2 px-6 py-4 whitespace-nowrap">
                                    <button class="rounded bg-yellow-400 px-3 py-1 text-white transition hover:bg-yellow-500" onclick={() => editWarehouse(warehouse.id)}>Edit</button>
                                    <button class="rounded bg-red-500 px-3 py-1 text-white transition hover:bg-red-600" onclick={() => deleteWarehouse(warehouse.id)}>Delete</button>
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
