# Vérifier le HUD et l'inventaire

Depuis la racine du projet, lancer tous les tests :

```bash
./gradlew :core:test
```

Ou seulement les tests liés à ces changements :

```bash
./gradlew :core:test --tests 'com.blindpath.domain.inventory.*' --tests 'com.blindpath.domain.character.GnomeTest' --tests 'com.blindpath.domain.light.LightTest'
```

Résultats : `core/build/reports/tests/test/index.html`.
Couverture : `core/build/reports/jacoco/test/html/index.html`.

## Vérifications automatisées

- Inventaire générique avec un propriétaire, cinq emplacements et cinq unités par type.
- Ajout, refus, consommation, suppression et décalage des piles, liste non modifiable.
- Soin d'un demi-cœur, refus de soin à pleine vie ou après la mort.
- Inventaires indépendants entre deux gnomes.
- Recharge initialement pleine, remise à zéro au clap, progression sur 1,5 seconde.

Ces tests ne démarrent pas de fenêtre OpenGL. Le HUD est vérifié dans le jeu.

## Vérifications dans le jeu

Lancer `./gradlew lwjgl3:run`, puis vérifier :

- Échap pendant l'introduction ouvre le menu ; laisser finir l'introduction ouvre aussi le menu.
- Start ouvre le jeu ; revenir au menu puis Start ne rejoue pas l'introduction.
- La barre se remplit du bas vers le haut et retrouve son maximum après 1,5 seconde.
- Les trois cœurs restent sous la barre ; après des dégâts, le cœur du haut se vide en premier.
- Avec des objets ajoutés au niveau de démonstration, chaque pile affiche la bonne icône
  et la bonne quantité ; consommer le dernier cœur décale les piles suivantes et vide le dernier emplacement.
- Les cinq emplacements restent à droite, centrés verticalement, après redimensionnement.
- En pause, la logique du jeu et la recharge restent suspendues ; Échap reprend le jeu.

Les quatre objets provisoires ont une méthode `use` vide : seul le soin de `Heart`
possède un effet métier à vérifier pour le moment. Leur consommation sans effet ne
constitue pas un test de la future logique de dynamite, piège, boussole ou booster.

L'alignement, les interactions des écrans, la libération des ressources graphiques
et la fluidité nécessitent ces essais réels ; les tests unitaires ne les garantissent pas.
