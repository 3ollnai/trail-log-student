# TrailLog — M3 Data Management : exercices étudiants

> Objectif : compléter la gestion des données de TrailLog, **sans refaire M1/M2**.
> L'application doit continuer à se lancer même si tu n'as pas fini tous les exercices.

## Prise en main
1. Forker le dépôt starter GitHub, cloner **son propre fork**.
2. Ouvrir le dossier racine dans Android Studio, suivre `README.md`.
3. Lancer le projet et parcourir la liste, les détails, les favoris et les paramètres.
4. Ne pas modifier les tests fournis ; committer et pousser son travail.

## Lab 1 — Room, CRUD et mise à jour réactive
- Étudier `model/Trail.kt`, `data/TrailRepository.kt`, `data/MemoryTrailRepository.kt`, `ui/TrailListViewModel.kt`.
- Compléter les fichiers dans `data/local/` pour introduire l'entity `TrailEntity`, les requêtes `TrailDao`, la `TrailDatabase` et l'implémentation `RoomTrailRepository`.
- Raccorder la nouvelle implémentation dans `TrailLogApplication.kt`.
- Conserver la pagination `PagingSource`, la liste `RecyclerView` et l'API `TrailRepository` existantes.
- Attendus : ajout, modification, suppression et favoris persistent après relance. Le flux de données actualise automatiquement l'interface.
- Valider les cas invalides (nom vide, distance négative), sans bloquer le thread principal.

## Lab 2 — Import JSON à l'installation
- Fichier source : `app/src/main/assets/trails.json`.
- Compléter `seedIfNeeded()` dans le Repository et parser le JSON fourni.
- Mapper et valider les objets (id et nom non vides, distances/dénivelés non négatifs).
- Insérer les randonnées initiales **une seule fois**, sans écraser une modification utilisateur.
- Attendu : liste alimentée à la première installation, sans doublons aux relances.

## Lab 3 — Préférences persistantes DataStore
- Étudier `model/UserSettings.kt`, `data/PreferencesRepository.kt`, `ui/SettingsActivity.kt`.
- Créer `data/DataStorePreferencesRepository.kt` avec deux clés :
  `pref_distance_units` (KM/MI) et `pref_default_sort` (NAME/DISTANCE).
- Remplacer la version en mémoire dans `TrailLogApplication.kt`.
- Observer les préférences comme un `Flow<UserSettings>`.
- Attendus : km/mi met à jour les distances, tri change l'ordre et les choix survivent à une relance.

## Vérifications
- `gradlew.bat assembleDebug` ou `./gradlew assembleDebug`
- `gradlew.bat testDebugUnitTest` ou `./gradlew testDebugUnitTest`
- Exécuter sur émulateur Android ; démontrer chaque comportement.

## Remise
Pousser les commits sur son fork et communiquer son URL selon les consignes du formateur.
