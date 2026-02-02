# 🚀 Démarrage Rapide - 3 Minutes

## Configuration Express pour la Revue de Code IA

### 1️⃣ Obtenir une Clé API Claude (1 min)

1. Allez sur https://console.anthropic.com/
2. Créez un compte (gratuit)
3. Dans **API Keys**, cliquez sur **Create Key**
4. Copiez la clé (commence par `sk-ant-...`)

### 2️⃣ Configurer GitHub (1 min)

```bash
# Dans votre repository GitHub :
Settings → Secrets and variables → Actions → New repository secret
```

- **Nom** : `ANTHROPIC_API_KEY`
- **Valeur** : Votre clé API Claude
- Cliquez sur **Add secret**

### 3️⃣ Copier les Fichiers (1 min)

```bash
# Copiez ces dossiers dans votre projet Java :
cp -r .github/ /votre/projet/
cp -r scripts/ /votre/projet/

# Ou manuellement :
# - .github/workflows/ai-code-review.yml
# - .github/config/checkstyle.xml
# - .github/config/pmd-ruleset.xml
# - scripts/ai_code_reviewer.py
# - scripts/requirements.txt
```

### 4️⃣ Tester

```bash
# Créer une branche feature
git checkout -b feature/test-ai

# Modifier un fichier Java
echo "// Test" >> src/main/java/Main.java

# Commiter et pousser
git add .
git commit -m "test: AI review"
git push origin feature/test-ai

# Créer une PR sur GitHub
# ➡️ Le bot va automatiquement analyser et commenter ! 🤖
```

---

## ✅ C'est Tout !

Le système est maintenant actif. À chaque commit sur une branche `feature/*` ou PR, le bot analysera automatiquement votre code Java.

---

## 📚 Documentation Complète

- **Installation détaillée** : [INSTALLATION_FR.md](INSTALLATION_FR.md)
- **Guide rapide** : [QUICK_START.md](QUICK_START.md)
- **Documentation complète** : [README.md](README.md)

---

## 🎯 Ce qui Se Passe Automatiquement

À chaque commit sur `feature/*`, `feat/*`, `hotfix/*` :

1. 🔍 **Détection** : Identification des fichiers Java modifiés
2. ☕ **Compilation** : Vérification que le code compile
3. 📋 **Analyse Statique** : Checkstyle, PMD (si configuré)
4. 🤖 **Analyse IA** : Claude examine le code en profondeur
5. 💬 **Commentaire** : Rapport posté automatiquement sur la PR

---

## 📊 Résultats d'Analyse

Le bot analysera :

- ✅ **Qualité du code** : Conventions Java, lisibilité, architecture
- 🐛 **Bugs potentiels** : Null pointers, gestion d'exceptions
- 🔒 **Sécurité** : Injections SQL, vulnérabilités OWASP
- ⚡ **Performance** : Optimisations possibles
- 📚 **Bonnes pratiques** : Patterns Java, immutabilité, streams

### Exemple de Rapport

```markdown
🤖 Revue de Code IA - Résultats

📊 Score Global: 85/100

🔍 Résumé: Code de bonne qualité avec quelques améliorations possibles

📄 Fichier: src/main/java/UserService.java
Score: 8/10

🔴 CRITIQUE
[Security] - Injection SQL Potentielle
La requête SQL est construite par concaténation...
💡 Suggestion: Utiliser PreparedStatement

🟡 MOYEN
[Performance] - Utilisation inefficace de la boucle
💡 Suggestion: Utiliser Stream.filter() pour meilleures performances

✅ Points Forts
- Bonne gestion des exceptions
- Code bien structuré et lisible
```

---

## ⚙️ Branches Surveillées par Défaut

```yaml
- main
- develop
- feature/**    ← Toutes les branches feature
- feat/**
- hotfix/**
```

Pour modifier : Éditez [.github/workflows/ai-code-review.yml](.github/workflows/ai-code-review.yml)

---

## 💡 Astuces

### Déclencher manuellement

Dans GitHub : **Actions** → **AI Code Review** → **Run workflow**

### Tester localement

```bash
export ANTHROPIC_API_KEY="sk-ant-..."
git diff --name-only HEAD~1 HEAD | grep '\.java$' > changed_files.txt
python scripts/ai_code_reviewer.py
```

### Voir les rapports complets

Allez dans **Actions** → Sélectionnez un workflow → **Artifacts** → Téléchargez `ai-review-report-*`

---

## ❓ Problèmes ?

### Le workflow ne se lance pas ?
→ Vérifiez l'onglet **Actions** dans GitHub (il doit être activé)

### Pas de commentaire sur la PR ?
→ Vérifiez que `ANTHROPIC_API_KEY` est configuré dans les secrets

### Erreur API Claude ?
→ Vérifiez que votre clé API est valide sur https://console.anthropic.com/

---

## 🎓 Pour Aller Plus Loin

1. **Personnaliser les règles** : Éditez `.github/config/checkstyle.xml` et `pmd-ruleset.xml`
2. **Changer le modèle IA** : Modifiez `scripts/ai_code_reviewer.py` ligne 30
3. **Ajouter d'autres langages** : Adaptez le workflow pour Python, JavaScript, etc.

---

## 📞 Besoin d'Aide ?

Consultez la documentation complète :
- [INSTALLATION_FR.md](INSTALLATION_FR.md) - Guide d'installation détaillé
- [README.md](README.md) - Documentation technique complète

---

**🎉 Bon développement avec votre assistant IA !**

*Propulsé par Claude Sonnet 4.5 🤖*
