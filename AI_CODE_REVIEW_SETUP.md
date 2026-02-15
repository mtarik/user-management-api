# 🤖 Configuration AI Code Review v3.0

Ce projet est configuré pour utiliser l'**AI Code Review Automation v3.0** pour des revues de code automatiques et intelligentes sur chaque Pull Request.

---

## ✅ Configuration Actuelle

Le workflow [.github/workflows/ai-code-review.yml](.github/workflows/ai-code-review.yml) est déjà configuré et prêt à l'emploi !

### 📊 Fonctionnalités Activées (v3.0)

- ✅ **Scoring détaillé par catégorie** : Architecture, Sécurité, Performance, Style (sur /10)
- ✅ **Analyse OWASP Top 10 2021** : Détection structurée des vulnérabilités de sécurité
- ✅ **Analyse architecturale approfondie** : Design patterns, violations de couches, couplage
- ✅ **Optimisations performance** : N+1 queries, caching, complexité algorithmique
- ✅ **Auto-labelling automatique** : 10+ labels ajoutés selon l'analyse
- ✅ **Commentaires sur la PR** : Rapport complet posté automatiquement
- ✅ **Analyses statiques** : Checkstyle, PMD intégrés

### ⚙️ Paramètres Configurés

```yaml
java-version: '17'                  # Version Java utilisée
build-tool: 'auto'                  # Détection auto Maven/Gradle
enable-static-analysis: true        # Checkstyle + PMD activés
post-pr-comment: true               # Commentaires automatiques sur PR
fail-on-critical: false             # ⚠️ Désactivé pour l'instant (voir ci-dessous)
```

---

## 🚀 Prérequis

### 1. Configurer la Clé API Anthropic

Pour que l'analyse IA fonctionne, vous devez configurer votre clé API Anthropic :

1. Aller dans **Settings → Secrets and variables → Actions**
2. Cliquer sur **New repository secret**
3. Créer un secret nommé : `ANTHROPIC_API_KEY`
4. Valeur : Votre clé API Anthropic (commence par `sk-ant-...`)

> 💡 Si vous n'avez pas de clé API, créez-en une sur [console.anthropic.com](https://console.anthropic.com)

### 2. Vérifier les Permissions

Le workflow nécessite les permissions suivantes (déjà configurées) :
- `contents: read` - Lire le code
- `pull-requests: write` - Commenter les PRs
- `issues: write` - Ajouter des labels

---

## 📝 Utilisation

### Déclenchement Automatique

Le workflow se déclenche automatiquement sur :

✅ **Pull Requests** (opened, synchronize, reopened)
✅ **Push sur branches** : `main`, `develop`, `feature/**`, `feat/**`, `hotfix/**`
✅ **Déclenchement manuel** via l'onglet Actions

### Que se passe-t-il ?

1. 🔍 Détection des fichiers `.java` modifiés
2. ☕ Compilation du projet (Maven)
3. 📋 Analyse statique (Checkstyle, PMD)
4. 🤖 Analyse IA complète par Claude Sonnet 4.5
5. 💬 Commentaire posté sur la PR avec :
   - Score global /10 avec quality label
   - Scores détaillés par catégorie
   - Section OWASP Top 10 si vulnérabilités
   - Problèmes détectés avec suggestions
   - Analyse architecturale
6. 🏷️ Auto-labelling automatique :
   - `ai-review:critical` - Problèmes critiques
   - `ai-review:security` - Problèmes de sécurité
   - `ai-review:owasp` - Vulnérabilités OWASP
   - `ai-review:performance` - Optimisations suggérées
   - `ai-review:architecture` - Problèmes architecturaux
   - `ai-review:excellent` / `good` / `acceptable` / `needs-work` (selon score)

---

## 🎯 Exemple de Rapport

```markdown
## 🤖 Revue de Code IA - Résumé

### 📊 Score Global : 7.5/10 - Good

| Catégorie | Niveau | Problèmes | Description |
|-----------|--------|-----------|-------------|
| 🏗️ Architecture | 🟢 Good | 1 | Bonne séparation des couches |
| 🔒 Sécurité | 🟡 Medium | 2 | Quelques améliorations nécessaires |
| ⚡ Performance | 🟢 Good | 0 | Pas d'optimisation nécessaire |
| 🎨 Style | 🔵 Info | 3 | Suggestions mineures de style |

### 🔒 Analyse OWASP Top 10 2021

**🟡 A01:2021 – Broken Access Control**
- UserController.java:45 - Manque de validation des permissions
  💡 Suggestion: Ajouter @PreAuthorize("hasRole('ADMIN')")

### 📄 UserService.java
**Score**: 8/10

🟡 **MEDIUM** - Optimisation possible
Description: Requête N+1 potentielle détectée
💡 **Suggestion**: Utiliser JOIN FETCH dans la requête JPA
```

---

## 🔧 Configuration Avancée

### Activer le Blocage sur Problèmes Critiques

Une fois que vous êtes confiant dans le système, vous pouvez activer le blocage automatique :

```yaml
fail-on-critical: true  # Bloque le merge si problèmes critiques
```

⚠️ **Recommandation** : Testez d'abord avec plusieurs PRs avant d'activer cette option !

### Personnaliser les Branches

Pour changer les branches surveillées :

```yaml
push:
  branches:
    - main
    - develop
    - 'votre-pattern/**'
```

### Désactiver les Analyses Statiques

Si vous ne voulez pas Checkstyle/PMD :

```yaml
enable-static-analysis: false
```

---

## 🧪 Tester la Configuration

### Option 1 : Créer une PR de Test

1. Créer une branche : `git checkout -b test/ai-review`
2. Modifier un fichier Java (ajouter un problème volontaire)
3. Commit et push
4. Créer une Pull Request
5. Attendre l'analyse (1-2 minutes)
6. Vérifier le commentaire et les labels

### Option 2 : Déclenchement Manuel

1. Aller dans **Actions** → **AI Code Review v3.0**
2. Cliquer sur **Run workflow**
3. Sélectionner la branche
4. Lancer et consulter les logs

---

## 📊 Métriques

Le système v3.0 enregistre automatiquement des métriques pour chaque analyse :
- Durée d'exécution
- Score global et par catégorie
- Nombre d'issues par sévérité
- Tendances dans le temps

Les métriques sont disponibles dans les artefacts du workflow.

---

## 🆘 Dépannage

### Le workflow ne se déclenche pas

- Vérifier que la clé API `ANTHROPIC_API_KEY` est configurée
- Vérifier que des fichiers `.java` ont été modifiés
- Vérifier que la branche est dans la liste des branches surveillées

### Aucun commentaire posté

- Vérifier les permissions du workflow (Settings → Actions → General)
- Vérifier que `post-pr-comment: true`
- Consulter les logs du workflow pour les erreurs

### Labels non créés

- Vérifier que le workflow a les permissions `issues: write`
- Les labels seront créés automatiquement à la première utilisation

---

## 📚 Documentation Complète

Pour plus d'informations sur le système AI Code Review :

- **Repository principal** : [mtarik/java-ai-code-review](https://github.com/mtarik/java-ai-code-review)
- **CHANGELOG v3.0** : Voir toutes les fonctionnalités
- **IMPLEMENTATION_COMPLETE** : Guide complet v3.0

---

## ✅ Checklist de Validation

- [ ] Clé API Anthropic configurée
- [ ] Workflow testé sur une PR de test
- [ ] Commentaires automatiques fonctionnels
- [ ] Labels automatiques ajoutés
- [ ] Rapport complet généré
- [ ] fail-on-critical activé (après validation)

---

**Version** : 3.0.0
**Dernière mise à jour** : 2026-02-15
**Propulsé par** : Claude Sonnet 4.5 🤖
