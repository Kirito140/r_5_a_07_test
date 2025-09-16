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
	void testGetConnection() throws SQLException {
		// Vérifie que la connexion s'ouvre correctement
		assertNotNull(sql.getConnection(), "La connexion ne doit pas être null");
	}

	@Test
	void testExecuteUpdateAndSelect() throws SQLException {
		// Crée une table test
		sql.executeUpdate("CREATE TABLE IF NOT EXISTS users(id INT PRIMARY KEY, name VARCHAR(50))", null);

		// Insère une ligne
		int rows = sql.executeUpdate("INSERT INTO users(id, name) VALUES(?, ?)", new Object[] { 1, "Alice" });
		assertEquals(1, rows, "Une seule ligne doit être insérée");

		// Sélectionne la ligne
		ResultSet rs = sql.select("SELECT * FROM users WHERE id = ?", new Object[] { 1 });
		assertTrue(rs.next(), "Le ResultSet doit contenir une ligne");
		assertEquals("Alice", rs.getString("name"), "Le nom doit correspondre à 'Alice'");
	}

	@Test
	void testUpdate() throws SQLException {
		// Met à jour la ligne existante
		int rows = sql.executeUpdate("UPDATE users SET name=? WHERE id=?", new Object[] { "Bob", 1 });
		assertEquals(1, rows, "Une seule ligne doit être affectée");

		ResultSet rs = sql.select("SELECT * FROM users WHERE id=?", new Object[] { 1 });
		assertTrue(rs.next());
		assertEquals("Bob", rs.getString("name"), "Le nom doit maintenant être 'Bob'");
	}
}
