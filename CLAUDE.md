# CLAUDE.md — chess_clock (app mobile)

## Stack decision — 2026-09-13

### Contexte

Rebuild quasi complet d'un premier projet web vanilla JS (chess clock fait à la main, avant IA) en app mobile native, publiée sur l'App Store et le Google Play Store. Objectif : pièce de portfolio démontrant un pipeline complet dev → CI/CD → publication store, sans Mac local. Pas de vocation commerciale, pas de scalabilité serveur à prévoir (aucun serveur dans l'architecture). L'utilisateur agit uniquement comme architecte/décideur — aucune écriture de code de sa part sur ce projet.

### Choix retenus

- **Runtime / framework : natif x2 — SwiftUI (iOS) + Kotlin / Jetpack Compose (Android)** — pourquoi pas React Native/Expo ou Flutter : rejet explicite de toute couche d'encapsulation/abstraction cross-platform (mauvaise expérience passée avec Expo), volonté de vivre l'expérience "vrai code natif dans 2 écosystèmes" pour le portfolio.
- **Base de données : aucune** — stockage local uniquement (préférences utilisateur : temps par défaut, options), pas de backend, pas de compte, pas de paiement.
- **Auth : aucune** — app 100% locale, pas de notion d'utilisateur distant.
- **Hébergement : aucun serveur** — app purement client, s'exécute entièrement sur l'appareil.
- **CI/CD : Codemagic** — déjà utilisé et maîtrisé par l'utilisateur pour son premier app (Next.js/React encapsulé via TestFlight). Build Android également possible en local (Android Studio, Linux) ; build/signature iOS uniquement via Codemagic (VM macOS cloud), aucun Xcode local disponible.

### Risques acceptés consciemment

- **Boucle de développement iOS lente** : pas de Mac local, donc pas de Simulator ni de SwiftUI Previews en local. Chaque itération visuelle nécessite un cycle CI complet (Codemagic) + installation via TestFlight. Accepté consciemment — même workflow que pour la première app, pas de Mac distant envisagé pour l'instant, pas de besoin de rush.
- **Deux codebases à maintenir en parallèle** (Swift + Kotlin) au lieu d'une seule — accepté car l'objectif explicite est justement de comparer cette approche à l'encapsulation utilisée sur la première app.
- **Robustesse "par appareil" plutôt que scalabilité serveur** : "1000 joueurs simultanés" ne concerne pas une infra partagée (il n'y en a pas) mais la fiabilité du moteur de chrono sur chaque appareil pris individuellement (dérive de timer, comportement en arrière-plan, cycle de vie OS) — à traiter au moment de l'implémentation du chrono, pas comme un sujet d'infra.
