SELECT username, password, role
FROM "user"
WHERE "user".username = :username;