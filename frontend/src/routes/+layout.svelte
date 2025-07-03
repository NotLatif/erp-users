<script lang="ts">
	import Footer from '$lib/components/Footer.svelte';
	import '../app.css';

	let { children } = $props();
</script>

<div>
	<nav class="bg-gray-800 p-4">
		<div class="container mx-auto flex justify-between items-center">
			<a href="/" class="text-white text-lg font-semibold">ERP</a>
			<div class="flex space-x-4">
				<a href="/warehouses" class="text-gray-300 hover:text-white">Warehouses</a>
				<a href="/products" class="text-gray-300 hover:text-white">Products</a>
				<a href="/stocks" class="text-gray-300 hover:text-white">Stocks</a>
				<a href="/users" class="text-gray-300 hover:text-white">Users</a>
			</div>
		</div>
	</nav>
</div>

{@render children()}


