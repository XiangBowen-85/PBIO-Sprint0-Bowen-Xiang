<?php

declare(strict_types=1);

/**
 * Capa de acceso a la base de datos.
 *
 * Esta clase solamente se encarga de crear
 * y proporcionar una conexión PDO.
 */
final class Database
{
    private ?PDO $connection = null;

    /**
     * Devuelve una conexión PDO reutilizable.
     */
    public function getConnection(): PDO
    {
        if ($this->connection !== null) {
            return $this->connection;
        }

        $dsn =
            'mysql:host=' . DB_HOST .
            ';dbname=' . DB_NAME .
            ';charset=utf8mb4';

        $this->connection = new PDO(
            $dsn,
            DB_USER,
            DB_PASSWORD,
            [
                PDO::ATTR_ERRMODE =>
                    PDO::ERRMODE_EXCEPTION,

                PDO::ATTR_DEFAULT_FETCH_MODE =>
                    PDO::FETCH_ASSOC,

                PDO::ATTR_EMULATE_PREPARES =>
                    false
            ]
        );

        return $this->connection;
    }
}