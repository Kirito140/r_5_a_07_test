#!/bin/bash

# Nom du conteneur
DB_CONTAINER="demo-mysql"

# Vérifie si le conteneur existe déjà
if [ "$(docker ps -q -f name=$DB_CONTAINER)" ]; then
    echo "✅ Conteneur $DB_CONTAINER déjà lancé"
else
    echo "🚀 Démarrage de la base de données..."
    docker compose up -d db
fi

# Attente que la BDD soit prête (ping MySQL)
echo "⏳ Attente que MySQL soit prêt..."
until docker exec $DB_CONTAINER mysqladmin ping -h localhost -u$DB_USERNAME -p$DB_PASSWORD --silent; do
    sleep 2
done

echo "✅ Base prête, exportation des variables d'environnemnt..."
export $(cat .env | xargs)

echo "✅ Lancement de l'application."
mvn spring-boot:run
