package org.itzstonlex.recon.sql;

import org.itzstonlex.recon.sql.connection.SqliteDatabaseConnection;
import org.itzstonlex.recon.sql.event.ReconSqlEventListenerAdapter;
import org.itzstonlex.recon.sql.request.field.ReconSqlFieldType;
import org.itzstonlex.recon.sql.request.field.impl.IndexedRequestField;
import org.itzstonlex.recon.sql.request.field.impl.ValuedRequestField;

import java.io.File;
import java.sql.Timestamp;

public class TestSqliteConnection {

    public static final String EMPTY_RESPONSE_DEBUG = "Sqlite Response is empty (0)!";

    public static void main(String[] args) {
        SqliteDatabaseConnection connection = ReconSql.getInstance().createSqliteConnection(new File("sqlite.db"));
        connection.setEventHandler(new SqliteEventHandler());

        connection.connect();

        // Get users table
        ReconSqlTable usersTable = connection.createOrGetTable("users", create -> {

            create.push(IndexedRequestField.createPrimaryNotNull(ReconSqlFieldType.INT, "Id")
                    .putIndex(IndexedRequestField.IndexType.AUTO_INCREMENT));

            create.push(IndexedRequestField.createNotNull(ReconSqlFieldType.VAR_CHAR, "FirstName"));
            create.push(IndexedRequestField.createNotNull(ReconSqlFieldType.VAR_CHAR, "LastName"));

            create.push(IndexedRequestField.create(ReconSqlFieldType.TIMESTAMP, "Birthday"));
        });

        // Remove all values in table.
        usersTable.clear();

        // Insert new field to users table.
        usersTable.insert(insert -> {

            insert.push(ValuedRequestField.create("FirstName", "Misha"));
            insert.push(ValuedRequestField.create("LastName", "Leyn"));

            insert.push(ValuedRequestField.create("Birthday", new Timestamp(System.currentTimeMillis())));
        });

        // Getting a first table response field.
        usersTable.selectAll(response -> {

            if (!response.next()) {
                connection.getLogger().info(EMPTY_RESPONSE_DEBUG);
            }
            else {

                int identifier = response.getInt("Id");

                String firstName = response.getString("FirstName");
                String lastName = response.getString("LastName");

                long birthdayAsMillis = response.getTimestamp("Birthday").getTime();

                connection.getLogger().info("Sqlite Response find!");
                connection.getLogger().info(" ID: " + identifier);
                connection.getLogger().info(" First name: " + firstName);
                connection.getLogger().info(" Last name: " + lastName);
                connection.getLogger().info(" Birthday Time (ms): " + birthdayAsMillis);
            }
        });
    }

    public static class SqliteEventHandler extends ReconSqlEventListenerAdapter {

        private File getSqliteStorage(ReconSqlConnection connection) {
            return ((SqliteDatabaseConnection) connection).getFileStorage();
        }

        @Override
        public void onConnected(ReconSqlConnection connection) {
            connection.getLogger().info("Success connected to " + getSqliteStorage(connection).getName());
        }

        @Override
        public void onReconnect(ReconSqlConnection connection) {
            connection.getLogger().info("Try reconnect to " + getSqliteStorage(connection).getName());
        }

        @Override
        public void onDisconnect(ReconSqlConnection connection) {
            connection.getLogger().info(getSqliteStorage(connection).getName() + " was disconnected");
        }

        @Override
        public void onExecute(ReconSqlConnection connection, String sql) {
            connection.getLogger().info("Request sent: " + sql);
        }
    }
}
