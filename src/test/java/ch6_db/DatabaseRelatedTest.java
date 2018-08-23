package ch6_db;

import org.junit.*;

public class DatabaseRelatedTest {
    @BeforeClass
    public static void setupForTheClass(){ System.out.println("setupForTheClass() is called"); }

    @AfterClass
    public static void cleanUpAfterTheClass(){
        System.out.println("cleanAfterClass() is called");
    }

    @Before
    public void setupForEachMethod(){
        System.out.println("setupForEachMethod() is called");
    }

    @After
    public void cleanUpAfterEachMethod(){
        System.out.println("cleanAfterEachMethod() is called");
    }

    @Test
    public void testMethodOne(){ System.out.println("testMethodOne() is called"); }

    @Test
    public void testMethodTwo(){ System.out.println("testMethodTwo() is called"); }
}
