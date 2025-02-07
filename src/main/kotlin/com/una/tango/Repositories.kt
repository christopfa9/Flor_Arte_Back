package com.una.tango

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RoleRepository : JpaRepository<Role, Long>{
    fun findByName(@Param("name") name: String): Optional<Role>
}

@Repository
interface PrivilegeRepository : JpaRepository<Privilege, Long>

@Repository
interface UserRepository : JpaRepository<User, Long>{
    fun findByEmail(@Param("email") email: String): Optional<User>
}

@Repository
interface TagRepository : JpaRepository<Tag, Long>{
    fun findByNameContainsIgnoreCase(@Param("name")name:String): List<Tag>
}

@Repository
interface ProductRepository : JpaRepository<Product, Long>{

    /**
     * No se puede pasar el arreglo de IDs directamente.
     * Forma correcta: (nombre, categoria, {etiqueta1, ..., etiquetaN})
     * JDBC: (nombre, categoria, etiqueta1, ... etiquetaN)
     *
     * Solución: Pasar el arreglo como string desde el servicio y
     * hacer un cast a BIGINT[] (tipo de arreglo que recibe la funcion como parametro).
     *
     * Otra posible solución: Convertir función a procedimiento almacenado,
     * utilizar parametro de salida para obtener resultado y llamar utilizando @Procedure.
     *
     * */
    @Query("SELECT * FROM fun_findproducts(:name, :category, CAST(:tags AS BIGINT[]))", nativeQuery = true)
    fun search(@Param("name")name:String?, @Param("category")category: Long?, @Param("tags")tags: String?) : List<Product>

}

@Repository
interface CategoryRepository : JpaRepository<Category, Long>

@Repository
interface OrderRepository : JpaRepository<Order, Long>{
    fun findByClient(@Param("client") client: User) : List<Order>
    fun findByClientOrderByDateDescDeliveryDateAsc(@Param("client") client: User) : List<Order>
    fun findAllByOrderByDateDescDeliveryDateAsc() : List<Order>
}

@Repository
interface OrderDetailRepository : JpaRepository<OrderDetail, Long>

@Repository
interface OrderStatusRepository : JpaRepository<OrderStatus, Long>