# UML de Blind Path

Diagrammes de classes écrits en [Mermaid](https://mermaid.js.org/) : GitHub les dessine directement quand on ouvre un fichier.
Ils sont générés à partir du code de `core/src/main/java` (branche `dev`, 22/11/2026).

## Lire les diagrammes

- `+` public, `-` privé, `#` protégé, `~` visibilité package.
- Souligné = statique ; *italique* = abstrait.
- Triangle plein = héritage (`extends`) ; triangle en pointillés = implémentation d'interface (`implements`).
- Flèche simple = attribut vers une autre classe (association), avec sa multiplicité (`1` ou `*`).
- Flèche en pointillés = dépendance (la classe crée ou renvoie l'autre).
- Classe grisée en pointillés = classe d'un autre package, détaillée dans son propre fichier.

## Fichiers

| Fichier | Contenu |
|---|---|
| [00_Vue_ensemble](00_Vue_ensemble.md) | Toutes les classes principales et leurs liens, sans détail |
| [01_Personnage](01_Personnage.md) | `domain.character` : Character, Gnome, Direction, Movable |
| [02_Jeu_et_niveaux](02_Jeu_et_niveaux.md) | `domain.game` : GameSession, Level, LevelFactory, LevelConfig, LevelCatalog |
| [03_Labyrinthe](03_Labyrinthe.md) | `domain.labyrinth` : Labyrinth, obstacles, entrée, sortie, Collidable |
| [04_Monstres](04_Monstres.md) | `domain.monster` : Monster, les 5 monstres, MovementStrategy, MonsterType |
| [05_Lumiere_et_snap](05_Lumiere_et_snap.md) | `domain.light` : Light, SnapEvent, SnapListener |
| [06_Inventaire_et_objets](06_Inventaire_et_objets.md) | `domain.inventory` + `domain.object` : Inventory, les 5 objets, objets posés |
| [07_Coffres](07_Coffres.md) | `domain.chest` : Chest, LootTable, LootResult |
| [08_Pathfinding](08_Pathfinding.md) | `domain.pathfinding` + `infrastructure.pathfinding` |
| [09_Temps](09_Temps.md) | `domain.time` : GameClock, GameTimer, TickListener |
| [10_Physique](10_Physique.md) | `domain.physics` + `infrastructure.physics` : CollisionWorld, AABB, Box2D |
| [11_Chargement_des_cartes](11_Chargement_des_cartes.md) | `infrastructure.map` : Tiled, LevelRegistry |
| [12_Ecrans_HUD_et_audio](12_Ecrans_HUD_et_audio.md) | `Main`, `presentation.screen`, `hud`, `input`, `audio` |
| [13_Rendu](13_Rendu.md) | `presentation.renderer` |

## Patrons de conception

| Patron | Où le voir |
|---|---|
| Strategy | `MovementStrategy` / `ChaseStrategy` / `JumperStrategy` (04) |
| Fabrique | `MonsterType.create`, `ItemType.create` (04, 06), `LevelFactory` (02) |
| Observer | `SnapListener` (05, 04, 07) et `TickListener` (09, 04) |
| Template / héritage | `Monster`, `Item`, `PlacedObject`, `Obstacle`, `Character` |
| Inversion de dépendance | interfaces du domaine `Pathfinder`, `CollisionWorld`, `LabyrinthLoader`, `LevelCatalog` implémentées dans `infrastructure` (08, 10, 11) |
