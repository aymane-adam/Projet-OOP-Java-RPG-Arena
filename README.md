# Arène RPG : simulateur de combat par équipes

Projet final du cours de programmation orientée objet en Java (JUNIA, M1).

## Objectif de l'application

L'application simule des combats au tour par tour entre des équipes de personnages
(guerriers, mages, soigneurs). Les équipes sont chargées depuis un fichier CSV au démarrage.
On peut ensuite :

- consulter les équipes et les statistiques des personnages ;
- lancer un **duel détaillé** entre deux équipes, avec le récit de chaque action ;
- lancer un **tournoi** où chaque équipe affronte toutes les autres, avec un classement
  final et des récompenses (plus gros cogneur, meilleur soigneur, finisseur).

## Lancer l'application

Prérequis : Java 17 ou plus récent, Maven 3.6 ou plus récent.

```bash
mvn compile exec:java
```

Pour charger un autre fichier de données :

```bash
mvn compile exec:java -Dexec.args="chemin/vers/mon-fichier.csv"
```

Il faut lancer la commande depuis la racine du projet : le fichier par défaut est
`data/characters.csv`.

## Lancer les tests

```bash
mvn test
```

Les tests se trouvent dans `src/test/java`. Ils utilisent JUnit 5 (Jupiter).

## Fonctionnalités et règles métier

| Règle | Où elle est codée |
|---|---|
| Ordre de jeu : le plus rapide agit en premier (le nom départage les égalités) | `BattleEngine` |
| Un personnage mis K.O. pendant le tour perd son action | `BattleEngine` |
| Dégâts = attaque × multiplicateur − défense × 0,5, avec 10 % de coups critiques (×1,5) et au minimum 1 | `StandardDamageCalculator` |
| Le guerrier passe en rage sous 50 % de PV et utilise *Frappe puissante* (×1,6) | `Warrior` |
| Le mage lance *Boule de feu* (20 mana, ×1,8) sur l'ennemi le plus dangereux, puis attaque au bâton quand il n'a plus de mana | `Mage` |
| Le soigneur soigne l'allié le plus blessé sous 50 % de PV s'il a le mana nécessaire, sinon il attaque | `Healer` |
| Les lanceurs de sorts régénèrent 10 % de leur mana max à la fin de chaque tour | `SpellCaster` |
| On ne peut ni attaquer ni soigner un personnage K.O., ni s'attaquer soi-même | `ActionPreconditions`, `DamageEffect` |
| Un sort trop cher échoue sans rien consommer ni toucher la cible | `Spell`, `SpellCaster` |
| Au bout de 50 tours, l'équipe qui a gardé la plus grande part de ses PV gagne ; à égalité, match nul | `BattleEngine` |
| Tournoi : victoire 3 pts, nul 1 pt, défaite 0 ; classement par points, puis victoires, puis nom | `TournamentService`, `TeamStanding` |
| Une équipe compte au plus 5 membres, sans doublon de nom | `Team` |

## Structure du projet

```
src/main/java/com/junia/arena/
├── Application.java          point d'entrée : assemble les objets
├── model/                    entités du domaine
│   ├── Character             classe abstraite de base
│   ├── SpellCaster           classe abstraite des lanceurs de sorts
│   ├── Warrior, Mage, Healer personnages concrets
│   ├── ManaUser              interface « possède du mana »
│   ├── Team, TurnDecision, CharacterType, CharacterFactory
├── action/                   ce qu'un personnage peut faire
│   ├── Action                interface commune
│   ├── Spell                 classe abstraite des sorts
│   ├── BasicAttack, PowerStrike, Fireball, HealingSpell
│   └── ActionResult, EffectType
├── service/                  logique métier
│   ├── BattleEngine          déroulement d'un combat
│   ├── TournamentService     tournoi et classement
│   ├── DamageCalculator      interface de calcul des dégâts
│   ├── StandardDamageCalculator
│   ├── BattleListener        interface d'observation du combat
│   └── BattleStatistics, BattleResult, TeamStanding
├── repository/               chargement des données
│   ├── TeamRepository        interface
│   └── CsvTeamRepository     implémentation CSV
├── ui/                       affichage console et menu
├── exception/                exceptions personnalisées
└── util/                     RandomSource (aléatoire injectable)
```

### Classes principales

- **`Character`** : état commun (PV, attaque, défense, vitesse) et règles communes
  (`takeDamage`, `heal`, `restore`). Chaque sous-classe implémente `decideTurn`.
- **`BattleEngine`** : fait jouer les tours, applique les actions et détermine le vainqueur.
- **`TournamentService`** : organise le tournoi « toutes contre toutes » et calcule le classement.
- **`CsvTeamRepository`** : lit le CSV, valide chaque ligne et crée les objets via `CharacterFactory`.
- **`ConsoleMenu`** : gère uniquement les échanges avec l'utilisateur.

## Héritage

```
Character (abstraite)
├── Warrior
└── SpellCaster (abstraite, implémente ManaUser)
    ├── Mage
    └── Healer

Spell (abstraite, implémente Action)
├── Fireball
└── HealingSpell
```

Chaque relation est une vraie relation « est un » : un mage **est un** lanceur de sorts,
qui **est un** personnage. `SpellCaster` regroupe la gestion du mana, identique pour les
mages et les soigneurs, au lieu de la dupliquer. `SpellCaster` redéfinit `restore()` et
`onRoundEnd()` en appelant `super` pour compléter le comportement du parent sans le remplacer.

## Polymorphisme

- `BattleEngine` manipule uniquement des `Character` : il appelle `character.decideTurn(...)`
  puis `decision.getAction().execute(...)`. Le comportement dépend du type réel (le guerrier
  frappe, le mage lance une boule de feu, le soigneur soigne) sans aucun `if` sur le type.
- `Action` : le moteur exécute une `Action` sans savoir s'il s'agit d'une attaque ou d'un sort.
- `BattleListener` : le moteur notifie une `List<BattleListener>`. Selon l'objet, cela
  affiche le combat (`ConsoleBattleView`), un résumé (`MatchSummaryView`) ou calcule des
  statistiques (`BattleStatistics`).

## Classes abstraites et interfaces

| Type | Nature | Pourquoi ce choix |
|---|---|---|
| `Character` | classe abstraite | Un « personnage générique » n'existe pas, mais tous partagent de l'état (PV...) et du code (`takeDamage`, `heal`). |
| `SpellCaster` | classe abstraite | État partagé (mana) et code commun aux mages et soigneurs. |
| `Spell` | classe abstraite | Tous les sorts suivent la même séquence (vérifier, payer le mana, appliquer l'effet). Elle est figée dans `execute` (méthode `final`) et seul `applyEffect` varie (patron *Template Method*). |
| `Action` | interface | Une attaque et un sort n'ont aucun état commun, seulement un contrat. |
| `ManaUser` | interface | Capacité que seuls certains personnages possèdent (ségrégation des interfaces). |
| `DamageCalculator`, `RandomSource`, `TeamRepository` | interfaces | Abstractions dont dépend la logique métier, pour pouvoir changer ou simuler l'implémentation (inversion des dépendances). |
| `BattleListener` | interface | Patron *Observer* ; méthodes `default` pour que chaque écouteur n'implémente que ce qui l'intéresse. |

## Exceptions personnalisées

| Exception | Type | Quand |
|---|---|---|
| `DataLoadingException` | vérifiée (`Exception`) | Fichier introuvable ou illisible, ligne mal formée, valeur invalide. Le message indique le numéro de ligne. |
| `InvalidCharacterException` | non vérifiée | Statistiques interdites (nom vide, PV ≤ 0, attaque négative...). |
| `InsufficientManaException` | non vérifiée | Un sort coûte plus de mana que le lanceur n'en possède. |
| `InvalidBattleException` | non vérifiée | Équipe contre elle-même, équipe sans combattant, tournoi à moins de deux équipes. |
| `InvalidTeamException` | non vérifiée | Équipe pleine ou doublon de nom. |

Des exceptions standard sont aussi utilisées quand elles suffisent :
`IllegalArgumentException` (dégâts négatifs, cible déjà K.O.), `IllegalStateException`
(un personnage K.O. tente d'agir), `NumberFormatException` (saisie utilisateur invalide).

Les exceptions sont attrapées là où on peut vraiment réagir : `Application` arrête
proprement le programme si les données sont invalides, et `ConsoleMenu` affiche un message
puis revient au menu si un duel est impossible ou si la saisie est incorrecte.

## Chargement des données

Au démarrage, `Application` crée un `CsvTeamRepository` sur `data/characters.csv`
(ou sur le chemin passé en argument) et appelle `loadTeams()`.

Format du fichier :

```
team,type,name,health,attack,defense,speed,mana
Les Lions,WARRIOR,Arthur,140,22,14,8,0
Les Lions,MAGE,Merlin,80,28,5,10,60
```

- les lignes vides, les commentaires (`#`) et la ligne d'en-tête sont ignorés ;
- le type n'est pas sensible à la casse (`WARRIOR`, `mage`...) ;
- la colonne `mana` peut rester vide pour un guerrier ;
- les personnages sont regroupés par équipe dans l'ordre du fichier (`LinkedHashMap`) ;
- `CharacterFactory` choisit la bonne sous-classe selon le type.

## Principes SOLID

- **S (responsabilité unique)** : chaque classe a une seule raison de changer. `CsvTeamRepository`
  lit le fichier, `BattleEngine` fait combattre, `TournamentService` classe, `ConsoleMenu`
  dialogue avec l'utilisateur, `ConsoleBattleView` affiche. `Application` ne fait qu'assembler les objets.
- **O (ouvert/fermé)** : pour ajouter une nouvelle action, il suffit d'implémenter `Action`.
  Pour ajouter un nouvel affichage ou une nouvelle statistique, il suffit d'implémenter
  `BattleListener`. Dans les deux cas, `BattleEngine` reste inchangé.
- **L (substitution de Liskov)** : partout où un `Character` est attendu, n'importe quelle
  sous-classe fonctionne. Les redéfinitions (`restore`, `onRoundEnd`) complètent le contrat
  du parent sans le casser.
- **I (ségrégation des interfaces)** : le mana est dans `ManaUser`, pas dans `Character`,
  donc les guerriers n'ont pas de méthodes de mana inutiles. Les méthodes `default`
  de `BattleListener` évitent d'imposer des méthodes vides.
- **D (inversion des dépendances)** : `BattleEngine` dépend de `DamageCalculator`, le calcul
  dépend de `RandomSource` et l'application dépend de `TeamRepository`. Les implémentations
  concrètes sont injectées par constructeur depuis `Application`. C'est ce qui permet aux
  tests d'utiliser un aléatoire fixe (`() -> 0.99`) ou des dégâts constants.

## Décisions techniques

1. **Aléatoire injecté via `RandomSource`.** Les coups critiques rendent le jeu vivant mais
   les tests doivent être déterministes. Une lambda remplace le hasard dans les tests.
2. **Échec immédiat sur une ligne CSV invalide.** Démarrer une partie avec des personnages
   silencieusement manquants fausserait les résultats. Le message donne le fichier, la ligne
   et la cause pour corriger rapidement.
3. **Le personnage choisit, le moteur exécute.** `decideTurn` renvoie un `TurnDecision`
   (action + cible) au lieu d'agir directement. La stratégie de chaque classe est donc
   testable seule, sans lancer de combat.
4. **Affichage par observateurs.** Le moteur ne fait aucun `System.out`. Cela permet un duel
   détaillé et un tournoi résumé avec le même moteur, et des tests silencieux.
5. **Limite de tours.** Deux soigneurs pourraient se soigner à l'infini. La limite garantit
   que le combat se termine, et la règle des PV restants désigne un vainqueur juste.

## Fonctionnalités Java utilisées

- **Collections** : `List`, `Map` (`LinkedHashMap` pour garder l'ordre, `HashMap` pour les
  statistiques), `Set` pour détecter une équipe inscrite deux fois.
- **Streams et lambdas** : filtres et tris (`Comparator.comparingInt(...).reversed()`),
  sommes de PV, recherche de la cible la plus faible.
- **`Optional`** : vainqueur d'un combat (vide en cas de nul), allié à soigner, meilleur joueur.
- **Enums** : `CharacterType` (avec recherche insensible à la casse), `EffectType`.
- **`equals` / `hashCode`** : redéfinis dans `Team` pour l'utiliser comme clé de `Map` et dans un `Set`.
- **API fichiers** : `Files.readAllLines` et `Path`.
