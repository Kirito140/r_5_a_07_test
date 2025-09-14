FROM mysql:8.0

# Copie le script d'initialisation pour créer la BDD et les tables automatiquement
COPY init.sql /docker-entrypoint-initdb.d/
