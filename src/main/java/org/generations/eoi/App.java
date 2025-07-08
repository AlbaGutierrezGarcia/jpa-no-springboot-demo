package org.generations.eoi;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.generations.eoi.entity.Product;

public class App {

    private static EntityManagerFactory emf;

    public static void main( String[] args )
    {
        emf = Persistence.createEntityManagerFactory("mi-unidad");
        EntityManager em = null;

        try {

            em = emf.createEntityManager();
            em.getTransaction().begin();

            Product product1 = new Product("laptop", 900.00);
            Product product2 = new Product("tablet", 300.00);

            em.persist(product1);
            em.persist(product2);

            em.getTransaction().commit();

            System.out.println( "Productos creados: "+product1.getId()+", "+product2.getId() );


        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally{
            if(emf != null && emf.isOpen()){
                emf.close();
            }
        }
    }
}

