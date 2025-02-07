package com.una.tango

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Profile
import org.springframework.test.context.jdbc.Sql
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

@Profile("initlocal")
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Sql(
    scripts = [
        "/delete-data.sql",
        "/import-categories.sql",
        "/import-tags.sql",
        "/import-privileges.sql",
        "/import-roles.sql",
        "/import-users.sql",
        "/import-order-status.sql",
        "/import-products.sql",
        "/import-orders.sql",
        "/import-order-details.sql"
   ],
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@Sql(
    scripts = ["/fun-findproducts.sql"],
    executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS
)
class LoadInitData (
    @Autowired
    val productRepository: ProductRepository,
    @Autowired
    val userRepository: UserRepository,
    @Autowired
    val tagRepository: TagRepository,
    @Autowired
    val categoryRepository: CategoryRepository,
    @Autowired
    val roleRepository: RoleRepository,
    @Autowired
    val orderStatusRepository: OrderStatusRepository,
    @Autowired
    val orderRepository: OrderRepository,
    @Autowired
    val orderDetailRepository: OrderDetailRepository,
    @Autowired
    val privilegeRepository: PrivilegeRepository
) {

    /***
     * Se verifica que se hayan cargado todos los productos
     */
    @Test
    fun testProductsFindAll() {
        val elementList: List<Product> = productRepository.findAll()
        Assertions.assertTrue(elementList.size == 29)
    }

    /***
     * Se verifica que se pueda buscar un producto por ID.
     */
    @Test
    fun testProductsFindById() {
        val id: Long = 12;
        val product = productRepository.findById(id);
        Assertions.assertTrue(product.isPresent && product.get().id == id);
    }

    /***
     * Se verifica que se pueda eliminar un producto.
     */
    @Test
    fun deleteProduct() {
        val product = productRepository.save(Product(name = "Producto de prueba", price = 100.0, quantity = 10));
        productRepository.deleteById(product.id!!);
        Assertions.assertTrue(productRepository.findById(product.id!!).isEmpty);
    }

    /***
     * Se verifica que se pueda crear un producto.
     */
    @Test
    fun createProduct() {
        val category = categoryRepository.findById(1);
        var product = Product(
            name = "Producto de prueba",
            category = category.get(),
            price = 100.0,
            quantity = 10,
            description = "",
            imageUrl = "",
            tags = null,
            orderDetails = null
        );
        product = productRepository.save(product);
        Assertions.assertTrue(productRepository.findById(product.id!!).isPresent);
    }

    /***
     * Se verifica que se pueda actualizar un producto.
     */
    @Test
    fun updateProduct() {
        val id: Long = 12;
        val product = productRepository.findById(id);
        product.get().name = "Producto de prueba 2";
        productRepository.save(product.get());
        Assertions.assertTrue(productRepository.findById(id).get().name == "Producto de prueba 2");
    }

    /***
     * Se verifica que se hayan cargado todos los tags.
     */
    @Test
    fun testTagsFindAll() {
        val elementList: List<Tag> = tagRepository.findAll()
        Assertions.assertTrue(elementList.size == 19)
    }

    /***
     * Se verifica que se pueda buscar un tag por ID.
     */
    @Test
    fun testTagsFindById() {
        val id: Long = 5;
        val tag = tagRepository.findById(id);
        Assertions.assertTrue(tag.isPresent && tag.get().id == id);
    }

    /***
     * Se verifica que se pueda eliminar un tag.
     */
    @Test
    fun deleteTag() {
        var tag = Tag(90, "Tag de prueba");
        tag = tagRepository.save(tag);
        tagRepository.deleteById(tag.id!!);
        Assertions.assertTrue(tagRepository.findById(tag.id!!).isEmpty);
    }

    /***
     * Se verifica que se pueda crear un tag.
     */
    @Test
    fun createTag() {
        var tag = Tag(name = "Tag de prueba");
        tag = tagRepository.save(tag);
        Assertions.assertTrue(tagRepository.findById(tag.id!!).isPresent);
    }

    /***
     * Se verifica que se pueda actualizar un tag.
     */

    @Test
    fun updateTag() {
        val id: Long = 5;
        val tag = tagRepository.findById(id);
        tag.get().name = "Tag de prueba 2";
        tagRepository.save(tag.get());
        Assertions.assertTrue(tagRepository.findById(id).get().name == "Tag de prueba 2");
    }

    /***
     * Se verifica que se hayan cargado todas las categorias.
     */
    @Test
    fun testCategoriesFindAll() {
        val elementList: List<Category> = categoryRepository.findAll()
        Assertions.assertTrue(elementList.size == 5)
    }

    /***
     * Se verifica que se pueda buscar una categoria por ID.
     */
    @Test
    fun testCategoriesFindById() {
        val id: Long = 5;
        val category = categoryRepository.findById(id);
        Assertions.assertTrue(category.isPresent && category.get().id == id);
    }

    /***
     * Se verifica que se pueda eliminar una categoria.
     */
    @Test
    fun deleteCategory() {
        var category = Category(90, "Categoria de prueba");
        category = categoryRepository.save(category);
        categoryRepository.deleteById(category.id!!);
        Assertions.assertTrue(categoryRepository.findById(category.id!!).isEmpty)
    }

    /***
     * Se verifica que se pueda crear una categoria.
     */
    @Test
    fun createCategory() {
        var category = Category(name = "Categoria de prueba");
        category = categoryRepository.save(category);
        Assertions.assertTrue(categoryRepository.findById(category.id!!).isPresent);
    }

    /***
     * Se verifica que se pueda actualizar una categoria.
     */
    @Test
    fun updateCategory() {
        val id: Long = 5;
        val category = categoryRepository.findById(id);
        category.get().name = "Categoria de prueba 2";
        categoryRepository.save(category.get());
        Assertions.assertTrue(categoryRepository.findById(id).get().name == "Categoria de prueba 2");
    }

    /***
     * Se verifica que se hayan cargado todos los roles
     */
    @Test
    fun testRolesFindAll() {
        val elementList: List<Role> = roleRepository.findAll()
        Assertions.assertTrue(elementList.size == 3)
    }

    /***
     * Se verifica que se pueda buscar un rol por ID.
     */
    @Test
    fun testRolesFindById() {
        val id: Long = 2;
        val role = roleRepository.findById(id);
        Assertions.assertTrue(role.isPresent && role.get().id == id);
    }

    /***
     * Se verifica que se pueda eliminar un rol.
     */
    @Test
    fun deleteRole() {
        // Create role and then delete it.
        val id: Long = 6;
        roleRepository.deleteById(id);
        Assertions.assertTrue(roleRepository.findById(id).isEmpty)
    }


    /***
     * Se verifica que se hayan cargado todos los usuarios
     */
    @Test
    fun testUsersFindAll() {
        val elementList: List<User> = userRepository.findAll()
        Assertions.assertTrue(elementList.size == 5)
    }

    /***
     * Se verifica que se pueda buscar un usuario por ID.
     */
    @Test
    fun testUsersFindById() {
        val id: Long = 102;
        val user = userRepository.findById(id);
        Assertions.assertTrue(user.isPresent && user.get().id == id);
    }

    /***
     * Se verifica que se pueda eliminar un usuario.
     */
    @Test
    fun deleteUser() {
        val role = roleRepository.findById(1);
        val user = userRepository.save(User(
            firstName = "Usuario de prueba",
            lastName = "Usuario de prueba",
            email = "testuser@mail.com",
            password = "12345",
            enabled = true
        ));
        userRepository.deleteById(user.id!!);
        Assertions.assertTrue(userRepository.findById(user.id!!).isEmpty)
    }

    /***
     * Se verifica que se pueda crear un usuario.
     */
    @Test
    fun createUser() {
        val role = roleRepository.findById(1);
        var user = User(
            firstName = "Usuario de prueba",
            lastName = "Usuario de prueba",
            email = "testuser@mail.com",
            password = "12345",
            enabled = true
        );
        user = userRepository.save(user);
        Assertions.assertNotNull(user.id);
        Assertions.assertTrue(
            userRepository.findById(user.id!!).isPresent
        );
    }

    /***
     * Se verifica que se pueda actualizar un usuario.
     */
    @Test
    fun updateUser() {
        val id: Long = 102;
        val user = userRepository.findById(id);
        user.get().firstName = "Usuario de prueba 2";
        userRepository.save(user.get());
        Assertions.assertTrue(userRepository.findById(id).get().firstName == "Usuario de prueba 2");
    }

    /***
     * Se verifica que se hayan listado todas las categorias
     */
    @Test
    fun testCategoryFindAll() { //
        val categoryList: List<Category> = categoryRepository.findAll()
        Assertions.assertTrue(categoryList.size == 5)
    }

    /**
     * Se verifica que se pueda desplegar la informacion de una categoria especifica
     */
    @Test
    fun testCategoryFindById() { //
        val category: Category = categoryRepository.findById(1).get()
        Assertions.assertTrue(category.id?.toInt() == 1)
    }

    /**
     * Se verifica que se pueda crear una nueva categoria
     */
    @Test
    fun testCreateCategory() {
        val newCategory = Category(name = "Categoria prueba")
        val savedCategory = categoryRepository.save(newCategory)
        Assertions.assertNotNull(savedCategory.id)
        Assertions.assertEquals("Categoria prueba", savedCategory.name)
    }

    /**
     * Se verifica que se pueda modificar una categoria
     */
    @Test
    fun testUpdateCategory() {
        val existingCategory = categoryRepository.findById(1).get()
        existingCategory.name = "Modificar Prueba"
        val updatedCategory = categoryRepository.save(existingCategory)
        Assertions.assertNotNull(updatedCategory.id)
        Assertions.assertEquals("Modificar Prueba", updatedCategory.name)
    }

    /**
     * Se verifica que se pueda eliminar una categoria
     */
    @Test
    fun testDeleteCategory() {
        var category = Category(name = "Categoria eliminar"); //CREAR UN NUEVO REGISTRO PARA ELIMINAR
        category = categoryRepository.save(category)
        val existingCategory = categoryRepository.findById(category.id!!).get() //VALIDAR QUE EXISTE
        categoryRepository.delete(existingCategory)
        val deletedCategory = categoryRepository.findById(category.id!!) //VALIDAR QUE YA NO EXISTE
        Assertions.assertTrue(deletedCategory.isEmpty)
    }

    /**
     * Se verifica que se hayan listado todas los ordenes
     */
    @Test
    fun testOrderFindAll() { //LISTAR TODAS LAS ORDENES
        val orderList: List<Order> = orderRepository.findAll()
        Assertions.assertTrue(orderList.size == 5)
    }

    /**
     * Se verifica que se pueda desplegar la informacion de una orden especifica
     */
    @Test
    fun testOrderFindById() {
        val order: Order = orderRepository.findById(1).get()
        Assertions.assertTrue(order.id?.toInt() == 1)
    }

    /**
     * Se verifica que se pueda crear una nueva orden
     */
    @Test
    fun testCreateOrder() { //CREAR NUEVA ORDEN
        val existingClient = userRepository.findById(101).get() //BUSCAR USER
        val existingStatus = orderStatusRepository.findById(1).get() //BUSCAR ESTADO
        val currentDate: Date =
            Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()) //FECHA ACTUAL
        val dateString = "2024-09-02"
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val deliveryDate: Date = dateFormat.parse(dateString)

        val newOrder = Order(
            date = currentDate,
            client = existingClient,
            total = 15.99,
            orderStatus = existingStatus,
            deliveryDate = deliveryDate,
            additionalInformation = "Entrega a las 3pm"
        )
        val savedOrder = orderRepository.save(newOrder)
        Assertions.assertNotNull(savedOrder.id)
        Assertions.assertEquals(15.99, savedOrder.total)
    }

    /**
     * Se verifica que se pueda eliminar una orden
     */
    @Test
    fun testDeleteOrder() {
        val existingClient = userRepository.findById(101).get() //BUSCAR USER
        val existingStatus = orderStatusRepository.findById(1).get() //BUSCAR ESTADO
        val currentDate: Date =
            Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()) //FECHA ACTUAL
        val dateString = "2024-09-02"
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val deliveryDate: Date = dateFormat.parse(dateString)

        val newOrder = Order(
            date = currentDate,
            client = existingClient,
            total = 15.99,
            orderStatus = existingStatus,
            deliveryDate = deliveryDate,
            additionalInformation = "Entrega a las 3pm"
        )
        val savedOrder = orderRepository.save(newOrder)

        val existingOrder = orderRepository.findById(savedOrder.id!!).get() //VALIDAR QUE EXISTE
        orderRepository.delete(existingOrder)
        val deletedOrder = orderRepository.findById(savedOrder.id!!) //VALIDAR QUE YA NO EXISTE
        Assertions.assertTrue(deletedOrder.isEmpty)
    }

    /**
     * Se verifica que se hayan listado todas los detalles de orden
     */
    @Test
    fun testOrderDetailFindAll() {
        val orderDetailList: List<OrderDetail> = orderDetailRepository.findAll()
        Assertions.assertTrue(orderDetailList.size == 6)
    }

    /**
     * Se verifica que se pueda desplegar la informacion de un detalle de orden especifico
     */
    @Test
    fun testOrderDetailFindById() {
        val orderDetail: OrderDetail = orderDetailRepository.findById(1).get()
        Assertions.assertTrue(orderDetail.id?.toInt() == 1)
    }

    /**
     * Se verifica que se pueda crear un nuevo detalle de orden
     */
    @Test
    fun testCreateOrderDetail() {
        val existingOrder = orderRepository.findById(5).get() //BUSCAR ORDEN
        val existingProduct = productRepository.findById(10).get() //BUSCAR PRODUCTO
        val newOrderDetail =
            OrderDetail(order = existingOrder, product = existingProduct, quantity = 1, price = 15.99, total = 15.99)
        val savedOrderDetail = orderDetailRepository.save(newOrderDetail)
        Assertions.assertNotNull(savedOrderDetail.id)
        Assertions.assertEquals(15.99, savedOrderDetail.total)
    }

    /**
     * Se verifica que se pueda eliminar un detalle de orden
     */
    @Test
    fun testDeleteOrderDetail() {
        val existingOrder = orderRepository.findById(5).get() //BUSCAR ORDEN
        val existingProduct = productRepository.findById(10).get() //BUSCAR PRODUCTO
        var orderDetail = OrderDetail(
            order = existingOrder,
            product = existingProduct,
            quantity = 1,
            price = 15.99,
            total = 15.99
        ) //CREAR UN NUEVO REGISTRO PARA ELIMINAR
        orderDetail = orderDetailRepository.save(orderDetail)
        val existingOrderDetail = orderDetailRepository.findById(orderDetail.id!!).get() //VALIDAR QUE EXISTE
        orderDetailRepository.delete(existingOrderDetail)
        val deletedOrderStatus = orderDetailRepository.findById(orderDetail.id!!) //VALIDAR QUE YA NO EXISTE
        Assertions.assertTrue(deletedOrderStatus.isEmpty)
    }
    /**
     * Se verifica que se hayan listado todas los estados de orden
     */
    @Test
    fun testOrderStatusFindAll() { //LISTAR TODOS LOS ESTADOS DE ORDEN
        val orderStatusList: List<OrderStatus> = orderStatusRepository.findAll()
        Assertions.assertTrue(orderStatusList.size == 3)
    }
    /**
     * Se verifica que se pueda buscar un estado de orden especifico
     */
    @Test
    fun testOrderStatusFindById() { //DESPLEGAR INFORMACION DE ESTADO ESPECIFICO
        val orderStatus: OrderStatus = orderStatusRepository.findById(1).get()
        Assertions.assertTrue(orderStatus.id?.toInt() == 1)
    }

    /**
     * Se verifica que se pueda crear un nuevo estado de orden
     */
    @Test
    fun testCreateOrderStatus() {
        val newOrderStatus = OrderStatus(name = "Estado prueba")
        val savedOrderStatus = orderStatusRepository.save(newOrderStatus)
        Assertions.assertNotNull(savedOrderStatus.id)
        Assertions.assertEquals("Estado prueba", savedOrderStatus.name)
    }

    /**
     * Se verifica que se pueda modificar un estado de orden
     */
    @Test
    fun testUpdateOrderStatus() { //MODIFICAR UN ESTADO DE ORDEN
        val existingOrderStatus = orderStatusRepository.findById(1).get()
        existingOrderStatus.name = "Modificar Prueba"
        val updatedOrderStatus = orderStatusRepository.save(existingOrderStatus)
        Assertions.assertNotNull(updatedOrderStatus.id)
        Assertions.assertEquals("Modificar Prueba", updatedOrderStatus.name)
    }

    /**
     * Se verifica que se pueda eliminar un estado de orden
     */
    @Test
    fun testDeleteOrderStatus() { //ELIMINAR UN ESTADO DE ORDEN
        var orderStatus = OrderStatus(name = "Estado eliminar"); //CREAR UN NUEVO REGISTRO PARA ELIMINAR
        orderStatus = orderStatusRepository.save(orderStatus)
        val existingOrderStatus = orderStatusRepository.findById(orderStatus.id!!).get() //VALIDAR QUE EXISTE
        orderStatusRepository.delete(existingOrderStatus)
        val deletedOrderStatus = orderStatusRepository.findById(orderStatus.id!!) //VALIDAR QUE YA NO EXISTE
        Assertions.assertTrue(deletedOrderStatus.isEmpty)
    }
    /***
     * Se verifica que se hayan cargado todos los privilegios
     */
    @Test
    fun testPrivilegesFindAll() {
        val elementList: List<Privilege> = privilegeRepository.findAll()
        Assertions.assertTrue(elementList.size == 2)
    }

    /***
     * Se verifica que se pueda buscar un privilegio por ID.
     */
    @Test
    fun testPrivilegesFindById(){
        val id : Long = 1;
        val privilege = privilegeRepository.findById(id);
        Assertions.assertTrue(privilege.isPresent && privilege.get().id == id);
    }

    /***
     * Se verifica que se pueda eliminar un privilegio.
     */
    @Test
    fun deletePrivilege() {
        var privilege = Privilege(name = "Privilegio de prueba", userList = emptySet(), roleList = emptySet());
        privilege = privilegeRepository.save(privilege);
        privilegeRepository.deleteById(privilege.id!!);
        Assertions.assertTrue(privilegeRepository.findById(privilege.id!!).isEmpty)
    }

    /***
     * Se verifica que se pueda crear un privilegio.
     */
    @Test
    fun createPrivilege() {
        var privilege = Privilege(name = "Privilegio de prueba", userList = emptySet(), roleList = emptySet());
        privilege = privilegeRepository.save(privilege);
        Assertions.assertTrue(privilegeRepository.findById(privilege.id!!).isPresent);
    }

    /***
     * Se verifica que se pueda actualizar un privilegio.
     */
    @Test
    fun updatePrivilege() {
        val id: Long = 1;
        val privilege = privilegeRepository.findById(id);
        privilege.get().name = "Privilegio de prueba 1";
        privilegeRepository.save(privilege.get());
        Assertions.assertTrue(privilegeRepository.findById(id).get().name == "Privilegio de prueba 1");
    }
}