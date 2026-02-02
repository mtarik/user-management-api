# 🌐 Configuration Centralisée - Multi-Repositories

Ce guide explique comment déployer le système de revue de code IA sur **plusieurs repositories** en utilisant une approche centralisée.

## 📋 Table des matières

- [Vue d'ensemble](#vue-densemble)
- [Avantages de l'approche centralisée](#avantages)
- [Architecture](#architecture)
- [Installation](#installation)
- [Configuration par repository](#configuration-par-repository)
- [Gestion des clés API](#gestion-des-clés-api)
- [Personnalisation](#personnalisation)
- [Dépannage](#dépannage)

---

## 🎯 Vue d'ensemble

Cette approche vous permet de :
- ✅ Maintenir **une seule version** du code de revue IA
- ✅ Déployer sur **plusieurs repositories** facilement
- ✅ Mettre à jour **tous les repos** en une seule modification
- ✅ Centraliser la **gestion des configurations**

---

## 🌟 Avantages

| Avantage | Description |
|----------|-------------|
| **Maintenance simplifiée** | Modifiez le script une fois, tous les repos en bénéficient |
| **Déploiement rapide** | Ajoutez un nouveau repo en 2 minutes |
| **Cohérence** | Tous les repos utilisent les mêmes règles |
| **Économies** | Une seule clé API peut être partagée (au niveau org) |

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────┐
│  Repository Central: java-ai-code-review            │
│  ┌───────────────────────────────────────────────┐  │
│  │ .github/workflows/reusable-ai-review.yml      │  │
│  │ scripts/ai_code_reviewer.py                   │  │
│  │ scripts/requirements.txt                      │  │
│  └───────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────┘
                        ▲
                        │ Appelle le workflow réutilisable
                        │
        ┌───────────────┼───────────────┐
        │               │               │
┌───────┴───────┐ ┌─────┴──────┐ ┌─────┴──────┐
│   Repo A      │ │   Repo B   │ │   Repo C   │
│  (Project 1)  │ │ (Project 2)│ │ (Project 3)│
│               │ │            │ │            │
│ .github/      │ │ .github/   │ │ .github/   │
│  workflows/   │ │  workflows/│ │  workflows/│
│   ai-code-    │ │   ai-code- │ │   ai-code- │
│   review.yml  │ │   review.  │ │   review.  │
│   (3 lignes)  │ │   yml      │ │   yml      │
└───────────────┘ └────────────┘ └────────────┘
```

---

## 🚀 Installation

### Étape 1 : Préparer le repository central

1. **Créer/Utiliser ce repository comme central**

   Ce repository (`java-ai-code-review`) servira de source centrale.

2. **Le rendre public ou accessible**

   Le repository doit être accessible par vos autres repositories :
   - **Public** : Accessible à tous (recommandé pour open source)
   - **Privé** : Nécessite des permissions d'accès (voir section suivante)

3. **Pousser le code sur GitHub**

   ```bash
   cd c:\Users\tarik\java-ai-code-review

   # Si ce n'est pas encore un repo git
   git init
   git add .
   git commit -m "Configuration centralisée du système de revue IA"

   # Créer un repo sur GitHub et le lier
   git remote add origin https://github.com/VOTRE_USERNAME/java-ai-code-review.git
   git branch -M main
   git push -u origin main
   ```

### Étape 2 : Configuration pour repository privé (optionnel)

Si votre repository central est **privé**, vous devez autoriser les autres repos à y accéder :

#### Option A : Utiliser un Personal Access Token (PAT)

1. Créez un PAT sur GitHub :
   - Settings → Developer settings → Personal access tokens → Tokens (classic)
   - Cochez : `repo`, `workflow`

2. Dans chaque repository appelant :
   - Settings → Secrets and variables → Actions
   - Créez un secret : `GH_PAT` avec votre token

3. Modifiez le workflow appelant :
   ```yaml
   jobs:
     call-ai-review:
       uses: VOTRE_USERNAME/java-ai-code-review/.github/workflows/reusable-ai-review.yml@main
       secrets:
         ANTHROPIC_API_KEY: ${{ secrets.ANTHROPIC_API_KEY }}
         inherit: true  # Hérite de tous les secrets
   ```

#### Option B : Utiliser GitHub Apps (recommandé pour organisations)

Pour les organisations, créez une GitHub App avec les permissions nécessaires.

### Étape 3 : Obtenir une clé API Anthropic

1. Allez sur [console.anthropic.com](https://console.anthropic.com/)
2. Créez un compte ou connectez-vous
3. Générez une clé API
4. Copiez la clé (format: `sk-ant-...`)

---

## ⚙️ Configuration par repository

Pour chaque repository Java où vous voulez activer la revue IA :

### 1. Créer le workflow appelant

Créez le fichier `.github/workflows/ai-code-review.yml` :

```yaml
name: 🤖 AI Code Review

on:
  pull_request:
    types: [opened, synchronize, reopened]
    paths:
      - '**.java'
  push:
    branches:
      - main
      - develop
      - 'feature/**'
    paths:
      - '**.java'

jobs:
  call-ai-review:
    uses: VOTRE_USERNAME/java-ai-code-review/.github/workflows/reusable-ai-review.yml@main
    with:
      java-version: '17'
      build-tool: 'auto'
      enable-static-analysis: true
      post-pr-comment: true
      fail-on-critical: false
    secrets:
      ANTHROPIC_API_KEY: ${{ secrets.ANTHROPIC_API_KEY }}
```

**⚠️ IMPORTANT** : Remplacez `VOTRE_USERNAME` par :
- Votre nom d'utilisateur GitHub si c'est un compte personnel
- Le nom de votre organisation si c'est un compte organisation

### 2. Configurer le secret

Dans le repository :
1. Settings → Secrets and variables → Actions
2. New repository secret
3. Nom : `ANTHROPIC_API_KEY`
4. Valeur : Votre clé API Claude
5. Add secret

### 3. Tester

Créez une branche de test :

```bash
cd /chemin/vers/votre-projet

git checkout -b test-ai-review
echo "// Test comment" >> src/main/java/Main.java
git add .
git commit -m "Test: AI code review"
git push origin test-ai-review
```

Créez une Pull Request et observez le bot en action !

---

## 🔑 Gestion des clés API

### Option 1 : Clé par repository (recommandé pour débuter)

Chaque repository a sa propre clé `ANTHROPIC_API_KEY` dans ses secrets.

**Avantages :**
- ✅ Isolation : problème sur un repo n'affecte pas les autres
- ✅ Traçabilité : usage par repository
- ✅ Sécurité : révocation ciblée

### Option 2 : Clé d'organisation (recommandé pour les organisations)

Une seule clé partagée entre tous les repos de l'organisation.

**Configuration :**
1. Organization → Settings → Secrets and variables → Actions
2. New organization secret : `ANTHROPIC_API_KEY`
3. Choisissez quels repos peuvent l'utiliser

**Dans le workflow appelant :**
```yaml
secrets:
  ANTHROPIC_API_KEY: ${{ secrets.ANTHROPIC_API_KEY }}
```

**Avantages :**
- ✅ Gestion centralisée
- ✅ Déploiement simplifié
- ✅ Économies sur l'API

### Option 3 : Environnements GitHub

Créez des environnements avec protection :

1. Repository → Settings → Environments → New environment
2. Nom : `ai-review`
3. Ajoutez `ANTHROPIC_API_KEY` dans cet environnement

**Dans le workflow appelant :**
```yaml
jobs:
  call-ai-review:
    environment: ai-review  # Utilise l'environnement
    uses: ...
```

---

## 🎨 Personnalisation

### Personnaliser par repository

Vous pouvez adapter le comportement pour chaque repo :

```yaml
jobs:
  call-ai-review:
    uses: VOTRE_USERNAME/java-ai-code-review/.github/workflows/reusable-ai-review.yml@main
    with:
      # Java 11 pour ce projet legacy
      java-version: '11'

      # Désactiver analyses statiques (projet sans Maven)
      enable-static-analysis: false

      # Ne pas commenter les PRs (notifications email suffisent)
      post-pr-comment: false

      # Échouer le build si problèmes critiques
      fail-on-critical: true
    secrets:
      ANTHROPIC_API_KEY: ${{ secrets.ANTHROPIC_API_KEY }}
```

### Modifier le workflow central

Pour changer le comportement global :

1. Modifiez [`.github/workflows/reusable-ai-review.yml`](.github/workflows/reusable-ai-review.yml)
2. Ou modifiez [`scripts/ai_code_reviewer.py`](scripts/ai_code_reviewer.py)
3. Commitez et poussez

**Tous les repositories utiliseront la nouvelle version automatiquement !**

### Utiliser une version spécifique

Pour figer une version du workflow :

```yaml
# Utiliser le commit SHA (recommandé pour production)
uses: VOTRE_USERNAME/java-ai-code-review/.github/workflows/reusable-ai-review.yml@a1b2c3d

# Ou utiliser un tag
uses: VOTRE_USERNAME/java-ai-code-review/.github/workflows/reusable-ai-review.yml@v1.0.0

# Ou utiliser une branche (testing)
uses: VOTRE_USERNAME/java-ai-code-review/.github/workflows/reusable-ai-review.yml@develop
```

---

## 🛠️ Dépannage

### Le workflow ne se déclenche pas

**Cause possible :** Le repository appelant n'a pas les permissions.

**Solution :**
1. Vérifiez que GitHub Actions est activé
2. Vérifiez que le repo central est accessible
3. Pour un repo privé, configurez un PAT (voir Étape 2)

### Erreur "workflow was not found"

**Cause :** Le chemin ou le nom du workflow est incorrect.

**Solution :**
```yaml
# Vérifiez le chemin exact (sensible à la casse)
uses: USERNAME/java-ai-code-review/.github/workflows/reusable-ai-review.yml@main
#                                  ^^^^^^^^^^^^^^^^^^ Doit être exact
```

### Erreur "ANTHROPIC_API_KEY not set"

**Cause :** Le secret n'est pas configuré ou mal nommé.

**Solution :**
1. Settings → Secrets → Vérifiez `ANTHROPIC_API_KEY`
2. Le nom doit être exact (sensible à la casse)

### Le script Python échoue

**Cause :** Le script n'a pas pu être téléchargé depuis le repo central.

**Solution :**
1. Vérifiez que `scripts/ai_code_reviewer.py` existe dans le repo central
2. Vérifiez que le repo central est accessible (public ou PAT configuré)

### Test local

Pour tester le script en local avant de déployer :

```bash
# Cloner le repo central
git clone https://github.com/VOTRE_USERNAME/java-ai-code-review.git
cd java-ai-code-review

# Installer les dépendances
pip install -r scripts/requirements.txt

# Configurer les variables
export ANTHROPIC_API_KEY="sk-ant-..."
export COMMIT_SHA=$(git rev-parse HEAD)

# Créer une liste de fichiers de test
echo "src/main/java/Test.java" > changed_files.txt

# Exécuter
python scripts/ai_code_reviewer.py
```

---

## 📊 Suivi et statistiques

### Voir les exécutions

1. Allez dans l'onglet **Actions** de votre repository
2. Cliquez sur un workflow pour voir les détails
3. Téléchargez les artefacts (rapports JSON/Markdown)

### Analyser les coûts

L'API Claude facture à l'usage :
- **Claude Sonnet 4.5** : ~$3 / million de tokens d'entrée
- **Estimation moyenne** : $0.01-0.05 par revue

Pour suivre l'usage :
1. Console Anthropic → Usage
2. Filtrez par clé API si vous utilisez plusieurs clés

---

## 🔄 Mise à jour

Pour mettre à jour le système sur tous vos repositories :

1. Modifiez le code dans le repo central
2. Commitez et poussez
3. **C'est tout !** Tous les repos utiliseront la nouvelle version

**Exemple :**

```bash
cd java-ai-code-review

# Modifier le script
nano scripts/ai_code_reviewer.py

# Committer
git add scripts/ai_code_reviewer.py
git commit -m "feat: amélioration de l'analyse de sécurité"
git push origin main

# Tous vos repositories utiliseront cette version au prochain déclenchement
```

---

## 📚 Ressources supplémentaires

- [Documentation GitHub Actions - Reusable Workflows](https://docs.github.com/en/actions/using-workflows/reusing-workflows)
- [Documentation Claude API](https://docs.anthropic.com/)
- [Guide de sécurité GitHub Actions](https://docs.github.com/en/actions/security-guides)

---

## 🆘 Besoin d'aide ?

Si vous rencontrez des problèmes :

1. Consultez la section [Dépannage](#dépannage)
2. Vérifiez les logs dans l'onglet **Actions**
3. Testez en local avec le script Python
4. Ouvrez une issue sur le repository central

---

**🎉 Félicitations !** Vous avez configuré un système de revue de code IA centralisé sur plusieurs repositories !
