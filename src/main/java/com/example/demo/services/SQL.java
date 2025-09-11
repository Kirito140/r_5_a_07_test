
package com.example.demo.services;

import java.sql.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.demo.Main;

/**
 * Classe utilitaire pour exécuter des requêtes SQL (select, insert, update, delete)
 * en utilisant les paramètres de connexion définis dans application.yml.
 * Fournit un système de log dans Main.log pour chaque requête exécutée.
 */
@Service
public class SQL {
    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value ("${spring.datasource.driver-class-name}")
    private String driverClassName;

    /**
     * Ouvre une connexion à la base de données MySQL en utilisant les paramètres injectés.
     * Log la tentative de connexion et les erreurs éventuelles.
     * @return une connexion JDBC prête à l'emploi
     * @throws SQLException si la connexion échoue
     */
    public Connection getConnection() throws SQLException {
        try {
            // Charge dynamiquement le driver JDBC
            Class.forName(driverClassName);
        } catch (ClassNotFoundException e) {
            // Log si le driver n'est pas trouvé
            Main.log.severe("Driver JDBC non trouvé !");
            Main.log.severe(e.getMessage());
        }
        // Log la tentative de connexion
        Main.log.info("Connexion à la base de données : " + url);
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * Exécute une requête SELECT paramétrée et retourne le ResultSet.
     * Log la requête finale et le nombre de lignes obtenues.
     * @param query la requête SQL avec des ? pour les paramètres
     * @param params les valeurs à injecter dans la requête
     * @return le ResultSet positionné avant la première ligne
     * @throws SQLException en cas d'erreur SQL
     */
    public ResultSet select(String query, Object[] params) throws SQLException {
        String finalQuery = buildFinalQuery(query, params); // Construit la requête finale pour le log
        Main.log.info("SELECT: " + finalQuery); // Log la requête
        Connection conn = getConnection(); // Ouvre la connexion
        // Prépare la requête avec possibilité de naviguer dans le ResultSet
        PreparedStatement stmt = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        if (params != null) {
            // Injecte chaque paramètre dans la requête
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
        }
        ResultSet rs = stmt.executeQuery(); // Exécute la requête
        rs.last(); // Va à la dernière ligne pour compter
        int rowCount = rs.getRow(); // Nombre de lignes obtenues
        rs.beforeFirst(); // Repositionne le curseur avant la première ligne
        Main.log.info("Résultat: " + rowCount + " ligne(s)"); // Log le résultat

        // Log le contenu du ResultSet sous forme de tableau avec largeur ajustée
        ResultSetMetaData meta = rs.getMetaData();
        int colCount = meta.getColumnCount();
        // Calcul de la largeur max de chaque colonne
        int[] widths = new int[colCount];
        for (int i = 0; i < colCount; i++) {
            widths[i] = meta.getColumnLabel(i + 1).length();
        }
        while (rs.next()) {
            for (int i = 0; i < colCount; i++) {
                String val = rs.getString(i + 1);
                if (val != null && val.length() > widths[i]) widths[i] = val.length();
            }
        }
        rs.beforeFirst();
        // Construction du tableau aligné
        StringBuilder table = new StringBuilder();
        // En-têtes
        table.append("| ");
        for (int i = 0; i < colCount; i++) {
            String label = meta.getColumnLabel(i + 1);
            table.append(String.format("%-" + widths[i] + "s | ", label));
        }
        table.append("\n");
        // Séparateur
        table.append("|");
        for (int i = 0; i < colCount; i++) {
            for (int j = 0; j < widths[i] + 2; j++) table.append("-");
            table.append("|");
        }
        table.append("\n");
        // Données
        while (rs.next()) {
            table.append("| ");
            for (int i = 0; i < colCount; i++) {
                String val = rs.getString(i + 1);
                if (val == null) val = "";
                table.append(String.format("%-" + widths[i] + "s | ", val));
            }
            table.append("\n");
        }
        rs.beforeFirst(); // repositionne le curseur pour l'appelant
        Main.log.info("Tableau résultat :\n" + table);
        return rs;
    }

    /**
     * Exécute une requête INSERT, UPDATE ou DELETE paramétrée.
     * Log la requête finale et le nombre de lignes affectées.
     * @param query la requête SQL avec des ? pour les paramètres
     * @param params les valeurs à injecter dans la requête
     * @return le nombre de lignes affectées
     * @throws SQLException en cas d'erreur SQL
     */
    public int executeUpdate(String query, Object[] params) throws SQLException {
        String finalQuery = buildFinalQuery(query, params); // Construit la requête finale pour le log
        Main.log.info("UPDATE: " + finalQuery); // Log la requête
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            if (params != null) {
                // Injecte chaque paramètre dans la requête
                for (int i = 0; i < params.length; i++) {
                    stmt.setObject(i + 1, params[i]);
                }
            }
            int result = stmt.executeUpdate(); // Exécute la requête
            Main.log.info("Résultat: " + result + " ligne(s) affectée(s)"); // Log le résultat
            return result;
        } catch (SQLException e) {
            Main.log.severe("Erreur SQL: " + e.getMessage()); // Log l'erreur
            throw e;
        }
    }

    /**
     * Construit la requête SQL finale pour le log (remplace les ? par les valeurs réelles).
     * @param query la requête SQL avec des ?
     * @param params les valeurs à injecter
     * @return la requête complète prête à être logguée
     */
    private String buildFinalQuery(String query, Object[] params) {
        if (params == null || params.length == 0) return query;
        StringBuilder sb = new StringBuilder();
        int paramIdx = 0;
        for (char c : query.toCharArray()) {
            // Remplace chaque ? par la valeur correspondante
            if (c == '?' && paramIdx < params.length) {
                Object val = params[paramIdx++];
                if (val instanceof String) sb.append("'" + val + "'");
                else sb.append(val);
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
