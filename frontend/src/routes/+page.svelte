<script lang="ts">
	import type { ProductDTO, StockEntryDTO } from '$lib/types/inventory';
	import Footer from '$lib/components/Footer.svelte';
	import { enhance } from '$app/forms';

	let { data } = $props();

	console.log($state.snapshot(data))

	// Mock data for available products
	// We get stocks as they contain product information as well as quantity and other details
	let availableStocks: StockEntryDTO[] = data.stocks;

	let cart: { product: ProductDTO; quantity: number }[] = $state([]);
	let showCart = $state(false);
	


	// Add product to cart
	function addToCart(product: ProductDTO) {
		const existingItem = cart.find(item => item.product.id === product.id);
		if (existingItem) {
			existingItem.quantity += 1;
			cart = [...cart]; // Trigger reactivity
		} else {
			cart = [...cart, { product, quantity: 1 }];
		}
	}

	// Remove product from cart
	function removeFromCart(productId: number) {
		cart = cart.filter(item => item.product.id !== productId);
	}

	// Update product quantity in cart
	function updateCartQuantity(productId: number, newQuantity: number) {
		if (newQuantity < 1) return;
		const stock = availableStocks.find(p => p.id === productId);
		if (stock && newQuantity > stock.quantity) return;
		
		cart = cart.map(item => {
			if (item.product.id === productId) {
				return { ...item, quantity: newQuantity };
			}
			return item;
		});
	}

	// Calculate total price
	let totalPrice = $derived(cart.reduce((sum, item) => sum + item.product.price * item.quantity, 0));

	// Filter products by search query and order by selected sort option
	let searchQuery = $state('');
	let selectedSortOption = $state('name-asc');

	let filteredProducts = $derived.by(() => {
		let filtered = searchQuery
			? availableStocks.filter(p =>
				p.product.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
				p.product.description.toLowerCase().includes(searchQuery.toLowerCase())
			)
			: [...availableStocks];
		
		// Sort products
		if (selectedSortOption === 'name-asc') {
			filtered.sort((a, b) => a.product.name.localeCompare(b.product.name));
		} else if (selectedSortOption === 'name-desc') {
			filtered.sort((a, b) => b.product.name.localeCompare(a.product.name));
		} else if (selectedSortOption === 'price-asc') {
			filtered.sort((a, b) => a.product.price - b.product.price);
		} else if (selectedSortOption === 'price-desc') {
			filtered.sort((a, b) => b.product.price - a.product.price);
		}
		return filtered;
	});

	


</script>

<div class="bg-gradient-to-r from-blue-500 to-indigo-600 py-16 px-4">
	<div class="container mx-auto text-center">
		<h1 class="text-4xl md:text-5xl font-bold text-white mb-4">Random Stuff Store</h1>
	</div>
</div>

<!-- Main content -->
<div class="container mx-auto px-4 py-12">
	<!-- Shopping cart (slide-in panel) -->
	{#if showCart}
		<button
			type="button"
			class="fixed inset-0 bg-white/20 bg-opacity-50 z-40"
			aria-label="Close cart overlay"
			onclick={() => showCart = false}
			tabindex="0"
		></button>
		<div class="fixed right-0 top-0 h-full w-full md:w-96 z-50 bg-white shadow-xl transform transition-all duration-300 overflow-y-auto">
			<div class="p-6">
				<div class="flex justify-between items-center mb-6">
					<h2 class="text-2xl font-bold">Your Cart</h2>
					<button class="text-gray-500 hover:text-gray-800" aria-label="close cart" onclick={() => showCart = false}>
						<svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
							<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
						</svg>
					</button>
				</div>

				{#if cart.length === 0}
					<div class="text-center py-12">
						<svg xmlns="http://www.w3.org/2000/svg" class="h-16 w-16 mx-auto text-gray-300 mb-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
							<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
						</svg>
						<p class="text-gray-500 text-lg">Your cart is empty</p>
						<button 
							class="mt-4 bg-indigo-600 text-white hover:bg-indigo-700 font-medium px-6 py-2 rounded-lg transition-all"
							onclick={() => showCart = false}
						>
							Continue Shopping
						</button>
					</div>
				{:else}
					<div class="space-y-4 mb-6">
						{#each cart as item}
							<div class="border-b pb-4">
								<div class="flex justify-between">
									<div>
										<h3 class="font-medium">{item.product.name}</h3>
										<p class="text-gray-600 text-sm">${item.product.price.toFixed(2)} each</p>
									</div>
									<button 
										class="text-red-500 hover:text-red-700"
										onclick={() => removeFromCart(item.product.id)}
									>
										Remove
										<svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
											<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
										</svg>
									</button>
								</div>
								<div class="flex items-center mt-2">
									<button 
										class="bg-gray-200 hover:bg-gray-300 rounded-l p-1 w-8" 
										onclick={() => updateCartQuantity(item.product.id, item.quantity - 1)}
									>
										-
									</button>
									<input 
										type="number" 
										value={item.quantity} 
										min="1" 
										class="border-t border-b text-center w-12 p-1" 
										onchange={(e) => updateCartQuantity(item.product.id, parseInt(e.currentTarget.value))}
									/>
									<button 
										class="bg-gray-200 hover:bg-gray-300 rounded-r p-1 w-8" 
										onclick={() => updateCartQuantity(item.product.id, item.quantity + 1)}
									>
										+
									</button>
									<div class="ml-auto font-semibold">
										${(item.product.price * item.quantity).toFixed(2)}
									</div>
								</div>
							</div>
						{/each}
					</div>
					<div class="border-t pt-4">
						<div class="flex justify-between text-lg font-semibold mb-6">
							<span>Total:</span>
							<span>${totalPrice.toFixed(2)}</span>
						</div>

						<form action="?/checkout" method="POST" class="space-y-4" use:enhance={({ formData }) => {
							
								formData.append('cart', JSON.stringify(cart.map(item => ({
									productId: item.product.id,
									quantity: item.quantity
								}))));


								return async ({ result, update }) => {
									if (result.type === 'failure') {
										console.log("failure", result);
									} else if (result.type === 'error') {
										console.log("error", result);
									} else if (result.type === 'success') {
										console.log("success", result);
									} else {
										console.log("Unexpected error", result);
									}


									update(); // Trigger the default form update logic					
								};
							}}>

							<button class="w-full bg-indigo-600 text-white hover:bg-indigo-700 font-medium py-3 rounded-lg transition-all">
								Checkout
							</button>
						</form>
					</div>
				{/if}
			</div>
		</div>
	{/if}

	<!-- Search and Sort Controls -->
	<div class="flex flex-col md:flex-row md:items-center md:justify-between mb-8 gap-4" id="products">
		<div class=" flex items-center gap-3 max-w-md w-full">
			<input
				type="text"
				bind:value={searchQuery}
				placeholder="Search products..."
				class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
			/>
			<svg
				xmlns="http://www.w3.org/2000/svg"
				class="h-5 w-5  text-gray-400"
				fill="none"
				viewBox="0 0 24 24"
				stroke="currentColor"
			>
				<path
					stroke-linecap="round"
					stroke-linejoin="round"
					stroke-width="2"
					d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
				/>
			</svg>
		</div>
		<div class="flex items-center">
			<label for="sort" class="mr-2 text-gray-600">Sort by:</label>
			<select
				id="sort"
				bind:value={selectedSortOption}
				class="border border-gray-300 rounded-lg py-2 px-4 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
			>
				<option value="name-asc">Name (A-Z)</option>
				<option value="name-desc">Name (Z-A)</option>
				<option value="price-asc">Price (Low to High)</option>
				<option value="price-desc">Price (High to Low)</option>
			</select>
		</div>

        <div class="flex justify-center">
			<button 
				class="ml-4 bg-indigo-800 hover:bg-indigo-700 font-medium px-6 py-3 rounded-lg shadow-lg transition-all flex items-center"
				onclick={() => showCart = !showCart}
			>
				<svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
					<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
				</svg>
				Cart ({cart.reduce((sum, item) => sum + item.quantity, 0)})
			</button>
		</div>
	</div>


	<!-- Product Grid -->
	{#if filteredProducts.length === 0}
		<div class="text-center py-16">
			<svg xmlns="http://www.w3.org/2000/svg" class="h-16 w-16 mx-auto text-gray-300 mb-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
				<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.172 16.172a4 4 0 015.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
			</svg>
			<p class="text-gray-600 text-xl">No products found</p>
			<p class="text-gray-500 mt-2">Try adjusting your search</p>
		</div>
	{:else}
		<div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
			{#each filteredProducts as stock (stock.id)}
				<div class="bg-white rounded-xl shadow-md overflow-hidden border border-gray-200 hover:shadow-lg transition-shadow">
					<div class="p-6">
						<div class="flex justify-between items-start">
							<h3 class="text-lg font-bold text-gray-800">{stock.product.name}</h3>
							<div class="text-lg font-bold text-indigo-600">${stock.product.price.toFixed(2)}</div>
						</div>
						<p class="text-gray-600 mt-2 text-sm line-clamp-2">{stock.product.description}</p>
						<div class="mt-4 flex items-center justify-between">
							<span class="text-sm text-gray-500">
								{#if stock.quantity > 10}
									<span class="text-green-600 font-medium">In Stock</span> ({stock.quantity} available)
								{:else if stock.quantity > 0}
									<span class="text-orange-500 font-medium">Low Stock</span> ({stock.quantity} left)
								{:else}
									<span class="text-red-600 font-medium">Out of Stock</span>
								{/if}
							</span>
							<button
								class="bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-2 rounded-lg flex items-center transition-colors"
								onclick={() => addToCart(stock.product)}
								disabled={stock.quantity === 0}
								class:opacity-50={stock.quantity === 0}
								class:cursor-not-allowed={stock.quantity === 0}
							>
								<svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
									<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
								</svg>
								Add to Cart
							</button>
						</div>
					</div>
				</div>
			{/each}
		</div>
	{/if}
</div>


<Footer />