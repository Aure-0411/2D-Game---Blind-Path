# Blind Path

## Présentation du jeu

### Pitch

Lors de sa toute première expédition avec son père, un jeune gnome chute dans un ancien puits de mine et se réveille au plus profond d'un donjon maléfique. À ses côtés, une lanterne magique, qui n'éclaire que lui. D'un claquement de doigts, il peut en réveiller le pouvoir et révéler un bref instant ce qui l'entoure, mais le bruit produit attire ce qui rôde dans le noir...

Parcourez cinq niveaux pour remonter à la surface, déjouez les pièges et échappez aux monstres pour retrouver les vôtres. Chaque claquement est un choix crucial : voir ou rester discret.

### Gameplay

Le joueur évolue dans une série de labyrinthes plongés dans l'obscurité. À chaque claquement, le halo de la lanterne s'élargit un court instant autour de l'endroit où il a été lancé, ce qui permet au joueur de se repérer. Mais les monstres, qui se déplacent déjà de manière autonome, sont attirés par le bruit.

Chacun des cinq niveaux abrite un monstre différent. Le joueur ne peut pas tuer les monstres : il peut seulement les fuir, les ralentir ou les attirer ailleurs. En cas de mort, il reprend au dernier point de sauvegarde atteint et conserve son inventaire.

Le labyrinthe réserve aussi des obstacles (ronces, murs à levier) et des coffres, qui s'ouvrent au claquement. Pour s'aider, le joueur peut compter sur les objets qu'il y trouve.

### Monstres

| Monstre | Comportement |
| --- | --- |
| Jumper | Se dirige vers le bruit, puis bondit sur sa proie. |
| Screamer | Pousse un cri qui étourdit le gnome lorsque celui-ci claque des doigts trop souvent. |
| Chameleon | Reste invisible jusqu'à ce que le gnome s'en approche. |
| Runaway | Fuit vers la sortie à chaque claquement : le suivre indique le chemin, au risque de le croiser. |
| Inverter | Inverse les commandes du gnome lorsqu'il s'en approche. |

### Objets

| Objet | Effet |
| --- | --- |
| Cœur | Rend de la vie. |
| Piège à ours | Posé au sol, il immobilise pendant quelques secondes le premier monstre qui marche dessus. |
| Dynamite | Posée au sol, elle explose après un court délai et fait un bruit qui attire le monstre. |
| Recharge de lanterne | Agrandit le halo de la lanterne pendant les cinq claquements suivants. |
| Boussole | Indique pendant quelques secondes la direction de la sortie. |

### Inspiration

Le jeu s'inspire principalement de la série *Little Nightmares*, qui met en scène des personnages enfantins confrontés à des monstres aussi difformes que meurtriers. Le contraste entre l'apparence du gnome et celle des monstres y puise son inspiration, de même que l'incapacité du joueur à vaincre les ennemis et la fuite comme seule échappatoire.

Une autre inspiration est *The Binding of Isaac*, là aussi en raison du contraste entre l'aspect du protagoniste et celui des ennemis, mais aussi par le système d'objets : dans ce jeu comme dans *Blind Path*, le joueur ramasse des objets aux effets variés qui l'aident à progresser.

Enfin, *Blind Path* s'inspire du genre du roguelite, dans lequel on conserve tout ou partie de son inventaire après une défaite.

---

## Commandes

| Action | Touche |
| --- | --- |
| Se déplacer | Flèches ou Z, Q, S, D (deux touches pour les diagonales) |
| Claquer des doigts | Espace |
| Utiliser un objet de l'inventaire | 1 à 5 (rangée du haut ou pavé numérique) |
| Afficher les zones de collision (mode débogage) | F3 |

---

## Contexte du projet et équipe

*Blind Path* est un jeu conçu par quatre personnes, en trois semaines, dans le cadre du projet 2D Game d'Epitech. Il est développé en Java avec le framework libGDX. L'objectif principal du projet est la maîtrise de la programmation orientée objet.

| Membre | Compte GitHub | Module | Spécialité |
| --- | --- | --- | --- |
| Xavier | xavier-ammar | Héros et objets | Rédaction (GDD, README, Javadoc) |
| Pierre | pioupious | Affichage et interface | Assets graphiques et sonores, UML |
| Aurélien | Aure-0411 | Lanterne et monstres | Planification, GitHub, intégration continue |
| Grégoire | gregoireY | Niveaux et moteur | Outillage, relecture technique |

---

## Prérequis

Avant de cloner le dépôt, assurez-vous d'avoir installé :

- **Git** (vérification : `git --version`)
- **Java JDK 21** (vérification : `java --version`)

Si vous souhaitez naviguer dans le code et le modifier avec plus de confort, vous pouvez utiliser **IntelliJ IDEA** ou **VS Code**.

---

## Installation et lancement

1. **Cloner le dépôt :**

   ```bash
   git clone https://github.com/Aure-0411/2D-Game---Blind-Path.git
   cd 2D-Game---Blind-Path
   ```

2. **(Facultatif) Importer le projet dans l'éditeur de code :**
   - **IntelliJ IDEA :** choisissez **Open** (Ouvrir), puis sélectionnez le fichier `build.gradle` à la racine du projet.
   - **VS Code :** placez-vous à la racine du projet, puis tapez `code .` dans votre terminal.

   Laissez l'éditeur télécharger automatiquement toutes les dépendances (cela peut prendre quelques minutes au premier lancement).

3. **Lancer le jeu :**

   ```bash
   ./gradlew lwjgl3:run
   ```

   Depuis IntelliJ IDEA, vous pouvez aussi faire un clic droit sur la classe `Lwjgl3Launcher` (module `lwjgl3`), puis choisir **Run**.

---

## Tests et couverture

Lancer tous les tests unitaires (JUnit 5) :

```bash
./gradlew test
```

- Le rapport des tests se trouve dans `core/build/reports/tests/test/index.html`.
- Cette commande génère aussi le rapport de couverture JaCoCo, dans `core/build/reports/jacoco/test/html/index.html`.

Les tests sont également exécutés automatiquement par l'intégration continue (GitHub Actions) sur les branches du dépôt.

---

## Documentation

- **Javadoc :** générée avec la commande ci-dessous, puis consultable dans `core/build/docs/javadoc/index.html`. Elle est aussi publiée automatiquement par GitHub Actions à chaque fusion sur `dev`.

  ```bash
  ./gradlew :core:javadoc
  ```

- **Diagrammes UML :** dans le dossier [`docs/uml/`](docs/uml/), un fichier par partie du jeu (personnage, niveaux, monstres, objets, rendu…). Les diagrammes, écrits en Mermaid, s'affichent directement sur GitHub. Commencez par le [`README`](docs/uml/README.md) du dossier.

---

## Architecture du projet

Le dépôt est organisé en trois modules :

- `core` : toute la logique et l'affichage du jeu. **C'est ici que l'on code.**
- `assets` : images, sons, musiques et cartes Tiled (`assets/maps/`).
- `lwjgl3` : le lanceur pour ordinateur (Windows, macOS, Linux).

Le code de `core` (`com.blindpath`) est séparé en couches :

- `domain` : les règles du jeu (personnage, labyrinthe, monstres, lumière, objets…), sans aucune dépendance à libGDX, ce qui permet de les tester facilement ;
- `infrastructure` : le chargement des cartes Tiled, le calcul de chemin et la physique ;
- `presentation` : les écrans, le HUD, l'affichage, le son et la lecture du clavier.

---

## Méthode de travail (Git)

- **Ne jamais coder directement sur `main` ou `dev`.** La branche `dev` reçoit le travail en cours ; `main` est mise à jour à chaque jalon.
- Pour chaque tâche, créez une branche à partir de `dev`, nommée d'après le code de la tâche :

  ```bash
  git switch dev
  git pull origin dev
  git switch -c feature/CODE-N-description
  ```

  Exemple : `feature/POC_MEDIUM-4-dynamite`

- Messages de commit en anglais, à l'impératif, avec un préfixe (`feat:`, `fix:`, `docs:`, `test:`, `refactor:`, `style:`, `build:`, `chore:`) et le code de la tâche à la fin :

  ```bash
  git commit -m "feat: add PlacedDynamite and make it explode once (POC_MEDIUM-4)"
  ```

- Avant de pousser, vérifiez que tout fonctionne localement :

  ```bash
  ./gradlew test
  ./gradlew lwjgl3:run
  ```

- Poussez ensuite votre branche et ouvrez une **Pull Request** vers `dev`, en la liant à son issue (`Part of #N` ou `Closes #N`). Les PR sont fusionnées en « Squash and merge ».

---

## Problèmes fréquents

- **Erreur de version Java :** si le projet refuse de compiler, vérifiez que votre éditeur et Gradle utilisent bien Java 21 (dans IntelliJ IDEA : File > Project Structure).
- **Images introuvables au lancement :** si le jeu s'arrête au démarrage parce qu'il ne trouve pas les images, vérifiez que le dossier de travail (*Working Directory*) de votre configuration de lancement pointe bien sur le dossier `assets`.

---

## Crédits

- **Sons et musiques :** [Pixabay](https://pixabay.com/) (libres de droits).
- **Graphismes :** générés par intelligence artificielle (ChatGPT).
