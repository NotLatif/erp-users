<script lang="ts">
    import type { Product, Warehouse, StockEntry } from '$lib/types/inventory';

    // Mock data for the dashboard
    const mockProducts: Product[] = [
        { id: 1, name: 'Laptop XPS 13', price: 1299.99, sku: 'LT-XPS13', description: 'Dell XPS 13 Laptop' },
        { id: 2, name: 'Monitor 27"', price: 349.99, sku: 'MN-27UL', description: 'Ultra-wide 27" monitor' },
        { id: 3, name: 'Keyboard', price: 89.99, sku: 'KB-MX', description: 'Mechanical keyboard' },
        { id: 4, name: 'Mouse', price: 49.99, sku: 'MS-PRO', description: 'Wireless mouse' },
        { id: 5, name: 'Headphones', price: 129.99, sku: 'HP-NC', description: 'Noise-cancelling headphones' },
        { id: 6, name: 'Desk Chair', price: 199.99, sku: 'CH-ERG', description: 'Ergonomic desk chair' },
        { id: 7, name: 'Desk Lamp', price: 39.99, sku: 'LM-LED', description: 'LED desk lamp' },
        { id: 8, name: 'Printer', price: 299.99, sku: 'PR-LJ', description: 'Laser jet printer' }
    ];

    const mockWarehouses: Warehouse[] = [
        { id: 1, name: 'North Warehouse', location: 'New York' },
        { id: 2, name: 'South Warehouse', location: 'Miami' },
        { id: 3, name: 'West Warehouse', location: 'Los Angeles' },
        { id: 4, name: 'East Warehouse', location: 'Boston' }
    ];

    const mockStockEntries: StockEntry[] = [
        { id: 1, quantity: 25, updatedAt: '2025-06-28T08:30:00', product: mockProducts[0], warehouse: mockWarehouses[0] },
        { id: 2, quantity: 5, updatedAt: '2025-06-29T14:15:00', product: mockProducts[1], warehouse: mockWarehouses[0] },
        { id: 3, quantity: 3, updatedAt: '2025-06-30T11:45:00', product: mockProducts[2], warehouse: mockWarehouses[1] },
        { id: 4, quantity: 12, updatedAt: '2025-07-01T09:20:00', product: mockProducts[3], warehouse: mockWarehouses[1] },
        { id: 3, quantity: 12, updatedAt: '2025-07-01T09:20:00', product: mockProducts[3], warehouse: mockWarehouses[1] },
        { id: 6, quantity: 4, updatedAt: '2025-07-02T10:30:00', product: mockProducts[0], warehouse: mockWarehouses[2] },
        { id: 5, quantity: 8, updatedAt: '2025-07-01T16:05:00', product: mockProducts[4], warehouse: mockWarehouses[2] },
        { id: 6, quantity: 4, updatedAt: '2025-07-02T10:30:00', product: mockProducts[0], warehouse: mockWarehouses[2] },
    ];

    const mockRecentOrders = [
        { id: 'ORD-2025-1234', customer: 'Acme Corp', date: '2025-07-01', status: 'Shipped', total: 2599.98 },
        { id: 'ORD-2025-1233', customer: 'TechSolutions Inc', date: '2025-06-30', status: 'Processing', total: 1499.95 },
        { id: 'ORD-2025-1232', customer: 'Global Industries', date: '2025-06-28', status: 'Delivered', total: 3745.50 },
        { id: 'ORD-2025-1231', customer: 'StartUp LLC', date: '2025-06-27', status: 'Canceled', total: 899.99 }
    ];

    // Calculate metrics
    const totalProducts = mockProducts.length;
    const totalWarehouses = mockWarehouses.length;
    const totalInventoryValue = mockStockEntries.reduce((sum, entry) => {
        return sum + (entry.quantity * entry.product.price);
    }, 0);
    
    const lowStockItems = mockStockEntries.filter(entry => entry.quantity < 5).length;

    const today = new Date().toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    });
    
    // Calculate products by warehouse for the chart
    const warehouseProductCounts = mockWarehouses.map(warehouse => {
        const count = mockStockEntries.filter(entry => entry.warehouse.id === warehouse.id).length;
        return { name: warehouse.name, count };
    });
    
    // Get top products by value
    const topProductsByValue = [...mockStockEntries]
        .sort((a, b) => (b.quantity * b.product.price) - (a.quantity * a.product.price))
        .slice(0, 5);
</script>

<div class="container mx-auto px-4 py-8">
    <!-- Dashboard Header -->
    <div class="mb-8">
        <h1 class="text-3xl font-bold text-gray-800">ERP Dashboard</h1>
        <p class="text-gray-600">Overview of your business operations as of {today}</p>
    </div>

    <!-- Key Metrics Cards -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        <!-- Total Products -->
        <div class="bg-white rounded-lg shadow-md p-6 border-l-4 border-blue-500">
            <div class="flex items-center">
                <div class="p-3 rounded-full bg-blue-100 mr-4">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-blue-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10" />
                    </svg>
                </div>
                <div>
                    <p class="text-gray-500 text-sm">Total Products</p>
                    <p class="text-2xl font-semibold">{totalProducts}</p>
                </div>
            </div>
        </div>
        
        <!-- Total Warehouses -->
        <div class="bg-white rounded-lg shadow-md p-6 border-l-4 border-green-500">
            <div class="flex items-center">
                <div class="p-3 rounded-full bg-green-100 mr-4">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-green-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
                    </svg>
                </div>
                <div>
                    <p class="text-gray-500 text-sm">Warehouses</p>
                    <p class="text-2xl font-semibold">{totalWarehouses}</p>
                </div>
            </div>
        </div>
        
        <!-- Inventory Value -->
        <div class="bg-white rounded-lg shadow-md p-6 border-l-4 border-purple-500">
            <div class="flex items-center">
                <div class="p-3 rounded-full bg-purple-100 mr-4">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-purple-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
                </div>
                <div>
                    <p class="text-gray-500 text-sm">Inventory Value</p>
                    <p class="text-2xl font-semibold">${totalInventoryValue.toLocaleString(undefined, { maximumFractionDigits: 2 })}</p>
                </div>
            </div>
        </div>
        
        <!-- Low Stock Alert -->
        <div class="bg-white rounded-lg shadow-md p-6 border-l-4 border-red-500">
            <div class="flex items-center">
                <div class="p-3 rounded-full bg-red-100 mr-4">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-red-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
                    </svg>
                </div>
                <div>
                    <p class="text-gray-500 text-sm">Low Stock Items</p>
                    <p class="text-2xl font-semibold">{lowStockItems}</p>
                </div>
            </div>
        </div>
    </div>

    <!-- Two-column layout for charts and tables -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
        <!-- Left column -->
        <div>
            <!-- Top Products by Value -->
            <div class="bg-white rounded-lg shadow-md p-6 mb-8">
                <h2 class="text-xl font-semibold mb-4">Top Products by Value</h2>
                <div class="overflow-x-auto">
                    <table class="w-full text-left">
                        <thead>
                            <tr class="bg-gray-50">
                                <th class="py-2 px-4">Product</th>
                                <th class="py-2 px-4">Warehouse</th>
                                <th class="py-2 px-4">Quantity</th>
                                <th class="py-2 px-4">Value</th>
                            </tr>
                        </thead>
                        <tbody>
                            {#each topProductsByValue as entry}
                                <tr class="border-t">
                                    <td class="py-3 px-4">{entry.product.name}</td>
                                    <td class="py-3 px-4">{entry.warehouse.name}</td>
                                    <td class="py-3 px-4">{entry.quantity}</td>
                                    <td class="py-3 px-4">${(entry.quantity * entry.product.price).toLocaleString(undefined, { maximumFractionDigits: 2 })}</td>
                                </tr>
                            {/each}
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- Stock Distribution -->
            <div class="bg-white rounded-lg shadow-md p-6">
                <h2 class="text-xl font-semibold mb-4">Warehouse Product Distribution</h2>
                <div class="space-y-4">
                    {#each warehouseProductCounts as warehouse}
                        <div>
                            <div class="flex justify-between mb-1">
                                <span>{warehouse.name}</span>
                                <span class="text-sm font-medium">{warehouse.count} products</span>
                            </div>
                            <div class="w-full bg-gray-200 rounded-full h-2.5">
                                <div class="bg-blue-600 h-2.5 rounded-full" style="width: {(warehouse.count / totalProducts) * 100}%"></div>
                            </div>
                        </div>
                    {/each}
                </div>
            </div>
        </div>

        <!-- Right column -->
        <div>
            <!-- Recent Orders -->
            <div class="bg-white rounded-lg shadow-md p-6 mb-8">
                <div class="flex justify-between items-center mb-4">
                    <h2 class="text-xl font-semibold">Recent Orders</h2>
                    <a href="#" class="text-blue-600 hover:underline text-sm">View All</a>
                </div>
                <div class="overflow-x-auto">
                    <table class="w-full text-left">
                        <thead>
                            <tr class="bg-gray-50">
                                <th class="py-2 px-4">Order ID</th>
                                <th class="py-2 px-4">Customer</th>
                                <th class="py-2 px-4">Date</th>
                                <th class="py-2 px-4">Status</th>
                                <th class="py-2 px-4">Total</th>
                            </tr>
                        </thead>
                        <tbody>
                            {#each mockRecentOrders as order}
                                <tr class="border-t">
                                    <td class="py-3 px-4 font-medium">{order.id}</td>
                                    <td class="py-3 px-4">{order.customer}</td>
                                    <td class="py-3 px-4">{order.date}</td>
                                    <td class="py-3 px-4">
                                        <span class={`px-2 py-1 rounded-full text-xs
                                            ${order.status === 'Shipped' ? 'bg-blue-100 text-blue-800' : ''}
                                            ${order.status === 'Processing' ? 'bg-yellow-100 text-yellow-800' : ''}
                                            ${order.status === 'Delivered' ? 'bg-green-100 text-green-800' : ''}
                                            ${order.status === 'Canceled' ? 'bg-red-100 text-red-800' : ''}
                                        `}>
                                            {order.status}
                                        </span>
                                    </td>
                                    <td class="py-3 px-4">${order.total.toLocaleString(undefined, { maximumFractionDigits: 2 })}</td>
                                </tr>
                            {/each}
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- Quick Actions -->
            <div class="bg-white rounded-lg shadow-md p-6">
                <h2 class="text-xl font-semibold mb-4">Quick Actions</h2>
                <div class="grid grid-cols-2 gap-4">
                    <a href="/products" class="flex items-center p-4 bg-blue-50 rounded-lg hover:bg-blue-100 transition-colors">
                        <div class="p-2 rounded-full bg-blue-100 mr-3">
                            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-blue-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10" />
                            </svg>
                        </div>
                        <span>Manage Products</span>
                    </a>
                    <a href="/warehouses" class="flex items-center p-4 bg-green-50 rounded-lg hover:bg-green-100 transition-colors">
                        <div class="p-2 rounded-full bg-green-100 mr-3">
                            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-green-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
                            </svg>
                        </div>
                        <span>Manage Warehouses</span>
                    </a>
                    <a href="/stocks" class="flex items-center p-4 bg-purple-50 rounded-lg hover:bg-purple-100 transition-colors">
                        <div class="p-2 rounded-full bg-purple-100 mr-3">
                            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-purple-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
                            </svg>
                        </div>
                        <span>Manage Inventory</span>
                    </a>
                    <a href="/users" class="flex items-center p-4 bg-yellow-50 rounded-lg hover:bg-yellow-100 transition-colors">
                        <div class="p-2 rounded-full bg-yellow-100 mr-3">
                            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-yellow-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
                            </svg>
                        </div>
                        <span>Manage Users</span>
                    </a>
                    <a href="/orders" class="flex items-center p-4 bg-orange-50 rounded-lg hover:bg-orange-100 transition-colors">
                        <div class="p-2 rounded-full bg-orange-100 mr-3">
                            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-orange-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4" />
                            </svg>
                        </div>
                        <span>Manage Orders</span>
                    </a>
                    <a href="/clients" class="flex items-center p-4 bg-pink-50 rounded-lg hover:bg-pink-100 transition-colors">
                        <div class="p-2 rounded-full bg-pink-100 mr-3">
                            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-pink-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a4 4 0 00-3-3.87M9 20H4v-2a4 4 0 013-3.87m6-7a4 4 0 11-8 0 4 4 0 018 0zm6 4a4 4 0 10-8 0 4 4 0 008 0z" />
                            </svg>
                        </div>
                        <span>Manage Clients</span>
                    </a>
                </div>
            </div>
        </div>
    </div>
</div>