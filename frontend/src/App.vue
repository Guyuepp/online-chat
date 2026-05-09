<template>
  <!-- Background orbs live here so they persist across route changes -->
  <div class="orbs" aria-hidden="true">
    <div class="orb orb-a"></div>
    <div class="orb orb-b"></div>
    <div class="orb orb-c"></div>
  </div>
  <RouterView />
</template>

<style>
.orbs { position: fixed; inset: 0; pointer-events: none; z-index: 0; overflow: hidden; }
.orb  { position: absolute; border-radius: 50%; filter: blur(96px); }
.orb-a {
  width: 680px; height: 680px;
  background: radial-gradient(circle, #7c3aed 0%, transparent 65%);
  top: -240px; left: -170px; opacity: .30;
  animation: da 22s ease-in-out infinite;
}
.orb-b {
  width: 480px; height: 480px;
  background: radial-gradient(circle, #f472b6 0%, transparent 65%);
  bottom: -140px; right: 12%; opacity: .25;
  animation: db 28s ease-in-out infinite;
}
.orb-c {
  width: 300px; height: 300px;
  background: radial-gradient(circle, #312e81 0%, transparent 65%);
  top: 35%; right: -60px; opacity: .40;
  animation: dc 18s ease-in-out infinite;
}
@keyframes da { 0%,100%{transform:translate(0,0)} 50%{transform:translate(45px,32px)} }
@keyframes db { 0%,100%{transform:translate(0,0)} 50%{transform:translate(-45px,-28px)} }
@keyframes dc { 0%,100%{transform:translate(0,0)} 50%{transform:translate(26px,-42px)} }
</style>
