> Ce dépôt contient le code source complet de Mémoire 0.4.0 et ses tests. Aucune clé de signature privée, donnée personnelle, sauvegarde ou pièce jointe utilisateur n’y est publiée.
> Les APK générés sur une autre machine auront une autre signature, sauf réutilisation privée de la clé originale. Ne pas désinstaller l’application existante pour changer de signature sans avoir exporté une sauvegarde.

# Mémoire 0.4.0 — version de développement

Application Android française et locale, sans compte, serveur ni permission Internet. Nouvelle version étendue ; toutes les idées proposées ne sont pas encore intégrées. Lire les limites et exporter une sauvegarde depuis la version précédente avant de l'installer.

## Installer la mise à jour sur le Pixel 6a
1. Dans l'ancienne version, exporter une sauvegarde et en conserver une copie ailleurs.
2. Télécharger Memoire-0.4.0.apk et l'ouvrir depuis Files/Téléchargements.
3. Autoriser cette source d'installation si Android le demande.
4. Installer par-dessus la version précédente, sans désinstaller.
5. Vérifier quelques anciennes fiches, leurs fichiers et l'export/restauration avant de poursuivre l'usage quotidien.

Identifiant bf.memoire et certificat de signature conservés, versionCode 4. La mise à jour migre les anciennes données JSON vers SQLite. Le JSON d'origine n'est supprimé qu'après la réussite de la transaction de migration. Cette migration n'a pas encore été exécutée sur téléphone ou émulateur. Une erreur de lecture/migration affiche une erreur et conserve la source.

## Fonctionnalités développées

| Domaine | Présent dans le code de cette version |
| --- | --- |
| Espaces | Études, Travail, Personnel, Projets ; ajout, renommage, changement d'espace, recherche locale ou globale |
| Capture | Nouvelle fiche placée à classer, partage Android texte/fichier, photo via l'application appareil photo, note vocale locale |
| Classement | Matière/projet, étiquettes, statut, favoris, archives, déplacement vers un espace et classement différé |
| Modèles | Six modèles de départ, création depuis une structure de fiche, réutilisation, renommage, suppression du modèle sans supprimer les fiches |
| Champs | Texte, texte long, code, nombre, date, URL, liste textuelle, choix simple/multiple, case à cocher, relation vers une fiche ; renommage, ordre, obligation signalée |
| Document | Blocs texte, titre, liste, code, citation, tableau à cellules, tâche, lien, commande, résultat, question/réponse, image et fichier ; déplacement et suppression |
| Recherche | Insensible aux accents/casse, recherche multi-mots, tolérance à une erreur sur les mots de plus de quatre lettres ; filtres étiquette/projet/statut/favoris/date ; recherches enregistrées |
| Fichiers | Copie privée, bibliothèque par espace, taille lisible, ouverture externe, renommage, remplacement/retrait dans une fiche, détection de doublons par SHA-256 |
| Images | Prévisualisation échantillonnée dans les blocs ; annotations au doigt conservées comme une nouvelle image, sans changer l'original |
| Révision | Questions/réponses avec réponse masquée, session de révision, revue à un ou sept jours selon l'évaluation personnelle |
| Rappels | Notifications facultatives par fiche, avec restauration des rappels futurs après redémarrage ou import |
| Sauvegarde | ZIP complet ou archive .memoire chiffrée par mot de passe ; dernière date d'export ; rappel discret après sept jours activable |
| Restauration | Validation avant remplacement ; fusion globale ou de l'espace actuel ; les fiches avec un identifiant déjà présent sont conservées localement ; conflit de fichier différent sous le même ID : annulation |
| Export ouvert | ZIP contenant Markdown et fichiers originaux ; export d'une fiche via le service d'impression Android, avec option Enregistrer au format PDF |
| Récupération | Corbeille, suppression définitive confirmée, jusqu'à vingt anciennes versions du contenu et des champs ; blocs et références de fichiers restaurables |
| Confidentialité | Verrouillage biométrique facultatif avec recours au code Android ; contenu des notes et métadonnées chiffré dans SQLite via Android Keystore |
| Interface | Trois onglets, cartes, menus secondaires, thèmes Android clair/sombre, taille de texte réglable, actions principales visibles |

## Utilisation
- Accueil : choisir un espace, retrouver favoris/récents, consulter À classer, Réviser ou Archives.
- Fiches : Projets, Modèles et Filtrer. Le bouton Nouvelle fiche capture sans imposer un formulaire complet.
- Une fiche : choisir l'espace/le statut en haut. Champ, Fichier et Bloc en bas. Le menu ••• contient les actions secondaires, le classement, l'archivage, les exports et suppressions.
- Un bloc : ••• permet de monter, descendre ou supprimer. Les commandes/code ont un bouton Copier. Un tableau possède un éditeur de cellules.
- Réglages : sauvegarde, restauration, bibliothèque, modèles, recherches enregistrées, verrouillage et taille du texte.

Les champs obligatoires signalent une valeur manquante ; ils ne bloquent pas une capture brouillon. Les projets/matières sont actuellement des noms de regroupement, pas des entités dotées de leur propre tableau de bord.

## Sécurité et sauvegardes
Le contenu JSON des fiches, versions et métadonnées est chiffré avec AES-GCM dans SQLite, clé gérée par Android Keystore. Les pièces jointes sont dans le stockage privé mais ne sont pas chiffrées individuellement. Le verrouillage protège l'ouverture d'une nouvelle activité ; il ne reverrouille pas automatiquement à chaque retour depuis l'arrière-plan.

La sauvegarde chiffrée protège données et fichiers avec AES-GCM et une clé dérivée par PBKDF2-HMAC-SHA256, sel aléatoire et 150 000 itérations. Son mot de passe n'est pas récupérable. Il n'est pas enregistré dans le projet ni dans la sauvegarde. Si l'activité est recréée avant l'export et perd ce mot de passe, l'export chiffré est interrompu plutôt que produit en clair.

Un ZIP standard ou un export Markdown reste en clair. Une sauvegarde sur le même téléphone ne protège pas contre sa perte. Désinstaller efface les données locales et la clé locale. Pour changer de téléphone, utiliser une sauvegarde exportée puis l'importer ; copier directement le fichier SQLite ne suffit pas.

La restauration est limitée à 1 Go décompressé et 32 Mo de JSON. Les entrées inconnues, chemins dangereux, entrées dupliquées, fichiers manquants et objets invalides sont refusés. Une fusion préserve les fiches locales sur conflit d'ID ; elle n'est pas une synchronisation de modifications de la même fiche.

## Validation effectuée
- Compilation Java 17 contre SDK Android 35, compilation des ressources, génération DEX, assemblage, alignement et signatures v2/v3 : réussis.
- Certificat identique à la version 0.3.0 et versionCode augmenté : vérifiés.
- 23 tests exécutés : huit validations de sauvegarde, cinq tests de chiffrement (aller-retour, mauvais mot de passe, altération, troncature, format), cinq tests de recherche, cinq tests d'import ZIP.
- Aucun test d'installation, de migration SQLite/Keystore, d'interface, de biométrie, d'appareil photo, de microphone, d'impression PDF ou de rappel sur appareil/émulateur. Les tests de chiffrement d'archive ne valident pas le Keystore Android.
- Le build utilise build_local.py et les outils officiels du SDK. Gradle/lint n'a pas été exécuté avec succès dans cet environnement. Le workflow GitHub fourni n'a pas été lancé.

## Améliorations encore absentes ou partielles
- Synchronisation automatique, application ordinateur et gestion de conflits entre appareils : absentes. L'échange manuel de sauvegardes et l'export Markdown sont présents.
- OCR des images et recherche dans le texte des PDF/documents : absents. Les noms de fichiers sont recherchés.
- Visualiseur PDF/vidéo/audio intégré : absent ; ouverture avec une application compatible installée.
- Éditeur visuel complet avec gras/italique à l'intérieur du texte, déplacement par glisser-déposer, équations et schémas vectoriels : absent. Blocs éditables et déplacement par menu présents. Les annotations sont des traits rouges, sans zoom ni outil texte.
- Relations multiples, champ fichier dédié, date-heure combinée, listes à éléments structurés et édition des définitions de modèles : partiels/absents. La relation actuelle pointe vers une fiche.
- Comparaison visuelle de versions et bouton Annuler général : absents ; restauration de versions et corbeille présentes.
- Espaces supprimables, tableau de bord détaillé des projets, suppression de recherches enregistrées et pagination sur gros volumes : à développer.
- La recherche déchiffre les fiches en mémoire ; aucune indexation plein texte persistante n'expose leur contenu en clair. Les performances sur des milliers de fiches ou très gros historiques ne sont pas mesurées.
- Fichiers chiffrés individuellement, export chiffré sans fichier temporaire clair et audit de sécurité complet : à développer. Les fichiers temporaires d'export/import sont supprimés à la fin.
- Annulation d'un transfert en cours et reprise des enregistrements après arrêt forcé : absentes. Quitter l'application arrête l'enregistrement vocal.
- Restauration sélective fiche par fiche : absente ; sélection d'un espace en mode fusion présente.

## Recompiler
JDK 17, SDK Android avec platforms;android-35 et build-tools;35.0.0 :

```
python3 build_local.py --sdk /chemin/vers/sdk --jdk /chemin/vers/jdk
```

Le script produit Memoire-0.4.0.apk et vérifie signature et alignement. Configuration : minSdk 26, targetSdk 34, compileSdk 35. Actualiser le ciblage avant une distribution publique.

La clé de signature originale n’est pas incluse dans le dépôt. Le script build_local.py crée une nouvelle clé locale de développement si aucune n’est présente ; signing/ est exclu de Git. Pour produire des mises à jour de l’installation existante, réutiliser la clé originale en privé. Le workflow GitHub produit un APK debug avec une autre clé : ne pas le substituer à cette installation pour une mise à jour.

## Vérifications prioritaires sur le Pixel
Sauvegarder avant installation ; vérifier les anciennes fiches et fichiers après migration. Créer deux espaces et vérifier le périmètre de recherche. Classer une capture, ajouter plusieurs types de blocs, déplacer un bloc, éditer un tableau, joindre/remplacer une image. Exporter en ZIP et en chiffré, importer avec mauvais mot de passe puis avec le bon. Tester la fusion et vérifier qu'une fiche existante n'est pas remplacée. Tester biométrie/code, notifications, photo, note vocale, rotation et impression PDF. Conserver une sauvegarde externe pendant ces vérifications.
