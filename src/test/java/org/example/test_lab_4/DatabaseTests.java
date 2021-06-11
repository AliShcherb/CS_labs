package org.example.test_lab_4;

import org.example.lab4.DB;
import org.example.lab4.Product;
import org.example.lab4.Table;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DatabaseTests
{
    @Before
    public void setupDatabase() {
        DB.connect();
        Table.create();

        // make clean up in order to make sure that there is no rows inside the table
        Table.cleanDatabase();

        Table.insert("MOLOKO",29.19,5);
        Table.insert("GRECHKA",40,100);

        Table.insert("MORKVA",10,20);
        Table.insert("KOVBASKA",150,1);
        Table.insert("POMIDORKA",11,220);
    }

    @After
    public void cleanUp() {
        DB.close();
    }

    @Test
    public void shouldFindByName() {
        String productNameToSearch = "MOLOKO";
        Product milk = Table.selectByName(productNameToSearch);
        Assert.assertEquals(productNameToSearch, milk.getProductName());
    }

    @Test
    public void shouldDeleteProduct() {
        // Verify that the product exists
        Product existingProduct = Table.selectByName("GRECHKA");
        Assert.assertNotNull(existingProduct);

        // delete the product
        Table.deleteByName("GRECHKA");
        Product actualProduct = Table.selectByName("GRECHKA");

        // make sure we don't have such a product after deletion
        Assert.assertNull(actualProduct);
    }

    @Test
    public void shouldInsertNewProduct() {
        Table.insert("Napoleon",29.19,5);
        Product napoleonProduct = Table.selectByName("Napoleon");

        Assert.assertEquals("Napoleon", napoleonProduct.getProductName());
        Assert.assertEquals(29.19, napoleonProduct.getPrice(), 0.001);
        Assert.assertEquals(5L, napoleonProduct.getAmount().longValue());
    }

    @Test
    public void shouldUpdateProductAmount() {
        Table.update_amount("MOLOKO", 10);
        Product milk = Table.selectByName("MOLOKO");
        Assert.assertEquals(10, milk.getAmount().intValue());
    }
}
