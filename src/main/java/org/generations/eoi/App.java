package org.generations.eoi;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import org.generations.eoi.entity.Product;

import java.util.List;

public class App {

    private static EntityManagerFactory emf;

    public static void main( String[] args )
    {
        ddbbProcess();
    }

    private static void ddbbProcess(){
        // 1. Crear el EntityManagerFactory
        // "my-jpa-unit" debe coincidir con el nombre de la unidad de persistencia en persistence.xml
        emf = Persistence.createEntityManagerFactory("mi-unidad");
        EntityManager em = null;

        try {
            // --- Operación de CREAR (Persistir) ---
            System.out.println("--- Creating products ---");
            em = emf.createEntityManager();
            em.getTransaction().begin();

            Product product1 = new Product("Laptop", 1200.00);
            Product product2 = new Product("Teclado Mecánico", 95.50);
            Product product3 = new Product("Monitor 27 pulgadas", 350.00);

            // Persiste el objeto en la base de datos
            em.persist(product1);
            em.persist(product2);
            em.persist(product3);

            em.getTransaction().commit(); //Confirma la transacción

            System.out.println( "Products created: "+product1.getId()+", "+product2.getId()+", "+product3.getId());

            // --- Operación de LEER (Buscar por ID) ---
            System.out.println("\n--- Searching for a product by ID ---");
            em = emf.createEntityManager(); // Nueva EntityManager para una nueva operación
            Product foundProduct = em.find(Product.class, product1.getId());
            if (foundProduct != null) {
                System.out.println("Product found: " + foundProduct);
            } else {
                System.out.println("Product with ID " + product1.getId() + " not found.");
            }
            em.close();

            // --- Operación de LEER (Listar todos los productos con JPQL) ---
            System.out.println("\n--- Listing alls products ---");
            em = emf.createEntityManager();
            TypedQuery<Product> query = em.createQuery("SELECT p FROM Product p", Product.class);
            List<Product> products = query.getResultList();
            products.forEach(System.out::println);
            em.close();

            // --- Operación de ACTUALIZAR ---
            System.out.println("\n--- Updating a product ---");
            em = emf.createEntityManager();
            em.getTransaction().begin();
            Product productToUpdate = em.find(Product.class, product2.getId());
            if (productToUpdate != null) {
                productToUpdate.setPrice(105.00); // Cambiamos el precio
                // No necesitamos llamar a em.merge() si el objeto está en el contexto de persistencia y ha sido modificado
                System.out.println("Updated product : " + productToUpdate);
            }
            em.getTransaction().commit();
            em.close();

            // Verificar la actualización
            System.out.println("\n--- Verifying update ---");
            em = emf.createEntityManager();
            Product updatedProduct = em.find(Product.class, product2.getId());
            System.out.println("Product after update: " + updatedProduct);
            em.close();

            // --- Operación de BORRAR ---
            System.out.println("\n--- Deleting a product ---");
            em = emf.createEntityManager();
            em.getTransaction().begin();
            Product productToDelete = em.find(Product.class, product3.getId());
            if (productToDelete != null) {
                em.remove(productToDelete); // Elimina el objeto
                System.out.println("Deleted product: " + productToDelete);
            }
            em.getTransaction().commit();
            em.close();

            // Verificar el borrado
            System.out.println("\n--- Verifying deletion ---");
            em = emf.createEntityManager();
            Product deletedProduct = em.find(Product.class, product3.getId());
            if (deletedProduct == null) {
                System.out.println("Product with ID " + product3.getId() + " was successfully deleted.");
            }
            em.close();

        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback(); //Rollback en caso de error
            }
            e.printStackTrace();

        } finally {
            // 2. Cerrar el EntityManagerFactory al finalizar
            if (emf != null && emf.isOpen()) {

                emf.close();
            }
        }
    }
}

