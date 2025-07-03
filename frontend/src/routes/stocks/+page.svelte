<script lang="ts">
	import { enhance } from '$app/forms';
	import Footer from '$lib/components/Footer.svelte';
	import type { Product, StockEntry, Warehouse } from '$lib/types/inventory.js';
	import { fade } from 'svelte/transition';


    let { data } = $props();

	let stocks : StockEntry[] = $state<StockEntry[]>(data.stockEntries || []);

    let warehouses : Warehouse[] = $state<Warehouse[]>(data.warehouses || []);
    let products : Product[] = $state<Product[]>(data.products || []);

	let addingStock = $state(false);
	let newStockEntry = $state({
		product: { id: 0 },
		warehouse: { id: 0 },
		quantity: 0,
	});

	let editingStock: StockEntry | null = $state(null);
	let deletingStock: StockEntry | null = $state(null);


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

    function addStock() {
        newStockEntry = {
            product: { id: 0 },
            warehouse: { id: 0 },
            quantity: 0,
        };

        addingStock = true;
    }

	function editStock(id: number) {
		const found = stocks.find(s => s.id === id);
		if (found) editingStock = { ...found };
		else setActionFailure("Stock not found.");
	}
	function deleteStock(id: number) {
		const found = stocks.find(s => s.id === id);
		if (found) deletingStock = { ...found };
		else setActionFailure("Stock not found.");
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


{#if addingStock}
	<div class="fixed inset-0 z-50 flex items-center justify-center bg-gray-900/50">
		<div class="bg-white p-6 rounded-lg shadow-md max-w-md w-full">
			<h2 class="mb-4 text-xl font-semibold text-gray-700">Add New Stock Entry</h2>
			<form action="?/add" method="POST" use:enhance={({ formData }) => {
				formData.append('quantity', newStockEntry.quantity.toString());
				formData.append('product_id', newStockEntry.product.id.toString());
				formData.append('warehouse_id', newStockEntry.warehouse.id.toString());
				return async ({ result, update }) => {
                    console.log(result);
					if (result.type === 'failure') {
						console.error('Error adding stock', result);
						setActionFailure(typeof result.data?.error === 'string' ? result.data.error : 'Failed to add stock.');
					} else if (result.type === 'error') {
                        console.error('Unexpected error', result);
                        setActionFailure('Unexpected error. Please try again later.');
                    } else if (result.type === 'success') {

						if (result.data) {
							setActionSuccess('Stock added successfully!');
							// Additional logic when there's data
						} else {
							setActionFailure('Failed to add stock. No data returned.');
						}
					} else {
						console.error('Unexpected result type', result);
						setActionFailure('Unexpected error.');
					}

					addingStock = false; // Close the form after submission
					newStockEntry = { quantity: 0, product: { id: 0 }, warehouse: { id: 0 } }; // Reset form fields

					update(); // Trigger the default form update logic					
				};
			}}>
                <div class="mb-4">
					<label for="warehouse_id" class="block text-sm font-medium text-gray-700">Warehouse</label>
                    <select
                        id="warehouse_id"
                        bind:value={newStockEntry.warehouse.id}
                        class="mt-1 block w-full rounded border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
                    >
                        <option value="" disabled selected>Select Warehouse</option>
                        {#each warehouses as warehouse}
                            <option value={warehouse.id}>{warehouse.name} ({warehouse.location})</option>
                        {/each}
                    </select>
				</div>

				<div class="mb-4">
					<label for="product_id" class="block text-sm font-medium text-gray-700">Product</label>
					<select
                        id="product_id"
                        bind:value={newStockEntry.product.id}
                        class="mt-1 block w-full rounded border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
                    >
                        <option value="" disabled selected>Select Product</option>
                        {#each products as product}
                            <option value={product.id}>[{product.sku}] {product.name}</option>
                        {/each}
                    </select>
				</div>

				<div class="mb-4">
					<label for="quantity" class="block text-sm font-medium text-gray-700">Quantity</label>
					<input
						id="quantity"
						type="text"
						bind:value={newStockEntry.quantity}
						class="mt-1 block w-full rounded border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
						placeholder="Enter quantity"
					/>
				</div>

				
				
				<div class="flex justify-end">
					<button
						type="button"
						class="mr-2 rounded bg-gray-300 px-4 py-2 text-gray-700 transition hover:bg-gray-400"
						onclick={() => { addingStock = false; }}
					>
						Cancel
					</button>

					<button
						type="submit"
						class="rounded bg-blue-600 px-4 py-2 text-white transition hover:bg-blue-700"
					>
						Add Stock
					</button>
				</div>
			</form>
		</div>
	</div>
{/if}

{#if editingStock}
<div class="fixed inset-0 z-50 flex items-center justify-center bg-gray-900/50">
		<div class="bg-white p-6 rounded-lg shadow-md max-w-md w-full">
			<h2 class="mb-4 text-xl font-semibold text-gray-700">Edit Stock Entry</h2>
			<form action="?/edit" method="POST" use:enhance={({ formData }) => {
                if (!editingStock) {
                    setActionFailure('No stock entry selected for editing.');
                    return;
                }
                formData.append('id', editingStock.id.toString());
				formData.append('quantity', editingStock.quantity.toString());
				formData.append('product_id', editingStock.product.id.toString());
				formData.append('warehouse_id', editingStock.warehouse.id.toString());
				return async ({ result, update }) => {
                    console.log(result);
					if (result.type === 'failure') {
						console.error('Error editing stock', result);
						setActionFailure(typeof result.data?.error === 'string' ? result.data.error : 'Failed to edit stock.');
					} else if (result.type === 'error') {
                        console.error('Unexpected error', result);
                        setActionFailure('Unexpected error. Please try again later.');
                    } else if (result.type === 'success') {

						if (result.data) {
							setActionSuccess('Stock updated successfully!');
							// Additional logic when there's data
						} else {
							setActionFailure('Failed to edit stock. No data returned.');
						}
					} else {
						console.error('Unexpected result type', result);
						setActionFailure('Unexpected error.');
					}

					editingStock = null; // Close the form after submission

					update(); // Trigger the default form update logic					
				};
			}}>
                <div class="mb-4">
					<label for="warehouse_id" class="block text-sm font-medium text-gray-700">Warehouse</label>
                    <select
                        id="warehouse_id"
                        bind:value={editingStock.warehouse.id}
                        class="mt-1 block w-full rounded border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
                    >
                        <option value="" disabled>Select Warehouse</option>
                        {#each warehouses as warehouse}
                            <option value={warehouse.id} selected={warehouse.id === editingStock.warehouse.id}>{warehouse.name} ({warehouse.location})</option>
                        {/each}
                    </select>
				</div>

				<div class="mb-4">
					<label for="product_id" class="block text-sm font-medium text-gray-700">Product</label>
					<select
                        id="product_id"
                        bind:value={editingStock.product.id}
                        class="mt-1 block w-full rounded border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
                    >
                        <option value="" disabled>Select Product</option>
                        {#each products as product}
                            <option value={product.id} selected={product.id === editingStock.product.id}>[{product.sku}] {product.name}</option>
                        {/each}
                    </select>
				</div>

				<div class="mb-4">
					<label for="quantity" class="block text-sm font-medium text-gray-700">Quantity</label>
					<input
						id="quantity"
						type="text"
						bind:value={editingStock.quantity}
						class="mt-1 block w-full rounded border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
						placeholder="Enter quantity"
					/>
				</div>
				
				<div class="flex justify-end">
					<button
						type="button"
						class="mr-2 rounded bg-gray-300 px-4 py-2 text-gray-700 transition hover:bg-gray-400"
						onclick={() => { editingStock = null; }}
					>
						Cancel
					</button>

					<button
						type="submit"
						class="rounded bg-blue-600 px-4 py-2 text-white transition hover:bg-blue-700"
					>
						Update Stock
					</button>
				</div>
			</form>
		</div>
	</div>
{/if}

{#if deletingStock}
    <div class="fixed inset-0 z-50 flex items-center justify-center bg-gray-900/50">
        <div class="bg-white p-6 rounded-lg shadow-md max-w-md w-full">
            <h2 class="mb-4 text-xl font-semibold text-gray-700">Delete Stock Entry</h2>
            <p class="mb-4">Are you sure you want to delete the stock entry for <strong>{deletingStock.product.name}</strong> in <strong>{deletingStock.warehouse.name}</strong>?</p>
            <form action="?/delete" method="POST" use:enhance={({ formData }) => {
                if (!deletingStock) {
                    setActionFailure('No stock entry selected for deletion.');
                    return;
                }
                formData.append('id', deletingStock.id.toString());
                return async ({ result, update }) => {
                    if (result.type === 'failure') {
                        setActionFailure(typeof result.data?.error === 'string' ? result.data.error : 'Failed to delete stock.');
                    } else if (result.type === 'error') {
                        setActionFailure('Unexpected error. Please try again later.');
                    } else if (result.type === 'success') {
                        setActionSuccess('Stock entry deleted successfully!');
                    } else {
                        setActionFailure('Unexpected error.');
                    }

                    deletingStock = null; // Close the form after submission

                    update(); // Trigger the default form update logic					
                };
            }}>
                <div class="flex justify-end">
                    <button
                        type="button"
                        class="mr-2 rounded bg-gray-300 px-4 py-2 text-gray-700 transition hover:bg-gray-400"
                        onclick={() => { deletingStock = null; }}
                    >
                        Cancel
                    </button>

                    <button
                        type="submit"
                        class="rounded bg-red-600 px-4 py-2 text-white transition hover:bg-red-700"
                    >
                        Delete Stock
                    </button>
                </div>
            </form>
        </div>
    </div>
{/if}

<div class="container mx-auto px-4 py-8">
    <div class="mb-8">
        <h1 class="text-3xl font-bold text-gray-800">Stock Management</h1>
        <p class="text-gray-600">Manage inventory across warehouses</p>
    </div>

    <div class="bg-white rounded-lg shadow-md p-6 mb-6">
        <div class="flex justify-between items-center mb-4">
            <h2 class="text-xl font-semibold text-gray-700">Stock List</h2>
            <button 
                class="rounded bg-blue-600 px-4 py-2 text-white shadow transition hover:bg-blue-700" 
                onclick={() => addStock()}>
                + Add Stock
            </button>
        </div>
        
        <div class="overflow-x-auto">
            <table class="min-w-full divide-y divide-gray-200">
                <thead class="bg-gray-50">
                    <tr>
                        <th class="px-6 py-3 text-left text-xs font-medium tracking-wider text-gray-500 uppercase">ID</th>
                        <th class="px-6 py-3 text-left text-xs font-medium tracking-wider text-gray-500 uppercase">Product</th>
                        <th class="px-6 py-3 text-left text-xs font-medium tracking-wider text-gray-500 uppercase">SKU</th>
                        <th class="px-6 py-3 text-left text-xs font-medium tracking-wider text-gray-500 uppercase">Warehouse</th>
                        <th class="px-6 py-3 text-left text-xs font-medium tracking-wider text-gray-500 uppercase">Quantity</th>
                        <th class="px-6 py-3 text-left text-xs font-medium tracking-wider text-gray-500 uppercase">Updated</th>
                        <th class="px-6 py-3"></th>
                    </tr>
                </thead>
                {#if stocks.length === 0}
                    <tbody class="bg-gray-100">
                        <tr>
                            <td colspan="7" class="px-6 py-4 text-center text-gray-500">No stock entries found.</td>
                        </tr>
                    </tbody>
                {:else}
                    <tbody class="divide-y divide-gray-200 bg-white">
                        {#each stocks as stock (stock.id)}
                            <tr>
                                <td class="px-6 py-4 whitespace-nowrap">{stock.id}</td>
                                <td class="px-6 py-4 whitespace-nowrap">{stock.product.name}</td>
                                <td class="px-6 py-4 whitespace-nowrap">{stock.product.sku}</td>
                                <td class="px-6 py-4 whitespace-nowrap">{stock.warehouse.name}</td>
                                <td class="px-6 py-4 whitespace-nowrap">
                                    <span class={`px-2 py-1 rounded-full text-xs ${stock.quantity < 5 ? 'bg-red-100 text-red-800' : 'bg-green-100 text-green-800'}`}>
                                        {stock.quantity}
                                    </span>
                                </td>
                                <td class="px-6 py-4 whitespace-nowrap">{new Date(stock.updatedAt).toLocaleDateString()}</td>
                                <td class="flex gap-2 px-6 py-4 whitespace-nowrap">
                                    <button class="rounded bg-yellow-400 px-3 py-1 text-white transition hover:bg-yellow-500" onclick={() => editStock(stock.id)}>Edit</button>
                                    <button class="rounded bg-red-500 px-3 py-1 text-white transition hover:bg-red-600" onclick={() => deleteStock(stock.id)}>Delete</button>
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
