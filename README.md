# Blind Path libGDX

Description rapide du jeu (ex: Jeu de plateforme 2D développé avec libGDX).

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