# 🚀 Projet Spring Boot de Démonstration

Ce projet Spring Boot sert d'exemple pour tester le système de revue de code IA.

## 📋 Description

Application REST simple de gestion d'utilisateurs avec :
- ✅ Spring Boot 3.2.2
- ✅ Spring Data JPA
- ✅ Base de données H2 en mémoire
- ✅ API REST complète (CRUD)
- ✅ Validation des données
- ✅ Tests unitaires avec JUnit et Mockito

## 🏗️ Architecture

```
src/
├── main/
│   ├── java/com/example/demo/
│   │   ├── DemoApplication.java          # Point d'entrée
│   │   ├── controller/
│   │   │   └── UserController.java       # API REST
│   │   ├── service/
│   │   │   └── UserService.java          # Logique métier
│   │   ├── repository/
│   │   │   └── UserRepository.java       # Accès données
│   │   └── model/
│   │       └── User.java                 # Entité JPA
│   └── resources/
│       └── application.properties         # Configuration
└── test/
    └── java/com/example/demo/
        ├── DemoApplicationTests.java
        └── service/
            └── UserServiceTest.java       # Tests unitaires
```

## 🚀 Démarrer l'Application

### Prérequis

- Java 17+
- Maven 3.6+

### Compilation et Exécution

```bash
# Compiler
mvn clean compile

# Exécuter les tests
mvn test

# Lancer l'application
mvn spring-boot:run
```

L'application démarre sur http://localhost:8080

## 📡 API Endpoints

### Récupérer tous les utilisateurs
```bash
GET http://localhost:8080/api/users
```

### Récupérer un utilisateur par ID
```bash
GET http://localhost:8080/api/users/{id}
```

### Créer un utilisateur
```bash
POST http://localhost:8080/api/users
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123"
}
```

### Mettre à jour un utilisateur
```bash
PUT http://localhost:8080/api/users/{id}
Content-Type: application/json

{
  "username": "john_updated",
  "email": "john.updated@example.com",
  "password": "newpassword"
}
```

### Supprimer un utilisateur
```bash
DELETE http://localhost:8080/api/users/{id}
```

## 🗄️ Base de Données H2

Console H2 accessible sur : http://localhost:8080/h2-console

**Configuration :**
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (vide)

## 🧪 Tests

Exécuter les tests unitaires :

```bash
mvn test
```

Générer le rapport de couverture :

```bash
mvn verify
```

## 🤖 Test avec le Bot IA

Ce projet est conçu pour tester le système de revue de code IA.

### Scénario de Test 1 : Code Propre

1. Créer une branche :
```bash
git checkout -b feature/clean-code-test
```

2. Le code actuel est propre et bien structuré
3. Pousser et créer une PR
4. Le bot devrait donner un bon score (>80)

### Scénario de Test 2 : Ajouter du Code avec Problèmes

Pour tester la détection de problèmes, créez une nouvelle branche et ajoutez un fichier avec des problèmes :

```bash
git checkout -b feature/code-with-issues-test
```

Créez `src/main/java/com/example/demo/service/BadCodeExample.java` avec des problèmes intentionnels (voir examples/ExampleBadCode.java).

Le bot devrait détecter :
- Injection SQL
- NullPointerException potentielles
- Mauvaise gestion des ressources
- Problèmes de sécurité

## 📊 Analyse Statique

### Checkstyle

```bash
mvn checkstyle:check
```

### PMD

```bash
mvn pmd:check
```

## 📝 Notes

- Les mots de passe sont stockés en clair pour la démo
- En production, utilisez BCrypt ou un algorithme de hashing sécurisé
- La base H2 est en mémoire et se réinitialise à chaque démarrage
- Pour une vraie application, utilisez PostgreSQL ou MySQL

## 🎯 Utilisation pour Tester le Bot IA

1. **Pousser le code initial** (code propre) → Score élevé
2. **Créer une feature avec problèmes** → Score faible, détection de bugs
3. **Corriger les problèmes** → Score s'améliore
4. **Observer les commentaires du bot** sur les PR

## 📚 Ressources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [H2 Database](https://www.h2database.com/)
- [JUnit 5](https://junit.org/junit5/)
- [Mockito](https://site.mockito.org/)

---

**🤖 Ce projet est prêt pour tester votre système de revue de code IA !**
