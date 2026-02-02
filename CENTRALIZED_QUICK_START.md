# 🚀 Quick Start Centralisé - 3 minutes

Guide ultra-rapide pour déployer la revue de code IA sur **plusieurs repositories**.

---

## ⚡ Étape 1 : Préparer le repo central (1 fois)

```bash
# Dans ce repository
cd c:\Users\tarik\java-ai-code-review

# Initialiser git si nécessaire
git init
git add .
git commit -m "Configuration centralisée AI Code Review"

# Créer un repo sur GitHub et pousser
# Allez sur github.com → New repository → Créez "java-ai-code-review"
git remote add origin https://github.com/VOTRE_USERNAME/java-ai-code-review.git
git branch -M main
git push -u origin main
```

✅ **Fait !** Votre repo central est prêt.

---

## ⚡ Étape 2 : Obtenir la clé API (1 minute)

1. Allez sur https://console.anthropic.com/
2. Créez un compte → API Keys → Create Key
3. Copiez la clé (commence par `sk-ant-...`)

---

## ⚡ Étape 3 : Activer sur un repository (1 minute par repo)

Pour **chaque projet Java** où vous voulez la revue IA :

### A. Créer le workflow

```bash
cd /chemin/vers/votre-projet-java

# Créer le dossier .github/workflows
mkdir -p .github/workflows

# Copier le workflow
cat > .github/workflows/ai-code-review.yml << 'EOF'
name: 🤖 AI Code Review

on:
  pull_request:
    paths: ['**.java']
  push:
    branches: [main, develop, 'feature/**']
    paths: ['**.java']

jobs:
  ai-review:
    uses: VOTRE_USERNAME/java-ai-code-review/.github/workflows/reusable-ai-review.yml@main
    with:
      java-version: '17'
    secrets:
      ANTHROPIC_API_KEY: ${{ secrets.ANTHROPIC_API_KEY }}
EOF
```

**⚠️ Remplacez `VOTRE_USERNAME`** par votre username GitHub !

### B. Configurer le secret

1. Allez sur votre repo GitHub
2. Settings → Secrets and variables → Actions
3. New repository secret
   - **Name:** `ANTHROPIC_API_KEY`
   - **Value:** Votre clé API
4. Add secret

### C. Commiter et pousser

```bash
git add .github/workflows/ai-code-review.yml
git commit -m "feat: ajouter revue de code IA"
git push
```

✅ **Terminé !** La revue IA est active sur ce repo.

---

## ⚡ Répéter l'Étape 3 pour chaque repository

Pour ajouter un nouveau repo :
1. Copier le fichier `.github/workflows/ai-code-review.yml`
2. Configurer le secret `ANTHROPIC_API_KEY`
3. Pousser

**C'est tout !** 🎉

---

## 🧪 Tester

```bash
# Dans n'importe quel projet activé
git checkout -b test-ai-review
echo "// Test" >> src/main/java/Main.java
git add .
git commit -m "test: AI review"
git push origin test-ai-review
```

Créez une Pull Request → Le bot commente automatiquement !

---

## 📊 Configuration Avancée (optionnel)

Personnalisez pour un repo spécifique :

```yaml
jobs:
  ai-review:
    uses: VOTRE_USERNAME/java-ai-code-review/.github/workflows/reusable-ai-review.yml@main
    with:
      java-version: '11'                  # Java 11 pour ce projet
      build-tool: 'gradle'                # Forcer Gradle
      enable-static-analysis: false       # Désactiver PMD/Checkstyle
      post-pr-comment: true               # Commenter les PRs
      fail-on-critical: true              # Échouer si problèmes critiques
    secrets:
      ANTHROPIC_API_KEY: ${{ secrets.ANTHROPIC_API_KEY }}
```

---

## 🔄 Pour mettre à jour tous vos repos

Modifiez le code dans le **repo central** uniquement :

```bash
cd java-ai-code-review
nano scripts/ai_code_reviewer.py
git commit -am "feat: amélioration"
git push
```

**Tous les repositories sont mis à jour automatiquement !** ✨

---

## 🎯 Organisation : Secret partagé (recommandé)

Pour une organisation GitHub :

1. Organization → Settings → Secrets and variables → Actions
2. New organization secret : `ANTHROPIC_API_KEY`
3. Sélectionnez "All repositories" ou les repos spécifiques

**Avantage :** Une seule clé pour tous les repos ! Plus besoin de configurer le secret individuellement.

---

## ❓ Problèmes ?

### Erreur "workflow not found"
➡️ Vérifiez que vous avez bien remplacé `VOTRE_USERNAME`

### Erreur "ANTHROPIC_API_KEY not set"
➡️ Vérifiez que le secret est bien nommé `ANTHROPIC_API_KEY` (exact)

### Le workflow ne se déclenche pas
➡️ Vérifiez que vous modifiez bien des fichiers `.java`

### Besoin d'aide ?
➡️ Consultez [CENTRALIZED_SETUP.md](CENTRALIZED_SETUP.md) pour la doc complète

---

## 📖 Documentation complète

- **Guide complet** : [CENTRALIZED_SETUP.md](CENTRALIZED_SETUP.md)
- **README général** : [README.md](README.md)
- **Guide simple** : [QUICK_START.md](QUICK_START.md)

---

**✅ C'est fait !** Vous avez maintenant un système centralisé qui analyse automatiquement le code Java sur tous vos repositories !
