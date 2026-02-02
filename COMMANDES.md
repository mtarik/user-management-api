# 🛠️ Commandes Pratiques

Référence rapide des commandes utiles pour utiliser le système de revue de code IA.

---

## 📥 Installation Initiale

### Copier les Fichiers

```bash
# Dans votre projet Java
PROJECT_PATH="/chemin/vers/votre/projet"
AI_REVIEW_PATH="/chemin/vers/java-ai-code-review"

cd $PROJECT_PATH

# Créer la structure
mkdir -p .github/workflows
mkdir -p .github/config
mkdir -p scripts

# Copier les fichiers
cp $AI_REVIEW_PATH/.github/workflows/ai-code-review.yml .github/workflows/
cp $AI_REVIEW_PATH/.github/config/checkstyle.xml .github/config/
cp $AI_REVIEW_PATH/.github/config/pmd-ruleset.xml .github/config/
cp $AI_REVIEW_PATH/scripts/ai_code_reviewer.py scripts/
cp $AI_REVIEW_PATH/scripts/requirements.txt scripts/
```

### Configurer le Secret GitHub (via CLI)

```bash
# Installer GitHub CLI si nécessaire
# https://cli.github.com/

# Se connecter
gh auth login

# Ajouter le secret
gh secret set ANTHROPIC_API_KEY
# Puis coller votre clé API quand demandé
```

---

## 🔄 Workflow Quotidien

### Créer une Feature avec Revue Auto

```bash
# 1. Créer et basculer sur une branche feature
git checkout -b feature/ma-nouvelle-feature

# 2. Développer le code
# ... éditer vos fichiers Java ...

# 3. Ajouter et commiter
git add .
git commit -m "feat: Implémenter ma nouvelle fonctionnalité"

# 4. Pousser (déclenche l'analyse)
git push origin feature/ma-nouvelle-feature

# 5. Créer la PR via CLI
gh pr create --title "Feature: Ma nouvelle fonctionnalité" --body "Description de la feature"

# Le bot commente automatiquement dans quelques minutes !
```

### Voir les Workflows en Cours

```bash
# Lister les workflows
gh workflow list

# Voir les exécutions récentes
gh run list --workflow="ai-code-review.yml"

# Voir les détails d'une exécution
gh run view <run-id>

# Voir les logs
gh run view <run-id> --log
```

---

## 🧪 Tests Locaux

### Tester le Script Python Localement

```bash
# 1. Définir la clé API
export ANTHROPIC_API_KEY="sk-ant-votre-cle-api"

# 2. Générer la liste des fichiers modifiés
git diff --name-only HEAD~1 HEAD | grep '\.java$' > changed_files.txt

# Ou comparer avec une autre branche
git diff --name-only main...HEAD | grep '\.java$' > changed_files.txt

# 3. Exécuter le script
python scripts/ai_code_reviewer.py

# 4. Voir les rapports
cat code_review_*.md
```

### Tester avec des Fichiers Spécifiques

```bash
# Créer manuellement la liste des fichiers à analyser
echo "src/main/java/com/example/MyClass.java" > changed_files.txt
echo "src/main/java/com/example/AnotherClass.java" >> changed_files.txt

# Exécuter l'analyse
export ANTHROPIC_API_KEY="sk-ant-..."
python scripts/ai_code_reviewer.py
```

---

## 🔍 Analyse et Debugging

### Vérifier les Fichiers Modifiés dans un Commit

```bash
# Fichiers modifiés dans le dernier commit
git diff --name-only HEAD~1 HEAD

# Seulement les fichiers Java
git diff --name-only HEAD~1 HEAD | grep '\.java$'

# Fichiers modifiés depuis main
git diff --name-only main...HEAD | grep '\.java$'
```

### Voir le Diff Complet

```bash
# Diff du dernier commit
git diff HEAD~1 HEAD

# Diff d'un fichier spécifique
git diff HEAD~1 HEAD src/main/java/MyClass.java

# Diff depuis main
git diff main...HEAD
```

### Vérifier la Configuration GitHub Actions

```bash
# Valider la syntaxe du workflow
# (nécessite act: https://github.com/nektos/act)
act -l

# Tester localement avec act
act pull_request
```

---

## 📦 Maven / Gradle

### Exécuter les Analyses Statiques Localement

#### Maven

```bash
# Checkstyle
mvn checkstyle:check

# PMD
mvn pmd:check

# SpotBugs
mvn spotbugs:check

# Tout en un
mvn clean compile checkstyle:check pmd:check spotbugs:check
```

#### Gradle

```bash
# Checkstyle
./gradlew checkstyleMain

# PMD
./gradlew pmdMain

# SpotBugs
./gradlew spotbugsMain

# Tout en un
./gradlew check
```

---

## 🗂️ Gestion des Artefacts

### Télécharger les Rapports via CLI

```bash
# Lister les artefacts d'une exécution
gh run view <run-id> --log

# Télécharger tous les artefacts
gh run download <run-id>

# Télécharger un artefact spécifique
gh run download <run-id> --name ai-review-report-*
```

### Trouver le Run ID

```bash
# Dernières exécutions
gh run list --workflow="ai-code-review.yml" --limit 5

# Exécution pour une branche spécifique
gh run list --branch feature/ma-feature

# Exécution pour un commit
gh run list --commit a3f8b92
```

---

## 🔄 Maintenance

### Mettre à Jour le Script Python

```bash
# Depuis le repo java-ai-code-review
cd /chemin/vers/java-ai-code-review
git pull

# Dans votre projet
cd /chemin/vers/votre/projet
cp /chemin/vers/java-ai-code-review/scripts/ai_code_reviewer.py scripts/
git add scripts/ai_code_reviewer.py
git commit -m "chore: Mettre à jour le script AI reviewer"
git push
```

### Mettre à Jour le Workflow

```bash
cp /chemin/vers/java-ai-code-review/.github/workflows/ai-code-review.yml .github/workflows/
git add .github/workflows/ai-code-review.yml
git commit -m "chore: Mettre à jour le workflow AI review"
git push
```

---

## 🔐 Gestion des Secrets

### Lister les Secrets

```bash
gh secret list
```

### Mettre à Jour un Secret

```bash
gh secret set ANTHROPIC_API_KEY
# Coller la nouvelle valeur
```

### Supprimer un Secret

```bash
gh secret remove ANTHROPIC_API_KEY
```

---

## 📊 Statistiques et Rapports

### Voir l'Historique des Analyses

```bash
# Dernières 10 exécutions avec statut
gh run list --workflow="ai-code-review.yml" --limit 10 --json status,conclusion,createdAt,headBranch

# Exécutions échouées uniquement
gh run list --workflow="ai-code-review.yml" --json status,conclusion --jq '.[] | select(.conclusion == "failure")'
```

### Compter les Analyses par Branche

```bash
# Analyses sur les branches feature
gh run list --workflow="ai-code-review.yml" --json headBranch | jq '.[].headBranch' | grep feature | wc -l
```

---

## 🎯 Commandes de Dépannage

### Le Workflow Ne Se Lance Pas

```bash
# Vérifier que le fichier workflow existe
ls -la .github/workflows/ai-code-review.yml

# Vérifier la syntaxe YAML
cat .github/workflows/ai-code-review.yml | python -c "import yaml, sys; yaml.safe_load(sys.stdin)"

# Vérifier les permissions
gh api repos/:owner/:repo/actions/permissions
```

### Réexécuter une Analyse Échouée

```bash
# Réexécuter le dernier run
gh run rerun $(gh run list --workflow="ai-code-review.yml" --limit 1 --json databaseId --jq '.[0].databaseId')

# Réexécuter un run spécifique
gh run rerun <run-id>

# Réexécuter seulement les jobs échoués
gh run rerun <run-id> --failed
```

### Forcer une Analyse Manuelle

```bash
# Déclencher le workflow manuellement
gh workflow run ai-code-review.yml --ref feature/ma-branche
```

---

## 🧹 Nettoyage

### Supprimer les Anciens Artefacts

```bash
# Lister les artefacts (via API)
gh api repos/:owner/:repo/actions/artifacts

# Supprimer les artefacts de plus de 30 jours
# (ils sont automatiquement supprimés selon retention-days: 30)
```

### Nettoyer les Branches Feature Mergées

```bash
# Lister les branches mergées
git branch --merged main

# Supprimer localement
git branch --merged main | grep feature/ | xargs git branch -d

# Supprimer sur le remote
git branch -r --merged main | grep origin/feature/ | sed 's/origin\///' | xargs -I {} git push origin --delete {}
```

---

## 📝 Commandes Git Utiles

### Workflow Complet avec Squash

```bash
# 1. Créer la feature
git checkout -b feature/nouvelle-feature

# 2. Plusieurs commits
git add .
git commit -m "work in progress"
# ... plus de travail ...
git add .
git commit -m "more changes"

# 3. Squash avant de pousser
git rebase -i main
# Dans l'éditeur: marquer les commits à squash

# 4. Pousser (force si déjà poussé)
git push origin feature/nouvelle-feature --force-with-lease

# 5. Créer la PR
gh pr create
```

### Synchroniser avec Main

```bash
# Mettre à jour main
git checkout main
git pull origin main

# Rebaser la feature sur main
git checkout feature/ma-feature
git rebase main

# Ou merger main dans la feature
git merge main

# Pousser
git push origin feature/ma-feature --force-with-lease
```

---

## 🐍 Python - Environnement Virtuel (Optionnel)

### Pour Développement Local

```bash
# Créer un environnement virtuel
python -m venv venv

# Activer (Windows)
venv\Scripts\activate

# Activer (Linux/Mac)
source venv/bin/activate

# Installer les dépendances
pip install -r scripts/requirements.txt

# Exécuter le script
python scripts/ai_code_reviewer.py

# Désactiver
deactivate
```

---

## 📚 Références Rapides

### Alias Git Utiles

```bash
# Ajouter à ~/.gitconfig

[alias]
    # Fichiers Java modifiés depuis main
    java-diff = "!git diff --name-only main...HEAD | grep '\\.java$'"
    
    # Créer une branche feature
    feat = "!f() { git checkout -b feature/$1; }; f"
    
    # Pousser et créer PR
    pr = "!f() { git push origin HEAD && gh pr create; }; f"
    
    # Voir les runs du workflow
    runs = "!gh run list --workflow=ai-code-review.yml --limit 5"
```

### Variables d'Environnement

```bash
# Dans ~/.bashrc ou ~/.zshrc

# Clé API Claude (pour tests locaux)
export ANTHROPIC_API_KEY="sk-ant-votre-cle"

# Alias pour le script
alias ai-review="python scripts/ai_code_reviewer.py"
```

---

## ⚡ One-Liners Pratiques

```bash
# Analyser les changements depuis main
git diff --name-only main...HEAD | grep '\.java$' > changed_files.txt && python scripts/ai_code_reviewer.py

# Voir le dernier rapport
cat $(ls -t code_review_*.md | head -1)

# Compter les fichiers Java dans le projet
find . -name "*.java" | wc -l

# Fichiers Java modifiés dans les 7 derniers jours
find . -name "*.java" -mtime -7

# Taille du plus gros fichier Java
find . -name "*.java" -exec du -h {} + | sort -rh | head -1
```

---

**💡 Astuce** : Créez vos propres alias et scripts pour automatiser vos tâches récurrentes !

---

*Dernière mise à jour : Février 2026*
