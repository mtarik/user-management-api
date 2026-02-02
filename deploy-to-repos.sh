#!/bin/bash

################################################################################
# Script de déploiement automatique de la revue de code IA
# sur plusieurs repositories
#
# Usage: ./deploy-to-repos.sh
################################################################################

set -e  # Arrêter en cas d'erreur

# Couleurs pour l'affichage
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
GITHUB_USERNAME=""  # À remplir : votre username GitHub
CENTRAL_REPO="java-ai-code-review"  # Nom du repo central
API_KEY=""  # Optionnel : votre clé API Anthropic

# Liste des repositories à configurer
# Format: "proprietaire/repository"
REPOSITORIES=(
    # Ajoutez vos repositories ici
    # "$GITHUB_USERNAME/mon-projet-1"
    # "$GITHUB_USERNAME/mon-projet-2"
    # "$GITHUB_USERNAME/mon-projet-3"
)

################################################################################
# Fonctions utilitaires
################################################################################

print_header() {
    echo -e "\n${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}\n"
}

print_success() {
    echo -e "${GREEN}✓${NC} $1"
}

print_error() {
    echo -e "${RED}✗${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}⚠${NC} $1"
}

print_info() {
    echo -e "${BLUE}ℹ${NC} $1"
}

################################################################################
# Vérifications préalables
################################################################################

check_prerequisites() {
    print_header "Vérification des prérequis"

    # Vérifier git
    if ! command -v git &> /dev/null; then
        print_error "Git n'est pas installé"
        exit 1
    fi
    print_success "Git installé"

    # Vérifier gh CLI (optionnel mais recommandé)
    if command -v gh &> /dev/null; then
        print_success "GitHub CLI (gh) installé"
        GH_CLI_AVAILABLE=true
    else
        print_warning "GitHub CLI (gh) non installé - certaines fonctionnalités seront limitées"
        print_info "Installez gh : https://cli.github.com/"
        GH_CLI_AVAILABLE=false
    fi

    # Vérifier la configuration
    if [ -z "$GITHUB_USERNAME" ]; then
        print_error "GITHUB_USERNAME n'est pas configuré dans le script"
        print_info "Éditez ce script et remplissez la variable GITHUB_USERNAME"
        exit 1
    fi
    print_success "Username GitHub configuré : $GITHUB_USERNAME"

    if [ ${#REPOSITORIES[@]} -eq 0 ]; then
        print_warning "Aucun repository configuré dans le script"
        print_info "Éditez ce script et ajoutez vos repositories dans REPOSITORIES"
        exit 1
    fi
    print_success "${#REPOSITORIES[@]} repository/repositories à configurer"
}

################################################################################
# Génération du workflow
################################################################################

generate_workflow() {
    local repo=$1
    local java_version=${2:-17}

    cat << EOF
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
      - 'feat/**'
    paths:
      - '**.java'
  workflow_dispatch:

jobs:
  ai-review:
    name: Analyse IA du code Java
    uses: $GITHUB_USERNAME/$CENTRAL_REPO/.github/workflows/reusable-ai-review.yml@main
    with:
      java-version: '$java_version'
      build-tool: 'auto'
      enable-static-analysis: true
      post-pr-comment: true
      fail-on-critical: false
    secrets:
      ANTHROPIC_API_KEY: \${{ secrets.ANTHROPIC_API_KEY }}
EOF
}

################################################################################
# Déploiement sur un repository
################################################################################

deploy_to_repository() {
    local repo=$1
    print_header "Déploiement sur $repo"

    # Extraire le nom du repo
    local repo_name=$(basename "$repo")
    local temp_dir="temp_${repo_name}"

    # Nettoyer le dossier temporaire s'il existe
    if [ -d "$temp_dir" ]; then
        rm -rf "$temp_dir"
    fi

    # Cloner le repository
    print_info "Clonage de $repo..."
    if git clone "https://github.com/$repo.git" "$temp_dir" 2>/dev/null; then
        print_success "Repository cloné"
    else
        print_error "Échec du clonage de $repo"
        print_info "Assurez-vous d'avoir accès au repository"
        return 1
    fi

    cd "$temp_dir"

    # Créer une branche
    local branch_name="setup-ai-code-review"
    print_info "Création de la branche $branch_name..."
    git checkout -b "$branch_name" 2>/dev/null || {
        print_warning "Branche $branch_name existe déjà, passage sur cette branche"
        git checkout "$branch_name"
    }

    # Créer le dossier .github/workflows
    mkdir -p .github/workflows

    # Générer et écrire le workflow
    print_info "Génération du workflow..."
    generate_workflow "$repo" > .github/workflows/ai-code-review.yml
    print_success "Workflow généré : .github/workflows/ai-code-review.yml"

    # Commiter les changements
    git add .github/workflows/ai-code-review.yml
    if git diff --staged --quiet; then
        print_warning "Aucun changement à commiter (workflow déjà présent ?)"
        cd ..
        rm -rf "$temp_dir"
        return 0
    fi

    print_info "Commit des changements..."
    git commit -m "feat: ajouter revue de code IA avec Claude

- Configure le workflow de revue automatique
- Analyse le code Java à chaque commit/PR
- Utilise le workflow centralisé de $GITHUB_USERNAME/$CENTRAL_REPO

Co-Authored-By: AI Code Review Setup Script"

    # Pousser la branche
    print_info "Push de la branche..."
    if git push -u origin "$branch_name" 2>/dev/null; then
        print_success "Branche poussée avec succès"
    else
        print_error "Échec du push"
        cd ..
        rm -rf "$temp_dir"
        return 1
    fi

    # Créer une Pull Request (si gh CLI disponible)
    if [ "$GH_CLI_AVAILABLE" = true ]; then
        print_info "Création de la Pull Request..."
        if gh pr create \
            --title "🤖 Ajouter revue de code IA automatique" \
            --body "## 🤖 Configuration de la revue de code IA

Ce PR ajoute la revue automatique de code Java avec Claude AI.

### ✨ Fonctionnalités
- ✅ Analyse automatique à chaque commit et PR
- ✅ Détection de bugs, problèmes de sécurité, et optimisations
- ✅ Commentaires intelligents sur les Pull Requests
- ✅ Rapports détaillés téléchargeables

### 📋 Configuration requise

**⚠️ Action requise** : Avant de merger ce PR, vous devez configurer la clé API Anthropic :

1. Allez dans **Settings → Secrets and variables → Actions**
2. Créez un nouveau secret :
   - **Name** : \`ANTHROPIC_API_KEY\`
   - **Value** : Votre clé API Anthropic (obtenue sur https://console.anthropic.com/)
3. Mergez ce PR

### 🧪 Test

Une fois la clé API configurée :
1. Créez une branche de test
2. Modifiez un fichier Java
3. Créez une PR
4. Le bot analysera et commentera automatiquement !

### 📚 Documentation

- [Guide de démarrage rapide](https://github.com/$GITHUB_USERNAME/$CENTRAL_REPO/blob/main/CENTRALIZED_QUICK_START.md)
- [Documentation complète](https://github.com/$GITHUB_USERNAME/$CENTRAL_REPO/blob/main/CENTRALIZED_SETUP.md)

---

🤖 Généré automatiquement par le script de déploiement" 2>/dev/null; then
            print_success "Pull Request créée !"
            print_info "Consultez GitHub pour voir la PR"
        else
            print_warning "Impossible de créer la PR automatiquement"
            print_info "Créez manuellement la PR depuis : https://github.com/$repo/pull/new/$branch_name"
        fi
    else
        print_info "Pull Request à créer manuellement :"
        print_info "  https://github.com/$repo/pull/new/$branch_name"
    fi

    # Configuration du secret (avec gh CLI)
    if [ "$GH_CLI_AVAILABLE" = true ] && [ -n "$API_KEY" ]; then
        print_info "Configuration du secret ANTHROPIC_API_KEY..."
        if gh secret set ANTHROPIC_API_KEY --body "$API_KEY" --repo "$repo" 2>/dev/null; then
            print_success "Secret configuré avec succès"
        else
            print_warning "Impossible de configurer le secret automatiquement"
            print_info "Configurez-le manuellement dans les settings du repository"
        fi
    else
        print_warning "Secret à configurer manuellement :"
        print_info "  1. Allez sur https://github.com/$repo/settings/secrets/actions"
        print_info "  2. Créez ANTHROPIC_API_KEY avec votre clé API"
    fi

    # Retour au dossier parent et nettoyage
    cd ..
    rm -rf "$temp_dir"

    print_success "Déploiement terminé pour $repo !"
}

################################################################################
# Fonction principale
################################################################################

main() {
    clear
    print_header "🤖 Déploiement de la revue de code IA - Multi-repositories"

    echo "Ce script va déployer la revue de code IA sur les repositories suivants :"
    for repo in "${REPOSITORIES[@]}"; do
        echo "  - $repo"
    done
    echo ""

    # Demander confirmation
    read -p "Voulez-vous continuer ? (y/N) " -n 1 -r
    echo ""
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        print_info "Opération annulée"
        exit 0
    fi

    # Vérifications
    check_prerequisites

    # Déployer sur chaque repository
    local success_count=0
    local fail_count=0

    for repo in "${REPOSITORIES[@]}"; do
        if deploy_to_repository "$repo"; then
            ((success_count++))
        else
            ((fail_count++))
        fi
        echo ""
    done

    # Résumé final
    print_header "📊 Résumé du déploiement"
    print_success "$success_count repository(ies) configuré(s)"
    if [ $fail_count -gt 0 ]; then
        print_error "$fail_count repository(ies) en échec"
    fi

    echo ""
    print_info "Prochaines étapes :"
    echo "  1. Reviewez et mergez les Pull Requests créées"
    echo "  2. Vérifiez que les secrets ANTHROPIC_API_KEY sont configurés"
    echo "  3. Testez en créant une PR de test sur chaque repo"
    echo ""
    print_success "Déploiement terminé !"
}

################################################################################
# Point d'entrée
################################################################################

# Vérifier que le script est exécuté, pas sourcé
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    main "$@"
else
    print_error "Ce script doit être exécuté, pas sourcé"
    return 1
fi
