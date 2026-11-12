# Blind Path

## Présentation du jeu

### Pitch

Lors de sa toute première expédition avec son père, un jeune gnome chute dans un ancien puits de mine et se réveille au plus profond d'un donjon maléfique. À ses côtés, une lanterne magique, qui n'éclaire que lui. D'un claquement de doigts, il peut en réveiller le pouvoir et révéler un bref instant ce qui l'entoure, mais le bruit produit attire ce qui rôde dans le noir...

Parcourez vingt niveaux pour remonter à la surface, déjouez les pièges et découvrez les secrets du donjon pour retrouver les vôtres. Chaque claquement est un choix crucial : voir ou rester discret.

### Gameplay

Le joueur évolue dans une série de labyrinthes plongés dans l'obscurité. À chaque claquement, le halo de lumière de la lanterne s'élargit un court instant, ce qui permet au joueur de se repérer. Mais les monstres, qui se déplacent déjà de manière autonome, sont attirés vers le bruit qu'il fait alors.

Les vingt niveaux se divisent en quatre paliers de cinq niveaux. Chaque palier correspond à une difficulté croissante : facile, moyen, difficile, extrême. Chaque niveau ne compte qu'un ennemi ou aucun. Le joueur ne peut pas tuer les monstres, seulement les fuir ou les repousser. En cas de mort, il reprend au premier niveau du dernier palier atteint et conserve son inventaire. Néanmoins, en contrepartie des informations qu'il a pu obtenir lors sa précédente exploration, les monstres deviennent plus redoutables.

Le joueur peut aussi interragir avec les murs en activant des leviers ou grâce au claquement. Pour s'aider, il peut compter sur les objets aux effets variés qu'il trouve au cours de son exploration ainsi que sur les compétences qu'il débloque à mesure qu'il progresse.

### Inspiration

Le jeu s'inspire principalement de la série *Little Nightmares*, qui met en scène des personnages enfantins confrontés à des monstres aussi difformes que meurtriers. Le contraste entre l'apparence du gnome et celle des monstres y puise son inspiration, de même que l'incapacité du joueur à vaincre les ennemis et la fuite comme seule échappatoire.

Une autre inspiration est *The Binding of Isaac*, là aussi en raison du contraste entre l'aspect du protagoniste et celui des ennemis, mais aussi par le système d'objets : dans ce jeu comme dans *Blind Path*, le joueur ramasse des objets qui modifient les capacités du personnage, et gagne des pièces qu'il dépense dans une boutique.

Enfin, *Blind Path* s'inspire du genre du roguelite, dans lequel on conserve tout ou partie de son inventaire ou de ses améliorations après une défaite.

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

Avant de cloner, assurez-vous d'avoir installé :

- **Java JDK 21** (ou la version exacte configurée dans votre `build.gradle`)
- **IntelliJ IDEA** ou **VsCode**

---

## Installation et Lancement Rapide

1. **Cloner le dépôt :**

   ```bash
   git clone -b dev git@github.com:Aure-0411/2D-Game---Blind-Path.git
   cd 2D-Game---Blind-Path
   ```

2. **Importer le projet dans l'IDE :**
   - Ouvrez votre IDE (IntelliJ / VsCode).
   - Choisissez **Open** (Ouvrir) et sélectionnez le fichier **`build.gradle`** à la racine du projet.
   - Laissez l'IDE télécharger automatiquement toutes les dépendances (cela peut prendre quelques minutes au premier lancement).

3. **Lancer le jeu (Desktop/LWJGL3) :**
   - **Via l'IDE :** Trouvez la classe `Lwjgl3Launcher` dans le module `lwjgl3`, faites un clic droit et sélectionnez **Run**.
   - **Via le terminal :**
     ```bash
     ./gradlew lwjgl3:run
     ```

---

## Architecture du Projet

- `/core` : Contient toute la logique du jeu, les écrans (Screens) et les entités. **C'est ici qu'on code le jeu.**
- `/assets` : Contient les images, musiques, sons et polices de caractères du jeu.
- `/lwjgl3` : Contient le lanceur spécifique pour ordinateurs (Windows/Mac/Linux).

---

### Workflow Git (Branches)

- **Ne jamais coder directement sur `main` ou `dev`.**
- Pour chaque nouvelle fonctionnalité ou correction (ex. : ajouter le joueur, créer le menu), créez une branche dédiée à partir de `dev` :

  ```bash
  git checkout dev
  git pull origin dev
  git checkout -b type/issue-N-description
  ```

  Respectez impérativement la convention de nommage suivante : type/issue-N-description, où N correspond au numéro de l'issue GitHub associée.

  Exemple : feat/issue-12-player-movement

- Respectez également la convention Conventional Commits pour vos messages de commit :

  ```bash
  "feat(player): add player movement (#12)"
  ```

  Le numéro #12 correspond à l'issue GitHub associée au changement.

- Une fois vos modifications terminées, **assurez-vous qu'elles sont testées et que le projet fonctionne localement** :
  ```bash
  ./gradlew test
  ./gradlew lwjgl3:run
  ```
- Vérifiez la couverture des tests avec JaCoCo :

  L'exécution de ./gradlew test génère automatiquement les rapports JaCoCo.

  Consultez le rapport HTML situé dans core/build/reports/jacoco/test/html/index.html pour visualiser la couverture du code et identifier les parties qui ne sont pas couvertes par les tests.

  Assurez-vous que les tests couvrent correctement les fonctionnalités ajoutées ou modifiées.

- Poussez ensuite votre branche sur le dépôt distant et ouvrez une **Pull Request (PR)** sur GitHub en ciblant la branche `dev`.

### Où ajouter votre code ?

- Tout le code du jeu (logique, physique, affichage) doit être écrit **exclusivement dans le module `/core`**.
- Ne touchez pas aux modules `/lwjgl3` ou `/android` sauf si vous devez modifier la configuration de lancement (taille de la fenêtre de départ, orientation du téléphone, etc.).

## Problèmes fréquents (Dépannage)

- **Erreur de version Java :** Si le projet refuse de compiler, vérifiez dans l'IDE que le `Project SDK` et la version de Gradle utilisent bien Java 21 (File > Project Structure).
- **Fichiers Assets non trouvés :** Dans IntelliJ, si le jeu plante au démarrage car il ne trouve pas les images, vérifiez que le _Working Directory_ de votre configuration de lancement pointe bien sur le dossier `assets` (ou la racine du projet selon votre configuration GDX-Liftoff).