self.addEventListener('install', event => {
  console.log('Service Worker installé');
});

self.addEventListener('fetch', event => {
  // Pour l’instant, juste laisser passer les requêtes
});
