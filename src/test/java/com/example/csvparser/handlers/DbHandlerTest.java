package com.example.csvparser.handlers;

import com.example.csvparser.model.PageVisitModel;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

class DbHandlerTest {
    static DbHandler subject;
    static Statement stmt;

    @BeforeAll
    static void setup() throws SQLException {
        stmt = DriverManager.getConnection("jdbc:h2:file:./db/testdb").createStatement();
        subject = new DbHandler("jdbc:h2:file:./db/testdb");
    }

    private static void insertRandomData(int count) throws SQLException {
        for (int i = 0; i < count; i++) {
            stmt.executeUpdate(
                    "INSERT INTO pagevisits values ('abc-" + new Random().nextInt()
                            + "', '123', 'google.com')");
        }
    }

    @Test
    void testDbCreationOnInit() throws SQLException {
        // given
        stmt.execute(
                "CREATE TABLE IF NOT EXISTS pagevisits(email VARCHAR, phone VARCHAR, source VARCHAR, PRIMARY KEY(email,phone));");
        insertRandomData(1);

        // when
        subject.init();

        // then
        ResultSet rs = stmt.executeQuery("Select * from pagevisits;");
        Assertions.assertFalse(rs.next());
    }

    @Test
    void testDeleteAll() throws SQLException {
        // given
        subject.init();
        insertRandomData(5);

        // when
        subject.deleteAll();

        // then
        ResultSet rs = stmt.executeQuery("Select * from pagevisits;");
        Assertions.assertFalse(rs.next());
    }

    @Test
    void testInsert() throws SQLException {
        // given
        subject.init();

        // when
        int rowsInserted = subject.insert(new PageVisitModel("email", "phone", "source"));

        // then
        Assertions.assertEquals(1, rowsInserted);
        ResultSet rs = stmt.executeQuery("Select * from pagevisits;");
        Assertions.assertTrue(rs.next());
        Assertions.assertEquals("email", rs.getString(1));
        Assertions.assertEquals("phone", rs.getString(2));
        Assertions.assertEquals("source", rs.getString(3));
    }

    //region - null check
    @Test
    void testInsertWithNullData_source() throws SQLException {
        // given
        subject.init();

        // when
        int rowsInserted = subject.insert(new PageVisitModel("email", "phone", null));

        // then
        Assertions.assertEquals(-1, rowsInserted);
        ResultSet rs = stmt.executeQuery("Select * from pagevisits;");
        Assertions.assertFalse(rs.next());
    }

    @Test
    void testInsertWithNullData_phone() throws SQLException {
        // given
        subject.init();

        // when
        subject.insert(new PageVisitModel("email", null, "source"));

        // then
        ResultSet rs = stmt.executeQuery("Select * from pagevisits;");
        Assertions.assertFalse(rs.next());
    }

    @Test
    void testInsertWithNullData_email() throws SQLException {
        // given
        subject.init();

        // when
        subject.insert(new PageVisitModel(null, "phone", "source"));

        // then
        ResultSet rs = stmt.executeQuery("Select * from pagevisits;");
        Assertions.assertFalse(rs.next());
    }
    //endregion - null check

    //region - empty check
    @Test
    void testInsertWithEmptyData_sorce() throws SQLException {
        // given
        subject.init();

        // when
        subject.insert(new PageVisitModel("email", "phone", ""));

        // then
        ResultSet rs = stmt.executeQuery("Select * from pagevisits;");
        Assertions.assertFalse(rs.next());
    }

    @Test
    void testInsertWithEmptyData_phone() throws SQLException {
        // given
        subject.init();

        // when
        subject.insert(new PageVisitModel("email", "", "source"));

        // then
        ResultSet rs = stmt.executeQuery("Select * from pagevisits;");
        Assertions.assertFalse(rs.next());
    }

    @Test
    void testInsertWithEmptyData_email() throws SQLException {
        // given
        subject.init();

        // when
        subject.insert(new PageVisitModel("", "phone", "source"));

        // then
        ResultSet rs = stmt.executeQuery("Select * from pagevisits;");
        Assertions.assertFalse(rs.next());
    }

    //endregion - empty check

    @Test
    void testGetCount() {
        // given
        subject.init();
        subject.insert(new PageVisitModel("email1", "phone", "source"));
        subject.insert(new PageVisitModel("email2", "phone", "source"));

        // then
        Assertions.assertEquals(2, subject.getCount());

    }

    @Test
    void testGetCountWithDuplicates() {
        // given
        subject.init();
        subject.insert(new PageVisitModel("email1", "phone", "source"));
        subject.insert(new PageVisitModel("email2", "phone", "source"));
        subject.insert(new PageVisitModel("email3", "phone", "source"));
        subject.insert(new PageVisitModel("email3", "phone", "source"));

        // then
        Assertions.assertEquals(3, subject.getCount());
    }


}