package com.example.demo;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * Exemple de code bien écrit avec les bonnes pratiques Java
 * Démontre la sécurité, performance et maintenabilité
 */
public class ExampleGoodCode {

    // Constantes correctement définies
    private static final String DATABASE_URL = "jdbc:mysql://localhost/mydb";
    private static final int MIN_AGE = 18;
    private static final int MAX_AGE = 120;

    private final Connection connection;

    // Injection de dépendance via constructeur
    public ExampleGoodCode(Connection connection) {
        this.connection = Objects.requireNonNull(connection, "Connection cannot be null");
    }

    /**
     * Récupère un utilisateur par son ID en utilisant PreparedStatement
     * Prévient les injections SQL
     *
     * @param userId L'identifiant de l'utilisateur
     * @return L'utilisateur trouvé ou Optional.empty()
     * @throws SQLException Si une erreur de base de données survient
     */
    public Optional<User> getUserById(long userId) throws SQLException {
        String query = "SELECT id, name, email FROM users WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        }

        return Optional.empty();
    }

    /**
     * Mappe un ResultSet vers un objet User
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        return new User(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("email")
        );
    }

    /**
     * Traite les données en utilisant les Streams Java 8+
     * Plus lisible et performant
     *
     * @param data Les données à traiter
     * @return Liste des éléments filtrés et transformés
     */
    public List<String> processData(List<List<String>> data) {
        Objects.requireNonNull(data, "Data cannot be null");

        return data.stream()
            .flatMap(Collection::stream)
            .filter(Objects::nonNull)
            .filter(item -> !item.isEmpty())
            .filter(item -> item.startsWith("prefix"))
            .map(String::toLowerCase)
            .collect(Collectors.toList());
    }

    /**
     * Vérifie le statut avec equals() au lieu de ==
     *
     * @param status Le statut à vérifier
     * @return true si actif
     */
    public boolean isActive(String status) {
        return "active".equals(status); // Évite NullPointerException
    }

    /**
     * Calcule le total avec gestion des nulls
     *
     * @param numbers Liste de nombres (peut contenir des nulls)
     * @return La somme totale
     */
    public int calculateTotal(List<Integer> numbers) {
        if (numbers == null || numbers.isEmpty()) {
            return 0;
        }

        return numbers.stream()
            .filter(Objects::nonNull)
            .mapToInt(Integer::intValue)
            .sum();
    }

    /**
     * Compteur thread-safe
     */
    private final java.util.concurrent.atomic.AtomicInteger counter =
        new java.util.concurrent.atomic.AtomicInteger(0);

    public void incrementCounter() {
        counter.incrementAndGet();
    }

    public int getCounter() {
        return counter.get();
    }

    /**
     * Validation d'âge avec constantes
     *
     * @param age L'âge à valider
     * @return true si l'âge est valide
     */
    public boolean isValidAge(int age) {
        return age >= MIN_AGE && age <= MAX_AGE;
    }

    /**
     * Lecture de fichier avec gestion correcte des ressources
     *
     * @param path Chemin du fichier
     * @return Le contenu de la première ligne
     * @throws FileNotFoundException Si le fichier n'existe pas
     */
    public String readFirstLine(String path) throws FileNotFoundException {
        Objects.requireNonNull(path, "Path cannot be null");

        // try-with-resources ferme automatiquement le Scanner
        try (Scanner scanner = new Scanner(new File(path))) {
            return scanner.hasNextLine() ? scanner.nextLine() : "";
        }
    }

    /**
     * Pattern Builder pour éviter trop de paramètres
     */
    public static class UserBuilder {
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private String address;
        private String city;
        private String country;
        private String zipCode;
        private String company;

        public UserBuilder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public UserBuilder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public UserBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public UserBuilder address(String address) {
            this.address = address;
            return this;
        }

        public UserBuilder city(String city) {
            this.city = city;
            return this;
        }

        public UserBuilder country(String country) {
            this.country = country;
            return this;
        }

        public UserBuilder zipCode(String zipCode) {
            this.zipCode = zipCode;
            return this;
        }

        public UserBuilder company(String company) {
            this.company = company;
            return this;
        }

        public User build() {
            // Validation
            if (firstName == null || lastName == null || email == null) {
                throw new IllegalStateException("Required fields missing");
            }

            User user = new User();
            user.firstName = firstName;
            user.lastName = lastName;
            user.email = email;
            user.phone = phone;
            user.address = address;
            user.city = city;
            user.country = country;
            user.zipCode = zipCode;
            user.company = company;

            return user;
        }
    }

    /**
     * Gestion d'erreur spécifique avec logging approprié
     */
    public void performOperation() throws BusinessException {
        try {
            int result = riskyCalculation();
            processResult(result);
        } catch (ArithmeticException e) {
            // Log avec contexte
            System.err.println("Arithmetic error during operation: " + e.getMessage());
            throw new BusinessException("Failed to perform operation", e);
        }
    }

    private int riskyCalculation() {
        return 42 / getRandomDivisor();
    }

    private int getRandomDivisor() {
        return new Random().nextInt(10); // Pourrait être 0
    }

    private void processResult(int result) {
        System.out.println("Result: " + result);
    }

    /**
     * Classe interne static (ne garde pas référence vers la classe externe)
     */
    public static class StaticInnerClass {
        public void doSomething() {
            System.out.println("Optimized inner class");
        }
    }

    /**
     * Utilisation correcte des génériques
     */
    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT id, name, email FROM users";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }

        return Collections.unmodifiableList(users);
    }
}

/**
 * Classe User immutable avec tous les champs finaux
 */
final class User {
    private final long id;
    private final String name;
    private final String email;

    // Champs optionnels pour le Builder
    String firstName;
    String lastName;
    String phone;
    String address;
    String city;
    String country;
    String zipCode;
    String company;

    public User(long id, String name, String email) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.email = Objects.requireNonNull(email, "Email cannot be null");
    }

    User() {
        this(0, "", "");
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("User{id=%d, name='%s', email='%s'}", id, name, email);
    }
}

/**
 * Exception personnalisée pour la logique métier
 */
class BusinessException extends Exception {
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
