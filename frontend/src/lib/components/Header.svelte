<script lang="ts">

    let { data } = $props<{ data: { title: string; description: string } }>();

</script>


<div class="mb-8">
    <h1 class="text-3xl font-bold text-gray-800">{data.title}</h1>
    <p class="text-gray-600">{data.description}</p>
</div>