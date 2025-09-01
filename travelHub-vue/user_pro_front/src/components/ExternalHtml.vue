<template>
    <div class="iframe-container">
        <iframe src="/123.html" frameborder="0" class="iframe-content"></iframe>
    </div>
</template>

<style scoped>
.iframe-container {
    width: 100%;
    height: 100%;
    overflow: hidden;
}

.iframe-content {
    width: 100%;
    height: 100vh;
    /* Adjust as needed */
    border: none;
}
</style>
