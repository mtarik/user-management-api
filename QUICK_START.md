# 🚀 Quick Start - 5 minutes

Guide rapide pour démarrer avec l'AI Code Review en 5 minutes.

## Étape 1 : Obtenir une clé API Claude (2 min)

1. Allez sur https://console.anthropic.com/
2. Créez un compte ou connectez-vous
3. Allez dans **API Keys**
4. Cliquez sur **Create Key**
5. Copiez la clé (commence par `sk-ant-...`)

## Étape 2 : Configurer GitHub (2 min)

1. Allez dans votre dépôt GitHub
2. Cliquez sur **Settings** → **Secrets and variables** → **Actions**
3. Cliquez sur **New repository secret**
4. Nom : `ANTHROPIC_API_KEY`
5. Valeur : Collez votre clé API Claude
6. Cliquez sur **Add secret**

## Étape 3 : Copier les fichiers (1 min)

```bash
# Dans votre projet Java
cp -r java-ai-code-review/.github ./
cp -r java-ai-code-review/scripts ./
```

Ou copiez manuellement :
- `.github/workflows/ai-code-review.yml` → votre projet
- `.github/config/checkstyle.xml` → votre projet
- `.github/config/pmd-ruleset.xml` → votre projet
- `scripts/ai_code_reviewer.py` → votre projet

## Étape 4 : Tester

```bash
# Créez une branche de test
git checkout -b test-ai-review

# Modifiez un fichier Java
echo "// Test comment" >> src/main/java/Main.java

# Commitez et poussez
git add .
git commit -m "Test AI review"
git push origin test-ai-review

# Créez une Pull Request
# Le bot va commenter automatiquement !
```

## ✅ C'est tout !

Le système est maintenant actif. À chaque PR ou commit, le bot analysera votre code Java automatiquement.

## 📖 Prochaines étapes

- Lisez le [README.md](README.md) complet pour la configuration avancée
- Personnalisez les règles dans `.github/config/`
- Ajoutez les plugins Maven (voir `pom.xml.example`)

## ❓ Problèmes ?

- Le workflow ne se lance pas ? → Vérifiez l'onglet **Actions** dans GitHub
- Pas de commentaire sur la PR ? → Vérifiez que le secret `ANTHROPIC_API_KEY` existe
- Erreur de compilation ? → Assurez-vous que votre projet compile avec `mvn compile`

## 💡 Astuce

Pour tester localement avant de pousser :

```bash
export ANTHROPIC_API_KEY="sk-ant-..."
git diff --name-only HEAD~1 HEAD | grep '\.java$' > changed_files.txt
python scripts/ai_code_reviewer.py
```

---

**Besoin d'aide ?** Consultez le [README.md](README.md) détaillé.
