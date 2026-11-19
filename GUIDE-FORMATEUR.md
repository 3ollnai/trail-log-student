# Guide formateur — TrailLog M3

## Livrables
- **Starter M3** : tous les écrans et fonctions de démonstration M1/M2 sont fournis ; les données sont provisoirement en mémoire ; les trois exercices de persistance sont à réaliser.
- **Solution M3** : Room, import JSON et Preferences DataStore raccordés au même UI.

## Programme M3 harmonisé
1. **Lab 1 — Room + DAO + Repository + LiveData (ou Flow)** : conserver `Trail` comme modèle métier ; créer `TrailEntity` (`trails`), DAO CRUD, Database et adapter la source Paging ; raccorder la source dans `TrailLogApplication`. Faire fonctionner CRUD et persistance après fermeture de l'app.
2. **Lab 2 — JSON seed** : charger `app/src/main/assets/trails.json` au premier lancement, mapper et valider chaque objet, insérer via Repository ; ne pas écraser les ajouts des étudiants ni réinsérer à chaque démarrage.
3. **Lab 3 — DataStore** : conserver l'UI des paramètres déjà préparée ; remplacer `MemoryPreferencesRepository` par `DataStorePreferencesRepository` ; observer le Flow pour mettre à jour le tri, l'affichage des distances et les choix à la réouverture.

## Points à vérifier manuellement (solution)
- L'application s'ouvre sur la liste sans plantage.
- Ouvrir un détail, revenir, ouvrir/fermer le drawer et Favoris.
- Ajouter un trail, modifier, quitter puis relancer : valeurs toujours présentes.
- Supprimer un trail, relancer : suppression persistante.
- Sur une installation fraîche, le catalogue JSON apparaît ; un second lancement ne le duplique pas.
- Basculer km/mi et tri dans Paramètres : changement immédiat, choix conservés après relance.
- Pivot portrait/paysage sur l'écran de détail : composants accessibles.
- Tester notification sous Android 13+ en acceptant/refusant la permission.
- `testDebugUnitTest` et `connectedDebugAndroidTest` (émulateur obligatoire).

## Notes pédagogiques
- Le LMS M2 évoquait parfois Room avant M3 : retirer les références qui supposent la persistance déjà acquise.
- Normaliser partout `trails`, `id: String`, `distanceKm: Double`, `elevationGainM: Int`, `isFavorite: Boolean`.
- Utiliser une seule source de vérité des données ; laisser le Repository masquer la base.
- Les sources M2/M3 ne doivent pas contenir les solutions M3 dans le dépôt étudiant.
- **Ne pas distribuer ce fichier formateur avec la correction**.

## Point technique
L'exercice M3 peut accepter `Flow<List<Trail>>` (avec pont `.asLiveData()` côté ViewModel) plutôt que d'obliger simultanément LiveData et Paging 3 au même niveau : cela évite d'entretenir deux systèmes de pagination concurrents.
