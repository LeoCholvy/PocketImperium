# Pocket Imperium

### Description
Pocket Imperium est une implémentation du jeu de société stratégique **Pocket Imperium**, développée en Java dans le cadre d'une unité d'enseignement sur la programmation orientée objet (POO) à l'Université de Technologie de Troyes (UTT). Le projet est conçu pour offrir une expérience de jeu fluide avec des fonctionnalités telles que la possibilité de sauvegarder et de reprendre une partie ultérieurement. Si le temps le permet, une interface graphique sera intégrée.

---

### Auteur
- **Léo Cholvy**
- **Rémy Pocin**

Ce projet est développé dans le cadre d’un projet de l'UE LO02 à l'UTT.

---

### Fonctionnalités principales
ATTENTION : EXEMPLE:
- **Jeu complet** : Permet de jouer à Pocket Imperium en version numérique.
- **Sauvegarde** : Capacité de sauvegarder la progression du jeu pour une reprise ultérieure.
- **Interface graphique** *(potentielle)* : Si le temps le permet, une interface graphique sera développée pour améliorer l’expérience utilisateur.
- **Fonctionnalités futures** : Après la finalisation des fonctionnalités principales, plusieurs améliorations peuvent être envisagées pour enrichir l'expérience de jeu. Parmi celles-ci, on peut inclure :
  - **Multijoueur en ligne** : Permettre à plusieurs joueurs de s'affronter en ligne.
  - **Intelligence artificielle** : Ajouter des adversaires contrôlés par l'ordinateur pour jouer en solo.
  - **Extensions de jeu** : Intégrer des extensions ou des variantes de règles pour diversifier les parties.
  - **Statistiques et classements** : Suivre les performances des joueurs et afficher des classements.
  - **Personnalisation** : Permettre aux joueurs de personnaliser leurs avatars et leurs vaisseaux.

### Fonctionnalités à implémenter
- **historique** : Statistiques des parties précédentes, historique de partie et des parties
  - **visualisation** : Visualisation des parties précédentes et graphiques
  - **classement** : Classement des joueurs
  - **annulation** : Pouvoir revenir en arrière dans une partie
- **configuration** : Configuration de la partie
  - **règles** : choisir l'ordre de priorité des actions
ATTENTION : EXEMPLE
- **Interface graphique** *(potentielle)* : Si le temps le permet, une interface graphique sera développée pour améliorer l’expérience utilisateur.
- **Fonctionnalités futures** : Après la finalisation des fonctionnalités principales, plusieurs améliorations peuvent être envisagées pour enrichir l'expérience de jeu. Parmi celles-ci, on peut inclure :
    - **Multijoueur en ligne** : Permettre à plusieurs joueurs de s'affronter en ligne.
    - **Intelligence artificielle** : Ajouter des adversaires contrôlés par l'ordinateur pour jouer en solo.
    - **Extensions de jeu** : Intégrer des extensions ou des variantes de règles pour diversifier les parties.
    - **Statistiques et classements** : Suivre les performances des joueurs et afficher des classements.
    - **Personnalisation** : Permettre aux joueurs de personnaliser leurs avatars et leurs vaisseaux.
- **Modifications prévues sans devoir coder** :
    - **Carte personnalisée** : Permettre de créer sa propre carte de jeu. En modifiant le fichier map.properties, on peut modifier la carte (ajouter des obstacles, des tunnels, etc.).
---

### Installation

Pour installer et exécuter **Pocket Imperium**, vous devez :
1. **Pré-requis** : Assurez-vous d'avoir le **JDK 23** installé sur votre machine.
2. **Exécution** : Clonez le dépôt, puis lancez le fichier `Main.java` pour démarrer le jeu.

```bash
# Installation des dépendances (si nécessaire)
# Exécution
java Main
```

Aucune bibliothèque spécifique n'est encore utilisée pour ce projet. Cela peut évoluer au fur et à mesure du développement.

---

### Organisation du Code Source

Le projet est actuellement en phase de conception, donc la structure du code source n'est pas encore finalisée. Une fois le développement en cours, cette section sera mise à jour pour inclure la description des principaux répertoires et fichiers.

---

### Inputs du jeu
Lorsque le jeu a besoin d'un input, il utilise les méthodes de la classe IOHandler.

En fonction du mode d'entrée choisi, IOHandler appelle les méthodes de la bonne classe (CLI, GUI, etc).

---

### Licence

Nous n'avons pas encore décidé de la licence sous laquelle ce projet sera distribué. Si vous avez besoin d'une suggestion, des licences comme **MIT** ou **GPL v3** sont couramment utilisées pour les projets open-source. Cela sera déterminé au cours du développement.

---

### Version

**Pocket Imperium** est actuellement en phase de conception. La première version jouable sera disponible après la finalisation des fonctionnalités principales.

---

### Contact

Pour toute question ou assistance, veuillez contacter les auteurs à travers les plateformes universitaires.

---

### Historique des modifications

- **Version 0.1 - Conception** : Début du projet, travail sur la conception des classes et des fonctionnalités.

---

Ce README peut être enrichi à mesure que le projet progresse, notamment avec des détails sur la structure du code, les bibliothèques utilisées et des instructions plus précises pour l'installation et l'utilisation.

Si vous avez des préférences pour une licence, la **MIT** est généralement une bonne option pour les petits projets open-source, car elle est très permissive.