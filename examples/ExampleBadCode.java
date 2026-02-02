package com.example.demo;

import java.sql.*;
import java.util.*;

/**
 * Exemple de code avec plusieurs problèmes pour tester le système de revue
 * Ce fichier contient intentionnellement des erreurs de sécurité, performance et style
 */
public class ExampleBadCode {

    // Problème: Variable publique mutable
    public static String API_KEY = "secret-key-12345";

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
