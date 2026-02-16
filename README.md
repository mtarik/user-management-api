# User Management API - Projet de Démonstration

> API REST Spring Boot pour la gestion d'utilisateurs - Projet de test pour AI Code Review

## 📋 Description

Ce projet sert de **démonstration** pour le système [AI Code Review](https://github.com/mtarik/java-ai-code-review).

Il contient une API REST simple avec des endpoints pour gérer des utilisateurs, utilisée pour tester et démontrer les capacités de revue de code IA.

## 🏗️ Architecture

```
user-management-api/
├── src/main/java/com/example/demo/
│   ├── controller/
│   │   └── UserController.java
│   ├── service/
│   │   └── UserService.java
│   ├── repository/
│   │   └── UserRepository.java
│   └── model/
│       └── User.java
└── .github/workflows/
    └── ai-code-review.yml          # Workflow automatique
```

## 🤖 AI Code Review

Ce projet utilise le système AI Code Review centralisé. Chaque Pull Request est automatiquement analysée par Claude Sonnet 4.5 qui:

- ✅ Détecte les vulnérabilités de sécurité (OWASP Top 10)
- ✅ Identifie les problèmes de performance
- ✅ Vérifie l'architecture et les best practices
- ✅ Poste des commentaires inline sur les fichiers modifiés
- ✅ Ajoute des labels automatiques (score, sévérités, catégories)

### Exemple de branches de test

- `feature/test-bad-practices` - Mauvaises pratiques intentionnelles (score faible)
- `feature/test-007` - Code propre et refactorisé (score élevé)

## 🚀 Utiliser AI Code Review dans votre projet

Pour intégrer AI Code Review dans votre propre projet Java:

**📖 Consultez la documentation complète:** https://github.com/mtarik/java-ai-code-review

**⚡ Démarrage rapide:**

1. Copiez le fichier [.github/workflows/ai-code-review.yml](.github/workflows/ai-code-review.yml) dans votre projet
2. Ajoutez les secrets GitHub (`ANTHROPIC_API_KEY`, `PAT_GITHUB_TOKEN`)
3. Créez une PR - l'analyse se lance automatiquement !

## 📊 Technologies

- **Java 17** - Language
- **Spring Boot 3.x** - Framework
- **Spring Data JPA** - Persistence
- **H2 Database** - Base de données en mémoire
- **Maven** - Build tool
- **GitHub Actions** - CI/CD
- **Claude Sonnet 4.5** - AI Code Review

## 🔗 Liens Utiles

- [AI Code Review Repository](https://github.com/mtarik/java-ai-code-review)
- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)

---

<div align="center">

**🤖 Code review automatique propulsé par Claude Sonnet 4.5**

[Documentation](https://github.com/mtarik/java-ai-code-review) • [Issues](https://github.com/mtarik/user-management-api/issues)

</div>
