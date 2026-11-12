# TrailLog — E5 Android Mobile Development

Application Android native **Kotlin + XML Views**, destinée à la formation E5. Elle reprend
les éléments d'interface M1/M2 et sert de support à **M3 Data Management**.
Ce dépôt est le **starter étudiant M3** : les éléments M1/M2 fonctionnent ; Room, JSON et DataStore sont les objectifs M3.

## Prérequis
- Android Studio avec Android SDK API 35, émulateur Android ou appareil avec débogage USB
- JDK 17 (JDK fourni par Android Studio recommandé)
- Connexion internet lors du premier téléchargement Gradle / des dépendances

## Première ouverture sous Windows
1. Décompresser le projet, puis ouvrir son dossier racine dans Android Studio.
2. Avant le premier Sync, exécuter `powershell -ExecutionPolicy Bypass -File .\setup-wrapper.ps1` depuis la racine.
3. Cliquer sur **Sync Project with Gradle Files**.
4. Lancer `gradlew.bat assembleDebug` depuis un terminal PowerShell à la racine.
5. Démarrer un émulateur dans Device Manager, puis Run dans Android Studio.
6. Au besoin : `adb devices` puis `adb install -r app\build\outputs\apk\debug\app-debug.apk`.

Sur macOS/Linux : exécuter `sh setup-wrapper.sh`, puis `./gradlew assembleDebug`.
Le script télécharge la distribution officielle Gradle 8.13 depuis `services.gradle.org`, puis génère localement le Wrapper JAR. Le premier téléchargement peut être volumineux.

## Validation
- `gradlew.bat testDebugUnitTest` : tests Kotlin locaux (sur macOS `./gradlew`).
- `gradlew.bat connectedDebugAndroidTest` : tests instrumentés avec émulateur démarré.
- La compilation et l'exécution sur émulateur n'ont **pas** été vérifiées dans l'environnement de génération : vérifier avant distribution.

## Écrans et fonctionnalités
- Catalogue paginé `RecyclerView + Paging 3` (45 entrées fictives en mémoire ; import JSON à réaliser en M3).
- Détail de parcours, image d'en-tête, graphe `ElevationCurveView` en Canvas.
- Actions Ajouter, Modifier, Supprimer, Favori, Partager ; menu de tri et Drawer.
- Message Snackbar avec annulation de suppression tant que l'écran reste ouvert.
- Notification de test et `CoroutineWorker` de rappel à utiliser dans un scénario avancé.
- Ressources en français / anglais et descriptions d'accessibilité.
- À réaliser en M3 : Room, DAO, Repository, import `assets/trails.json`, DataStore pour unités et tri.

## Limites connues
- Le profil d'élévation affiche des données **fictives**, pas de relevé GPS.
- Aucune carte, géolocalisation réelle ni API distante.
- Le `TrailReminderWorker` implémente une tâche différée de démonstration, sans planification de calendrier complet.
- La pagination fonctionne via Paging 3, mais le catalogue fictif M2 et les requêtes Room M3 utilisent des sources distinctes.
- Les tests instrumentés nécessitent un appareil/émulateur Android ; ne sont pas lançables avec un simple JDK.
- Les paramètres sont implémentés avec une UI Material plutôt qu'un `PreferenceFragmentCompat` : le M3 peut conserver cette UI et se concentrer sur DataStore.

## Architecture
- `model/` : objets métier et formatage de distances.
- `ui/` : Activities XML, adapter Paging, ViewModel et View Canvas.
- `data/` : interface Repository, mémoire M2, Room M3, paramètres.
- `worker/` : notification et WorkManager.

Les exercices exacts sont dans `M3-EXERCISES.md`.
