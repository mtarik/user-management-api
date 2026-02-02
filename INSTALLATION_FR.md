# 📘 Guide d'Installation - Revue de Code IA pour Java

Guide complet pour configurer la revue de code automatique avec IA sur vos repositories GitHub.

## 🎯 Objectif

Mettre en place un système qui, à chaque commit sur une branche feature (ou PR), lance automatiquement une analyse de la qualité du code Java avec Claude AI et poste les résultats directement sur GitHub.

---

## 📋 Prérequis

### 1. Compte et Clés API

- ✅ Compte GitHub avec accès administrateur au repository
- ✅ Compte Anthropic avec clé API Claude
  - Créer un compte sur : https://console.anthropic.com/
  - Obtenir une clé API (commence par `sk-ant-...`)

### 2. Projet Java

- ✅ Projet Java avec Maven ou Gradle
- ✅ Java 17+ installé (recommandé)
- ✅ Code source dans des fichiers `.java`

---

## 🚀 Installation en 5 Étapes

### Étape 1 : Copier les Fichiers dans Votre Projet

Copiez la structure complète dans votre repository :

```bash
# Depuis le répertoire java-ai-code-review
cd /chemin/vers/votre/projet

# Copier le workflow GitHub Actions
mkdir -p .github/workflows
mkdir -p .github/config
cp /chemin/vers/java-ai-code-review/.github/workflows/ai-code-review.yml .github/workflows/
cp /chemin/vers/java-ai-code-review/.github/config/checkstyle.xml .github/config/
cp /chemin/vers/java-ai-code-review/.github/config/pmd-ruleset.xml .github/config/

# Copier les scripts Python
mkdir -p scripts
cp /chemin/vers/java-ai-code-review/scripts/ai_code_reviewer.py scripts/
cp /chemin/vers/java-ai-code-review/scripts/requirements.txt scripts/
```

**Résultat attendu** : Votre projet doit avoir cette structure :

```
votre-projet/
├── .github/
│   ├── workflows/
│   │   └── ai-code-review.yml
│   └── config/
│       ├── checkstyle.xml
│       └── pmd-ruleset.xml
├── scripts/
│   ├── ai_code_reviewer.py
│   └── requirements.txt
├── src/
│   └── main/java/...
└── pom.xml (ou build.gradle)
```

### Étape 2 : Configurer les Secrets GitHub

1. **Aller dans les paramètres du repository** :
   - Allez sur votre repository GitHub
   - Cliquez sur **Settings** (⚙️)
   - Dans le menu gauche : **Secrets and variables** → **Actions**

2. **Ajouter la clé API Claude** :
   - Cliquez sur **New repository secret**
   - **Name** : `ANTHROPIC_API_KEY`
   - **Value** : Collez votre clé API Claude (commence par `sk-ant-...`)
   - Cliquez sur **Add secret**

> ⚠️ **Important** : Le `GITHUB_TOKEN` est fourni automatiquement par GitHub Actions, pas besoin de le configurer.

### Étape 3 : Activer GitHub Actions

1. Allez dans l'onglet **Actions** de votre repository
2. Si GitHub Actions n'est pas activé, cliquez sur **I understand my workflows, go ahead and enable them**
3. Vérifiez que le workflow "🤖 AI Code Review - Java" apparaît dans la liste

### Étape 4 : (Optionnel) Configurer Maven/Gradle

Pour bénéficier de l'analyse statique (Checkstyle, PMD), ajoutez les plugins à votre configuration :

#### Pour Maven (`pom.xml`)

Voir le fichier [pom.xml.example](pom.xml.example) pour un exemple complet.

Ajoutez dans la section `<build><plugins>` :

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-checkstyle-plugin</artifactId>
    <version>3.3.0</version>
    <configuration>
        <configLocation>.github/config/checkstyle.xml</configLocation>
    </configuration>
</plugin>

<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-pmd-plugin</artifactId>
    <version>3.21.0</version>
    <configuration>
        <rulesets>
            <ruleset>.github/config/pmd-ruleset.xml</ruleset>
        </rulesets>
    </configuration>
</plugin>
```

#### Pour Gradle (`build.gradle`)

Voir le fichier [build.gradle.example](build.gradle.example) pour un exemple complet.

### Étape 5 : Tester l'Installation

1. **Créer une branche de test** :

```bash
git checkout -b feature/test-ai-review
```

2. **Modifier un fichier Java** (ou en créer un) :

```bash
# Exemple : ajouter un commentaire dans un fichier existant
echo "// Test AI review" >> src/main/java/com/example/Main.java
```

3. **Commiter et pousser** :

```bash
git add .
git commit -m "test: Tester la revue de code IA"
git push origin feature/test-ai-review
```

4. **Vérifier dans GitHub** :
   - Allez dans l'onglet **Actions**
   - Vous devriez voir le workflow "🤖 AI Code Review - Java" en cours d'exécution
   - Attendez quelques minutes que l'analyse se termine

5. **Créer une Pull Request** :
   - Créez une PR depuis votre branche `feature/test-ai-review` vers `main`
   - Le bot devrait poster un commentaire avec les résultats de l'analyse ! 🎉

---

## ⚙️ Configuration Avancée

### Personnaliser les Branches Surveillées

Éditez [.github/workflows/ai-code-review.yml](.github/workflows/ai-code-review.yml) :

```yaml
on:
  push:
    branches:
      - main
      - develop
      - 'feature/**'    # Toutes les branches feature/*
      - 'feat/**'       # Variante
      - 'hotfix/**'     # Branches hotfix
      - 'bugfix/**'     # Branches bugfix
```

### Modifier les Règles de Qualité

- **Checkstyle** : Éditez [.github/config/checkstyle.xml](.github/config/checkstyle.xml)
- **PMD** : Éditez [.github/config/pmd-ruleset.xml](.github/config/pmd-ruleset.xml)

### Changer le Modèle IA

Éditez [scripts/ai_code_reviewer.py](scripts/ai_code_reviewer.py) ligne 30 :

```python
self.model = "claude-sonnet-4-5-20250929"  # Modèle actuel
# ou
self.model = "claude-opus-4-20250514"      # Modèle plus puissant
```

### Personnaliser le Prompt d'Analyse

Éditez le prompt dans [scripts/ai_code_reviewer.py](scripts/ai_code_reviewer.py) à partir de la ligne 75 pour adapter les critères d'analyse.

---

## 🔍 Vérification et Débogage

### Comment vérifier que tout fonctionne ?

1. **GitHub Actions** :
   - Onglet **Actions** : Le workflow doit s'exécuter
   - Aucune erreur rouge

2. **Commentaires PR** :
   - Sur une PR, le bot doit poster un commentaire avec l'analyse
   - Le rapport doit contenir un score et des recommandations

3. **Artefacts** :
   - Dans les détails du workflow (Actions), vérifiez qu'il y a un artefact `ai-review-report-*`
   - Téléchargez-le pour voir les rapports JSON et Markdown complets

### Problèmes Courants

#### ❌ Le workflow ne se lance pas

**Causes possibles** :
- GitHub Actions n'est pas activé → Allez dans **Settings** → **Actions** → Activer
- Les fichiers ne sont pas dans `.github/workflows/` → Vérifiez le chemin
- Pas de fichiers `.java` modifiés → Le workflow se déclenche uniquement pour les fichiers Java

**Solution** :
```bash
# Vérifier que le fichier workflow existe
ls -la .github/workflows/ai-code-review.yml

# Vérifier qu'il y a des fichiers Java modifiés
git diff --name-only HEAD~1 HEAD | grep '.java'
```

#### ❌ Erreur "ANTHROPIC_API_KEY is not set"

**Cause** : Le secret n'est pas configuré correctement

**Solution** :
1. Allez dans **Settings** → **Secrets and variables** → **Actions**
2. Vérifiez que `ANTHROPIC_API_KEY` existe
3. Si nécessaire, supprimez-le et recréez-le
4. Relancez le workflow

#### ❌ Le bot ne poste pas de commentaire sur la PR

**Causes possibles** :
- Le workflow s'exécute sur un `push` mais pas sur une PR → Créez une PR
- Permissions insuffisantes → Vérifiez dans le workflow que `pull-requests: write` est présent

**Solution** :
```bash
# Relancer le workflow manuellement
# Allez dans Actions → AI Code Review → Run workflow
```

#### ❌ Erreur de compilation Java

**Cause** : Le projet ne compile pas

**Solution** :
- Le workflow continue même si la compilation échoue (`continue-on-error: true`)
- Corrigez les erreurs de compilation dans votre code
- L'analyse IA fonctionne quand même, mais sans les outils statiques (Checkstyle, PMD)

#### ❌ Trop de temps d'exécution / Coûts API élevés

**Solution** :
- Limitez le déclenchement aux PR uniquement (retirez `push:`)
- Analysez moins de fichiers à la fois
- Utilisez un modèle Claude moins cher (ex: `claude-sonnet-3-5-20241022`)

---

## 🧪 Tester Localement

Pour tester le script Python localement avant de pousser :

```bash
# Définir la clé API
export ANTHROPIC_API_KEY="sk-ant-..."

# Générer la liste des fichiers modifiés
git diff --name-only HEAD~1 HEAD | grep '\.java$' > changed_files.txt

# Exécuter le script
python scripts/ai_code_reviewer.py

# Vérifier les rapports générés
ls -l code_review_*.md code_review_*.json
```

---

## 📊 Comprendre les Résultats

### Score Global (0-100)

- **90-100** : Excellent, code de haute qualité
- **70-89** : Bon, quelques améliorations possibles
- **50-69** : Moyen, des problèmes à corriger
- **0-49** : Faible, nombreux problèmes à résoudre

### Niveaux de Sévérité

- 🔴 **CRITIQUE** : Bugs majeurs, failles de sécurité - **À corriger immédiatement**
- 🟠 **HAUT** : Problèmes importants - **À corriger avant merge**
- 🟡 **MOYEN** : Problèmes notables - **À planifier**
- 🔵 **BAS** : Améliorations mineures - **Nice to have**
- ℹ️ **INFO** : Suggestions, bonnes pratiques - **Pour information**

### Catégories d'Issues

- **Bug** : Erreurs de logique, null pointers, etc.
- **Security** : Vulnérabilités de sécurité (injection SQL, XSS, etc.)
- **Performance** : Optimisations possibles
- **Style** : Conventions de code, formatage
- **Best Practice** : Bonnes pratiques Java

---

## 💡 Bonnes Pratiques

### 1. Workflow d'Utilisation

```bash
# 1. Créer une branche feature
git checkout -b feature/nouvelle-fonctionnalite

# 2. Développer et commiter régulièrement
git add .
git commit -m "feat: Ajouter nouvelle fonctionnalité"

# 3. Pousser (déclenche l'analyse)
git push origin feature/nouvelle-fonctionnalite

# 4. Créer une PR (le bot commente automatiquement)
# Aller sur GitHub et créer la PR

# 5. Corriger les problèmes critiques et hauts
# Faire les corrections dans le code

# 6. Pousser les corrections (l'analyse se relance)
git add .
git commit -m "fix: Corriger les problèmes de sécurité"
git push

# 7. Merger quand le code est de bonne qualité
```

### 2. Optimiser les Coûts API

- ✅ Déclencher uniquement sur les PR (retirer `push:`)
- ✅ Limiter aux branches importantes (`feature/**`, `hotfix/**`)
- ✅ Analyser uniquement les fichiers modifiés (déjà configuré)
- ✅ Utiliser un modèle Claude adapté au budget

### 3. Intégration dans le Processus de Développement

- **Ne pas bloquer les merges** : Le workflow ne fait pas échouer la CI (par défaut)
- **Traiter les critiques** : Corriger les issues 🔴 CRITIQUE et 🟠 HAUT
- **Éduquer l'équipe** : Utiliser les rapports comme outils d'apprentissage
- **Itérer** : Ajuster les règles Checkstyle/PMD selon vos besoins

---

## 🆘 Support et Aide

### Documentation

- [README.md](README.md) - Documentation complète
- [QUICK_START.md](QUICK_START.md) - Guide rapide 5 minutes
- [STRUCTURE.md](STRUCTURE.md) - Architecture du projet

### Ressources Externes

- [Documentation Claude API](https://docs.anthropic.com/)
- [GitHub Actions Docs](https://docs.github.com/en/actions)
- [Maven Checkstyle Plugin](https://maven.apache.org/plugins/maven-checkstyle-plugin/)
- [Maven PMD Plugin](https://maven.apache.org/plugins/maven-pmd-plugin/)

### Problèmes Courants

Si vous rencontrez des problèmes :

1. Vérifiez les logs dans l'onglet **Actions** de GitHub
2. Testez localement avec le script Python
3. Vérifiez que tous les secrets sont configurés
4. Assurez-vous que le workflow a les bonnes permissions

---

## ✅ Checklist Finale

Avant de considérer l'installation terminée :

- [ ] Le workflow `.github/workflows/ai-code-review.yml` existe
- [ ] Les fichiers de configuration `.github/config/` sont présents
- [ ] Le script `scripts/ai_code_reviewer.py` est en place
- [ ] Le secret `ANTHROPIC_API_KEY` est configuré dans GitHub
- [ ] GitHub Actions est activé sur le repository
- [ ] Un test avec une branche feature fonctionne
- [ ] Le bot poste des commentaires sur les PR
- [ ] Les artefacts sont générés et téléchargeables
- [ ] L'équipe est formée sur l'utilisation du système

---

**🎉 Félicitations ! Votre système de revue de code IA est opérationnel !**

À chaque commit sur une branche feature ou PR, Claude analysera automatiquement votre code Java et fournira des recommandations intelligentes pour améliorer la qualité.

---

*Dernière mise à jour : Février 2026*
