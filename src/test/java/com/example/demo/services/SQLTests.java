package com.example.demo.services;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SQLTests {

	@Autowired
	private SQL sql;

	@Test
	void testConnectionAndSelect() throws SQLException {
		// Vérifie que la connexion s'ouvre
		assertNotNull(sql.getConnection(), "La connexion ne doit pas être null");

		// Teste une requête SELECT sur la table users déjà créée via init.sql
		ResultSet rs = sql.select("SELECT * FROM users", null);
		assertTrue(rs.next(), "Le ResultSet doit contenir au moins une ligne");

		// Vérifie qu'Alice est bien présente
		boolean foundAlice = false;
		rs.beforeFirst();
		while (rs.next()) {
			if ("Alice".equals(rs.getString("name"))) {
				foundAlice = true;
				break;
			}
		}
		assertTrue(foundAlice, "Alice doit être présente dans la table users");
	}

	@Test
	void testInsertAndUpdate() throws SQLException {
		// Insert une nouvelle ligne
		int rowsInserted = sql.executeUpdate(
				"INSERT INTO users(name, email) VALUES(?, ?)",
				new Object[] { "Charlie", "charlie@example.com" });
		assertEquals(1, rowsInserted, "Une ligne doit être insérée");

		// Met à jour cette ligne
		int rowsUpdated = sql.executeUpdate(
				"UPDATE users SET name=? WHERE email=?",
				new Object[] { "Charlie Brown", "charlie@example.com" });
		assertEquals(1, rowsUpdated, "Une ligne doit être mise à jour");

		// Vérifie que l'update a été pris en compte
		ResultSet rs = sql.select("SELECT * FROM users WHERE email=?", new Object[] { "charlie@example.com" });
		assertTrue(rs.next());
		assertEquals("Charlie Brown", rs.getString("name"));
	}
}
