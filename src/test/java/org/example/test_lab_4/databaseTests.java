package org.example.test_lab_4;

import org.example.lab4.Table;
import org.junit.Assert;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

public class databaseTests
{
    @Test
    public void shouldFindByName() throws SQLException {
        ResultSet byName1 = Table.selectByName("MOLOKO");
        String a = " " ;
        try{
            while (byName1.next()) {
                a = byName1.getString("name");

            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        Assert.assertEquals( "MOLOKO",a);
    }

}
