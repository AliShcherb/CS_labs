package org.example.lab4;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Main {
    public final static String dbName="database.db";
    public final static String tableName="MAGAZINCHIK";

    public static void main(String[] args) {
        DB.connect();
        Table.create();
        Table.cleanDatabase();

        Table.insert(1, "MOLOKO",29.19,5);
        Table.insert(2, "GRECHKA",40,100);//❤

        Integer idThree = Table.insert("MORKVA",10,20);
        Integer idFour = Table.insert("KOVBASKA",150,1);
        Integer idFive = Table.insert("POMIDORKA",11,220);

        ResultSet all = Table.selectAll();
        printResultSet("All products", all);

        Product productByName = Table.selectByName("MOLOKO");
        System.out.println("Find product with name \"MOLOKO\"");

        ResultSet byAmount_more = Table.selectWhereAmountBiggerThan(15);
        printResultSet("Find where amount of products more than 15", byAmount_more);

        ResultSet byAmount_less = Table.selectWhereAmountSmallerThan(10);
        printResultSet("Find where amount of products less than 10", byAmount_less);

        ResultSet byPrice_more = Table.selectWherePriceBiggerThan(25);
        printResultSet("Find where price of product more than 25", byPrice_more);

        ResultSet byPrice_less = Table.selectWherePriceSmallerThan(100);
        printResultSet("Find where price of product less than 100", byPrice_less);

        ResultSet oneLimitOffset = Table.selectOneLimitOffset(1, 2);
        printResultSet("oneLimitOffset", oneLimitOffset);

        Table.delete(4);

        all = Table.selectAll();
        printResultSet("All products", all);

        Table.update_name(1, "NE_MOLOKO");
        Table.update_amount("NE_MOLOKO", 50);
        Table.update_price(3,13);

        all = Table.selectAll();
        printResultSet("All products", all);

        Table.dropTable();

        DB.close();
    }

    public static void printResultSet(String resultSetName, ResultSet resultSet) {
        System.out.println(resultSetName + ":");
        try {
            while (resultSet.next()) {
                System.out.println(resultSet.getInt("id") +  "\t" + resultSet.getString("name"));
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        System.out.println();
    }
}


