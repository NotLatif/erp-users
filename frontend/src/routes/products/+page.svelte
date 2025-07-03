<script lang="ts">
	import { enhance } from '$app/forms';
	import Footer from '$lib/components/Footer.svelte';
	import Header from '$lib/components/Header.svelte';
	import type { Product } from '$lib/types/inventory.js';
	import { fade } from 'svelte/transition';


    let { data } = $props();

    let products : Product[] = $state<Product[]>(data.products || []);

	let addingProduct = $state(false);
	let newProduct = $state({
		id: 0,
		name: '',
		price: 0,
		sku: '',
		description: '',
	});

	let editingProduct: Product | null = $state(null);
	let deletingProduct: Product | null = $state(null);


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

    function addProduct() {
        newProduct = {
			id: 0,
			name: '',
			price: 0,
			sku: '',
			description: '',
		};

        addingProduct = true;
    }

	function editProduct(id: number) {
		const found = products.find(p => p.id === id);
		if (found) editingProduct = { ...found };
		else setActionFailure("Product not found.");
	}
	function deleteProduct(id: number) {
		const found = products.find(p => p.id === id);
		if (found) deletingProduct = { ...found };
		else setActionFailure("Product not found.");
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


{#if addingProduct}
	<div class="fixed inset-0 z-50 flex items-center justify-center bg-gray-900/50">
		<div class="bg-white p-6 rounded-lg shadow-md max-w-md w-full">
			<h2 class="mb-4 text-xl font-semibold text-gray-700">Add New Product</h2>
			<form action="?/create" method="POST" use:enhance={({ formData }) => {
				formData.append('name', newProduct.name);
				formData.append('price', newProduct.price.toString());
				formData.append('sku', newProduct.sku);
				formData.append('description', newProduct.description);

				return async ({ result, update }) => {
                    console.log(result);
					if (result.type === 'failure') {
						console.error('Error adding product', result);
						setActionFailure(typeof result.data?.error === 'string' ? result.data.error : 'Failed to add product.');
					} else if (result.type === 'error') {
                        console.error('Unexpected error', result);
                        setActionFailure('Unexpected error. Please try again later.');
                    } else if (result.type === 'success') {

						if (result.data) {
							setActionSuccess('Product added successfully!');

							const productData = result.data as { newProduct: Product };

							console.log('New product data:', productData);

							products.push({
                                id: productData.newProduct.id,
                                name: productData.newProduct.name,
                                sku: productData.newProduct.sku,
                                description: productData.newProduct.description,
                                price: productData.newProduct.price
							});
						} else {
							setActionFailure('Failed to add product. No data returned.');
						}
					} else {
						console.error('Unexpected result type', result);
						setActionFailure('Unexpected error.');
					}

					addingProduct = false; // Close the form after submission
					newProduct = { id: 0, name: '', price: 0, sku: '', description: '' }; // Reset form fields

					update(); // Trigger the default form update logic					
				};
			}}>
                <div class="mb-4">
					<label for="name" class="block text-sm font-medium text-gray-700">Name</label>
					<input
						id="name"
						type="text"
						bind:value={newProduct.name}
						class="mt-1 block w-full rounded border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
						placeholder="Enter product name"
					/>
				</div>
                <div class="mb-4">
					<label for="sku" class="block text-sm font-medium text-gray-700">SKU</label>
					<input
						id="sku"
						type="text"
						bind:value={newProduct.sku}
						class="mt-1 block w-full rounded border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
						placeholder="Enter product SKU"
					/>
				</div>
                <div class="mb-4">
					<label for="price" class="block text-sm font-medium text-gray-700">Price</label>
					<input
						id="price"
						type="text"
						bind:value={newProduct.price}
						class="mt-1 block w-full rounded border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
						placeholder="Enter product price"
					/>
				</div>
                <div class="mb-4">
					<label for="description" class="block text-sm font-medium text-gray-700">Description</label>
					<input
						id="description"
						type="text"
						bind:value={newProduct.description}
						class="mt-1 block w-full rounded border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
						placeholder="Enter product description"
					/>
				</div>

				
				
				<div class="flex justify-end">
					<button
						type="button"
						class="mr-2 rounded bg-gray-300 px-4 py-2 text-gray-700 transition hover:bg-gray-400"
						onclick={() => { addingProduct = false; }}
					>
						Cancel
					</button>

					<button
						type="submit"
						class="rounded bg-blue-600 px-4 py-2 text-white transition hover:bg-blue-700"
					>
						Add Product
					</button>
				</div>
			</form>
		</div>
	</div>
{/if}

{#if editingProduct}
<div class="fixed inset-0 z-50 flex items-center justify-center bg-gray-900/50">
		<div class="bg-white p-6 rounded-lg shadow-md max-w-md w-full">
			<h2 class="mb-4 text-xl font-semibold text-gray-700">Edit Product</h2>
			<form action="?/edit" method="POST" use:enhance={({ formData }) => {
                if (!editingProduct) {
                    setActionFailure('No product selected for editing.');
                    return;
                }
                formData.append('id', editingProduct.id.toString());
				formData.append('name', editingProduct.name);
				formData.append('price', editingProduct.price.toString());
				formData.append('sku', editingProduct.sku);
				formData.append('description', editingProduct.description);
				

				return async ({ result, update }) => {
                    console.log(result);
					if (result.type === 'failure') {
						console.error('Error editing product', result);
						setActionFailure(typeof result.data?.error === 'string' ? result.data.error : 'Failed to edit product.');
					} else if (result.type === 'error') {
                        console.error('Unexpected error', result);
                        setActionFailure('Unexpected error. Please try again later.');
                    } else if (result.type === 'success') {

						if (result.data) {
							setActionSuccess('Product edited successfully!');

							const productData = result.data as { updatedProduct: Product };

                            const idx = products.findIndex(p => p.id === productData.updatedProduct.id);
                            if (idx !== -1) {
                                products[idx] = {
                                    id: productData.updatedProduct.id,
                                    name: productData.updatedProduct.name,
                                    sku: productData.updatedProduct.sku,
                                    description: productData.updatedProduct.description,
                                    price: productData.updatedProduct.price
                                }
                            } else {
                                setActionFailure(`Product with ID ${productData.updatedProduct.id} not found in the list.`);
                            }

						} else {
							setActionFailure('Failed to edit product. No data returned.');
						}
					} else {
						console.error('Unexpected result type', result);
						setActionFailure('Unexpected error.');
					}

					editingProduct = null;; // Close the form after submission

					update(); // Trigger the default form update logic					
				};
			}}>
                <div class="mb-4">
					<label for="edit-name" class="block text-sm font-medium text-gray-700">Name</label>
					<input
						id="edit-name"
						type="text"
						bind:value={editingProduct.name}
						class="mt-1 block w-full rounded border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
						placeholder="Enter product name"
					/>
				</div>
                <div class="mb-4">
					<label for="edit-sku" class="block text-sm font-medium text-gray-700">SKU</label>
					<input
						id="edit-sku"
						type="text"
						bind:value={editingProduct.sku}
						class="mt-1 block w-full rounded border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
						placeholder="Enter product SKU"
					/>
				</div>
                <div class="mb-4">
					<label for="edit-price" class="block text-sm font-medium text-gray-700">Price</label>
					<input
						id="edit-price"
						type="text"
						bind:value={editingProduct.price}
						class="mt-1 block w-full rounded border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
						placeholder="Enter product price"
					/>
				</div>
                <div class="mb-4">
					<label for="edit-description" class="block text-sm font-medium text-gray-700">Description</label>
					<input
						id="edit-description"
						type="text"
						bind:value={editingProduct.description}
						class="mt-1 block w-full rounded border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
						placeholder="Enter product description"
					/>
				</div>

				<div class="flex justify-end">
					<button
						type="button"
						class="mr-2 rounded bg-gray-300 px-4 py-2 text-gray-700 transition hover:bg-gray-400"
						onclick={() => { editingProduct = null; }}
					>
						Cancel
					</button>

					<button
						type="submit"
						class="rounded bg-blue-600 px-4 py-2 text-white transition hover:bg-blue-700"
					>
						Update Product
					</button>
				</div>
			</form>
		</div>
	</div>
{/if}

{#if deletingProduct}
    <div class="fixed inset-0 z-50 flex items-center justify-center bg-gray-900/50">
        <div class="bg-white p-6 rounded-lg shadow-md max-w-md w-full">
            <h2 class="mb-4 text-xl font-semibold text-gray-700">Delete Product</h2>
            <p class="mb-4">Are you sure you want to delete <strong>{deletingProduct.name}</strong> [{deletingProduct.sku}]?</p>
            <form action="?/delete" method="POST" use:enhance={({ formData }) => {
                if (!deletingProduct) {
                    setActionFailure('No product selected for deletion.');
                    return;
                }
                formData.append('id', deletingProduct.id.toString());
                return async ({ result, update }) => {
                    if (result.type === 'failure') {
                        console.error('Error deleting product', result);
                        setActionFailure(typeof result.data?.error === 'string' ? result.data.error : 'Failed to delete product.');
                    } else if (result.type === 'error') {
                        console.error('Unexpected error', result);
                        setActionFailure('Unexpected error. Please try again later.');
                    } else if (result.type === 'success') {

                        if (result.data) {
                            setActionSuccess('Product deleted successfully!');

                            const productData = result.data as { deletedProductId: number };

                            const idx = products.findIndex(p => p.id === productData.deletedProductId);
                            if (idx !== -1) {
                                products.splice(idx, 1);
                            } else {
                                setActionFailure(`Product with ID ${productData.deletedProductId} not found in the list.`);
                            }
                        } else {
                            setActionFailure('Failed to delete product. No data returned.');
                        }
                    } else {
                        console.error('Unexpected result type', result);
                        setActionFailure('Unexpected error.');
                    }

                    deletingProduct = null; // Close the form after submission

                    update(); // Trigger the default form update logic					
                };
            }}>
                <div class="flex justify-end">
                    <button
                        type="button"
                        class="mr-2 rounded bg-gray-300 px-4 py-2 text-gray-700 transition hover:bg-gray-400"
                        onclick={() => { deletingProduct = null; }}
                    >
                        Cancel
                    </button>

                    <button
                        type="submit"
                        class="rounded bg-red-600 px-4 py-2 text-white transition hover:bg-red-700"
                    >
                        Delete Product
                    </button>
                </div>
            </form>
        </div>
    </div>
{/if}

<div class="container mx-auto px-4 py-8">
    <Header data={{title: "Products", description: "Manage your products here."}} />

    <div class="bg-white rounded-lg shadow-md p-6 mb-6">
        <div class="flex justify-between items-center mb-4">
            <h2 class="text-xl font-semibold text-gray-700">Product List</h2>
            <button 
                class="rounded bg-blue-600 px-4 py-2 text-white shadow transition hover:bg-blue-700" 
                onclick={() => addProduct()}>
                + Add Product
            </button>
        </div>
        
        <div class="overflow-x-auto">
            <table class="min-w-full divide-y divide-gray-200">
                <thead class="bg-gray-50">
                    <tr>
                        <th class="px-6 py-3 text-left text-xs font-medium tracking-wider text-gray-500 uppercase">ID</th>
                        <th class="px-6 py-3 text-left text-xs font-medium tracking-wider text-gray-500 uppercase">SKU</th>
                        <th class="px-6 py-3 text-left text-xs font-medium tracking-wider text-gray-500 uppercase">Name</th>
                        <th class="px-6 py-3 text-left text-xs font-medium tracking-wider text-gray-500 uppercase">Price</th>
                        <th class="px-6 py-3 text-left text-xs font-medium tracking-wider text-gray-500 uppercase">Description</th>
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
                {:else if products.length === 0}
                    <tbody class="bg-gray-100">
                        <tr>
                            <td colspan="6" class="px-6 py-4 text-center text-gray-500">No products found.</td>
                        </tr>
                    </tbody>
                {:else}
                    <tbody class="divide-y divide-gray-200 bg-white">
                        {#each products as product (product.id)}
                            <tr>
                                <td class="px-6 py-4 whitespace-nowrap">{product.id}</td>
                                <td class="px-6 py-4 whitespace-nowrap">{product.sku}</td>
                                <td class="px-6 py-4 whitespace-nowrap">{product.name}</td>
                                <td class="px-6 py-4 whitespace-nowrap">${product.price.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}</td>
                                <td class="px-6 py-4 whitespace-nowrap">{product.description}</td>
                                
                                <td class="flex gap-2 px-6 py-4 whitespace-nowrap">
                                    <button class="rounded bg-yellow-400 px-3 py-1 text-white transition hover:bg-yellow-500" onclick={() => editProduct(product.id)}>Edit</button>
                                    <button class="rounded bg-red-500 px-3 py-1 text-white transition hover:bg-red-600" onclick={() => deleteProduct(product.id)}>Delete</button>
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
