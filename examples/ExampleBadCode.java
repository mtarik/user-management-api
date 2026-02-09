package com.example.demo;

import java.sql.*;
import java.util.*;
import java.io.*;
import java.security.MessageDigest;
import java.net.*;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Exemple de code avec plusieurs problèmes pour tester le système de revue
 * Ce fichier contient intentionnellement des erreurs de sécurité, performance et style
 */
public class ExampleBadCode {

    // Problème: Variable publique mutable
    public static String API_KEY = "secret-key-1234_5";

    // Problème: Mot de passe en dur dans le code
    private static final String DB_PASSWORD = "admin123!";
    private static final String ADMIN_PASSWORD = "P@ssw0rd";

    // Problème: Clé de chiffrement exposée
    public static byte[] ENCRYPTION_KEY = "1234567890123456".getBytes();

    // Problème: URL sensible en dur
    public String internalApiUrl = "http://192.168.1.100:8080/admin/api";

    private Connection connection;

    // Problème: Pas de fermeture de ressource
    public void connectToDatabase(String host, String user, String password) {
        try {
            connection = DriverManager.getConnection(
                "jdbc:mysql://" + host + "/mydb", user, password
            );
        } catch (SQLException e) {
            e.printStackTrace(); // Problème: Mauvaise gestion des exceptions
        }
    }

    // Problème CRITIQUE: SQL Injection
    public User getUserById(String userId) {
        try {
            Statement stmt = connection.createStatement();
            // DANGER: Concaténation directe - SQL Injection!
            String query = "SELECT * FROM users WHERE id = '" + userId + "'";
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Problème: Méthode trop complexe, boucles imbriquées
    public List<String> processData(List<List<String>> data) {
        List<String> result = new ArrayList<>();

        for (int i = 0; i < data.size(); i++) {
            for (int j = 0; j < data.get(i).size(); j++) {
                String item = data.get(i).get(j);
                if (item != null) {
                    if (item.length() > 0) {
                        if (!item.equals("")) {
                            if (item.startsWith("prefix")) {
                                result.add(item.toLowerCase());
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    // Problème: Comparaison de String avec ==
    public boolean checkStatus(String status) {
        if (status == "active") { // ERREUR: Utiliser equals()
            return true;
        }
        return false;
    }

    // Problème: NullPointerException potentielle
    public int calculateTotal(List<Integer> numbers) {
        int total = 0;
        for (Integer num : numbers) {
            total += num; // Peut crasher si num est null
        }
        return total;
    }

    // Problème: Fuite mémoire potentielle avec les listeners
    public void addListener(EventListener listener) {
        listeners.add(listener); // Jamais retiré!
    }

    private List<EventListener> listeners = new ArrayList<>();

    // Problème: Synchronisation manquante
    public void incrementCounter() {
        counter++; // Race condition dans un environnement multi-thread
    }

    private int counter = 0;

    // Problème: Paramètres trop nombreux
    public void createUser(String firstName, String lastName, String email,
                          String phone, String address, String city,
                          String country, String zipCode, String company) {
        // Trop de paramètres, utiliser un objet Builder
    }

    // Problème: Catch trop générique
    public void riskyOperation() {
        try {
            // Code risqué
            int result = 10 / 0;
        } catch (Exception e) { // Trop générique!
            System.out.println("Error"); // Perte d'information
        }
    }

    // Problème: Utilisation de types génériques raw
    public List getItems() { // Devrait être List<Item>
        return new ArrayList();
    }

    // Problème: Magic numbers
    public boolean isValidAge(int age) {
        if (age > 18 && age < 120) { // Magic numbers!
            return true;
        }
        return false;
    }

    // Problème: Resource leak
    public String readFile(String path) {
        try {
            Scanner scanner = new Scanner(new File(path));
            return scanner.nextLine();
            // Scanner pas fermé!
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    // Classe interne qui devrait être static
    class InnerClass {
        public void doSomething() {
            System.out.println("Hello");
        }
    }

    // Problème CRITIQUE: Command Injection
    public String executeCommand(String userInput) {
        try {
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec("cmd /c " + userInput); // DANGER!
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            return reader.readLine();
        } catch (Exception e) {
            return null;
        }
    }

    // Problème CRITIQUE: Path Traversal
    public String readUserFile(String filename) {
        try {
            // DANGER: L'utilisateur peut accéder à n'importe quel fichier
            File file = new File("/uploads/" + filename);
            Scanner scanner = new Scanner(file);
            return scanner.nextLine();
        } catch (Exception e) {
            return null;
        }
    }

    // Problème: Cryptographie faible (MD5)
    public String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5"); // FAIBLE!
            byte[] hash = md.digest(password.getBytes());
            return new String(hash);
        } catch (Exception e) {
            return password; // Retourne le mot de passe en clair si erreur!
        }
    }

    // Problème: Random non sécurisé pour token
    public String generateToken() {
        Random random = new Random(); // Pas sécurisé pour la crypto!
        return String.valueOf(random.nextLong());
    }

    // Problème: Désérialisation non sécurisée
    public Object deserialize(byte[] data) {
        try {
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
            return ois.readObject(); // DANGER: Désérialisation arbitraire!
        } catch (Exception e) {
            return null;
        }
    }

    // Problème: SSRF (Server-Side Request Forgery)
    public String fetchUrl(String url) {
        try {
            URL urlObj = new URL(url); // L'utilisateur contrôle l'URL!
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlObj.openStream()));
            return reader.readLine();
        } catch (Exception e) {
            return null;
        }
    }

    // Problème: Log de données sensibles
    public void loginUser(String username, String password) {
        System.out.println("Login attempt: user=" + username + ", password=" + password);
        // Le mot de passe est loggé en clair!
    }

    // Problème: Concaténation de String dans une boucle
    public String buildReport(List<String> items) {
        String result = "";
        for (String item : items) {
            result = result + item + "\n"; // Très inefficace!
        }
        return result;
    }

    // Problème: Thread.sleep dans un bloc synchronized
    public synchronized void slowOperation() {
        try {
            Thread.sleep(10000); // Bloque tous les threads!
        } catch (InterruptedException e) {
            // Ignore l'interruption
        }
    }

    // Problème: Double-checked locking cassé
    private static ExampleBadCode instance;
    public static ExampleBadCode getInstance() {
        if (instance == null) {
            synchronized (ExampleBadCode.class) {
                if (instance == null) {
                    instance = new ExampleBadCode(); // Race condition!
                }
            }
        }
        return instance;
    }

    // Problème: Comparaison de float avec ==
    public boolean checkPrice(float price) {
        if (price == 19.99f) { // ERREUR: Jamais comparer des floats avec ==
            return true;
        }
        return false;
    }

    // Problème: Infinite loop potentiel
    public void waitForCondition(boolean condition) {
        while (!condition) {
            // Boucle infinie car condition ne change jamais
        }
    }

    // Problème: Retourne null au lieu de collection vide
    public List<String> findItems(String query) {
        if (query == null) {
            return null; // Devrait retourner Collections.emptyList()
        }
        return new ArrayList<>();
    }

    // Problème: Chiffrement ECB (mode non sécurisé)
    public byte[] encrypt(byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding"); // ECB non sécurisé!
            SecretKeySpec key = new SecretKeySpec(ENCRYPTION_KEY, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(data);
        } catch (Exception e) {
            return data; // Retourne les données non chiffrées!
        }
    }

    // Problème: Validation d'email avec regex incorrecte
    public boolean isValidEmail(String email) {
        return email.contains("@"); // Validation trop permissive!
    }

    // Problème: Trust all certificates (MITM vulnerability)
    public void connectToServer() {
        // Accepte tous les certificats SSL - TRÈS DANGEREUX
        System.setProperty("javax.net.ssl.trustStore", "NONE");
    }

    // Problème: Variables inutilisées et code mort
    public void deadCodeExample() {
        int unusedVar = 42;
        String neverUsed = "test";
        if (false) {
            System.out.println("Ce code ne s'exécute jamais");
        }
        return;
        // System.out.println("Unreachable code");
    }

    // Problème: Hardcoded IP et credentials
    public Connection getAdminConnection() throws SQLException {
        return DriverManager.getConnection(
            "jdbc:mysql://192.168.1.50:3306/production",
            "root",
            "toor123"
        );
    }
}

// Classe User simple (pour l'exemple)
class User {
    private int id;
    private String name;
    private String email;

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
}

interface EventListener {
    void onEvent();
}
