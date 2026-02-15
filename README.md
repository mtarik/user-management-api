# 🤖 AI Code Review - Java avec Claude

Système automatisé de revue de code Java utilisant l'IA Claude d'Anthropic. Ce système s'exécute automatiquement à chaque commit ou pull request pour analyser la qualité du code Java.

## 🌟 Deux modes d'utilisation :

Ce système peut être déployé de **deux façons** :

### 🌐 Mode Centralisé (Recommandé pour plusieurs repositories)

Utilisez **un seul repository central** pour gérer la revue de code sur **tous vos projets Java**.

- ✅ Déploiement en 2 minutes par projet
- ✅ Maintenance centralisée : modifiez une fois, tous les repos en bénéficient
- ✅ Idéal pour les organisations avec plusieurs projets

**➡️ [Guide de démarrage centralisé (3 min)](CENTRALIZED_QUICK_START.md)**

**➡️ [Documentation complète centralisée](CENTRALIZED_SETUP.md)**

### 📦 Mode Standalone (Un seul repository)

Copiez les fichiers directement dans votre projet.

- ✅ Autonome et indépendant
- ✅ Personnalisation maximale par projet
- ✅ Idéal pour un projet unique

**➡️ [Guide de démarrage standalone (5 min)](QUICK_START.md)**

---

## 🎯 Fonctionnalités

- ✅ **Revue automatique** : Se déclenche sur chaque commit/PR
- 🔍 **Analyse complète** : Qualité, bugs, sécurité, performance
- 🤖 **IA Claude** : Analyse intelligente avec Claude Sonnet 4.5
- 🛠️ **Outils Java** : Intégration Checkstyle, PMD, SpotBugs
- 📊 **Rapports détaillés** : JSON et Markdown
- 💬 **Commentaires PR** : Feedback directement sur GitHub

## 📋 Prérequis

- Compte GitHub avec accès au dépôt
- Clé API Anthropic (Claude)
- Projet Java (Maven ou Gradle)
- Java 17+ installé

## 🚀 Installation

### 1. Copier les fichiers dans votre projet

```bash
# Copier la structure des workflows
cp -r .github/ /chemin/vers/votre-projet/

# Copier le script Python
cp -r scripts/ /chemin/vers/votre-projet/

# Si vous utilisez Maven, ajouter les plugins au pom.xml
# Voir pom.xml.example pour la configuration
```

### 2. Configurer les secrets GitHub

Allez dans votre dépôt GitHub : **Settings → Secrets and variables → Actions**

Ajoutez les secrets suivants :

- `ANTHROPIC_API_KEY` : Votre clé API Claude
  - Obtenez-la sur : https://console.anthropic.com/
  - Format : `sk-ant-...`

Le `GITHUB_TOKEN` est automatiquement fourni par GitHub Actions.

### 3. Activer GitHub Actions

1. Allez dans l'onglet **Actions** de votre dépôt
2. Si demandé, activez les workflows
3. Le workflow se déclenchera automatiquement lors du prochain commit

## 📁 Structure du projet

```
votre-projet/
├── .github/
│   ├── workflows/
│   │   └── ai-code-review.yml      # Workflow GitHub Actions
│   └── config/
│       ├── checkstyle.xml          # Configuration Checkstyle
│       └── pmd-ruleset.xml         # Configuration PMD
├── scripts/
│   └── ai_code_reviewer.py         # Script Python d'analyse IA
└── pom.xml                         # Configuration Maven (avec plugins)
```

## ⚙️ Configuration

### Personnaliser le déclenchement

Éditez [.github/workflows/ai-code-review.yml](.github/workflows/ai-code-review.yml):

```yaml
on:
  pull_request:
    types: [opened, synchronize, reopened]
    paths:
      - '**.java'
  push:
    branches:
      - main
      - develop
      - 'feature/**'    # Modifier selon vos branches
    paths:
      - '**.java'
```

### Ajuster les règles de qualité

- **Checkstyle** : Modifiez [.github/config/checkstyle.xml](.github/config/checkstyle.xml)
- **PMD** : Modifiez [.github/config/pmd-ruleset.xml](.github/config/pmd-ruleset.xml)

### Personnaliser l'analyse IA

Éditez [scripts/ai_code_reviewer.py](scripts/ai_code_reviewer.py):

```python
# Ligne 62 : Changer le modèle
self.model = "claude-sonnet-4-5-20250929"  # ou claude-opus-4-5

# Ligne 99+ : Modifier le prompt d'analyse
prompt = f"""Tu es un expert en revue de code Java...
```

## 🔧 Configuration Maven

Ajoutez à votre `pom.xml` (voir [pom.xml.example](pom.xml.example)):

```xml
<build>
    <plugins>
        <!-- Checkstyle -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-checkstyle-plugin</artifactId>
            <version>3.3.1</version>
            <configuration>
                <configLocation>.github/config/checkstyle.xml</configLocation>
            </configuration>
        </plugin>

        <!-- PMD -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-pmd-plugin</artifactId>
            <version>3.21.2</version>
            <configuration>
                <rulesets>
                    <ruleset>.github/config/pmd-ruleset.xml</ruleset>
                </rulesets>
            </configuration>
        </plugin>

        <!-- SpotBugs -->
        <plugin>
            <groupId>com.github.spotbugs</groupId>
            <artifactId>spotbugs-maven-plugin</artifactId>
            <version>4.8.3.0</version>
        </plugin>
    </plugins>
</build>
```

## 📊 Utilisation

### Revue automatique

Le système s'exécute automatiquement quand :

1. Vous créez ou mettez à jour une **Pull Request**
2. Vous faites un **commit** sur les branches configurées
3. Des fichiers **`.java`** sont modifiés

### Rapport généré

Le bot génère :

1. **Commentaire sur la PR** avec l'analyse complète
2. **Fichiers de rapport** téléchargeables :
   - `code_review_XXXXXXXX.json` : Format JSON
   - `code_review_XXXXXXXX.md` : Format Markdown

### Exemple de rapport

```markdown
# 🤖 AI Code Review Report - Java

## 📊 Summary
Overall Score: 75/100

## 📄 File: `src/main/java/UserService.java`

### 🔍 Issues Found

#### 🔴 CRITICAL
**[SECURITY] (Line 42) - SQL Injection vulnerability**
La requête SQL utilise une concaténation de chaîne...
💡 Suggestion: Utiliser PreparedStatement

#### 🟡 MEDIUM
**[PERFORMANCE] (Line 89) - Boucle inefficace**
...
```

## 🎨 Catégories d'analyse

Le système analyse :

| Catégorie | Description |
|-----------|-------------|
| 🐛 **Bugs** | NullPointer, fuites mémoire, exceptions |
| 🔒 **Sécurité** | SQL injection, XSS, OWASP Top 10 |
| ⚡ **Performance** | Optimisations, collections inefficaces |
| 📐 **Style** | Conventions Java, lisibilité |
| ✨ **Bonnes pratiques** | Design patterns, tests |

## 🔍 Niveaux de sévérité

- 🔴 **Critical** : À corriger immédiatement
- 🟠 **High** : Correction urgente recommandée
- 🟡 **Medium** : À corriger prochainement
- 🔵 **Low** : Amélioration suggérée
- ℹ️ **Info** : Information

## 🧪 Test en local

Testez avant de pousser :

```bash
# 1. Installer les dépendances Python
pip install anthropic requests

# 2. Définir les variables d'environnement
export ANTHROPIC_API_KEY="sk-ant-..."
export GITHUB_TOKEN="ghp_..."  # Optionnel pour test local
export COMMIT_SHA=$(git rev-parse HEAD)

# 3. Créer une liste de fichiers modifiés
git diff --name-only HEAD~1 HEAD | grep '\.java$' > changed_files.txt

# 4. Exécuter le script
python scripts/ai_code_reviewer.py
```

## 🔧 Dépannage

### Le workflow ne se déclenche pas

- Vérifiez que les GitHub Actions sont activées
- Vérifiez que vous modifiez bien des fichiers `.java`
- Vérifiez les branches dans la configuration `on.push.branches`

### Erreur "ANTHROPIC_API_KEY not set"

- Vérifiez que le secret est bien configuré dans GitHub
- Le nom doit être exactement `ANTHROPIC_API_KEY`

### Erreur de compilation Maven

- Assurez-vous que `pom.xml` est valide
- Vérifiez la version de Java dans le workflow (ligne 23)

### Pas de commentaire sur la PR

- Vérifiez les permissions dans le workflow (ligne 15-17)
- Le `GITHUB_TOKEN` doit avoir les droits `pull-requests: write`

## 💰 Coûts

L'utilisation de Claude API est payante :

- **Claude Sonnet 4.5** : ~$3 / million tokens d'entrée
- **Estimation** : ~$0.01-0.05 par revue de code
- Budget mensuel recommandé : $10-50 selon la taille de l'équipe

Voir les prix : https://www.anthropic.com/pricing

## 📚 Ressources

- [Documentation Claude API](https://docs.anthropic.com/)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Checkstyle](https://checkstyle.org/)
- [PMD](https://pmd.github.io/)
- [SpotBugs](https://spotbugs.github.io/)

## 🤝 Contribution

Pour améliorer ce système :

1. Modifiez le prompt dans `ai_code_reviewer.py`
2. Ajoutez de nouvelles règles dans les configurations
3. Personnalisez les catégories d'analyse
4. Ajoutez plus d'outils (SonarQube, etc.)

## 📝 Licence

MIT License - Libre d'utilisation et modification

## 🆘 Support

En cas de problème :

1. Vérifiez les logs dans l'onglet **Actions** de GitHub
2. Consultez la section **Dépannage** ci-dessus
3. Ouvrez une issue sur le dépôt

---

**Créé avec ❤️ et Claude AI**
# Trigger workflow
