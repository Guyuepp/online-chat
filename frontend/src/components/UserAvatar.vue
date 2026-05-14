<template>
  <div class="avatar" :style="style">
    <img v-if="url && !imgErr" :src="url" :alt="name" @error="imgErr = true">
    <span v-else>{{ initial }}</span>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
  url:  { type: String,  default: '' },
  name: { type: String,  default: '' },
  size: { type: Number,  default: 40 }
})

const imgErr = ref(false)

const initial = computed(() => (props.name || '?')[0].toUpperCase())

const style = computed(() => ({
  width:    props.size + 'px',
  height:   props.size + 'px',
  fontSize: Math.round(props.size * 0.38) + 'px'
}))
</script>

<style scoped>
.avatar {
  border-radius: 50%;
  background: linear-gradient(135deg, var(--violet), var(--rose));
  display: flex; align-items: center; justify-content: center;
  color: #fff; font-weight: 500; flex-shrink: 0;
  overflow: hidden;
  user-select: none;
}
.avatar img { width: 100%; height: 100%; object-fit: cover; }
</style>
