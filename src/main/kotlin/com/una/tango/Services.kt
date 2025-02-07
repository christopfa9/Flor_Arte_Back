package com.una.tango

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.transaction.Transactional
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.PathVariable
import java.io.IOException
import java.util.*
import javax.annotation.PostConstruct

interface UserService{
    fun signup(dto:UserSignUpInput):UserResult
    fun update(dto:UserUpdateInput):UserResult
    fun update(dto: UserUpdateInput,id: Long): UserResult
    fun delete(id:Long):Boolean
    fun findById(id:Long):UserResult?
    fun findAll():List<UserResult>?
    fun getMyself():UserResult?

    fun getCurrentUser(): User
}
@Transactional
@Service
class AbstractUserService (
    @Autowired
    val passwordEncoder: PasswordEncoder,
    @Autowired
    val userRepository: UserRepository,
    @Autowired
    val userMapper: UserMapper,
    @Autowired
    val roleRepository: RoleRepository,
    @Autowired
    val roleMapper: RoleMapper

): UserService{

    override fun signup(dto: UserSignUpInput): UserResult {
        var user: User = userMapper.userSignUpInputToUser(dto)
        user.password = passwordEncoder.encode(user.password)
        // Asignar rol de usuario por defecto.
        user.roleList = setOf(
            roleRepository.findByName("ROLE_USER").get()
        )
        user = userRepository.save(user)
        return userMapper.userToUserResult(user)
    }
    @Throws(NotFoundException::class)
    override fun update(dto: UserUpdateInput): UserResult {

        // Obtener usuario.
        var userToUpdate = getCurrentUser()

        // 1. Actualizar y guardar.
        userToUpdate.firstName = dto.firstName
        userToUpdate.lastName = dto.lastName
        userToUpdate.email = dto.email
        userToUpdate.address = dto.address

        // Cambiar contraseña solo si se pasa contraseña nueva.
        if(!dto.password.isNullOrEmpty())
            userToUpdate.password = passwordEncoder.encode(dto.password)

        // 2. Almacenar en DB.
        userToUpdate = userRepository.save(userToUpdate)
        // 3. Devolver respuesta.
        return userMapper.userToUserResult(userToUpdate)
    }

    @Throws(NotFoundException::class, RuntimeException::class)
    override fun update(dto: UserUpdateInput,id: Long): UserResult {

        // 1. Comprobar que usuario haciendo cambio tenga roles.
        // TODO: Reemplazar esta sección, por incluir roles en JWT y utilizar @PreAuthorize para hacer el check.
        val user = getCurrentUser()
        val isAuthorized = user.roleList!!.any{ it in listOf(
            Role(1,"ROLE_ADMIN", setOf()),
            Role(2,"ROLE_STAFF", setOf())
        )}

        if(!isAuthorized)
            throw RuntimeException("User is not authorized to update other users.")

        // 3. Buscar usuario.
        var userToUpdate = userRepository.findById(id).orElseThrow{NoSuchElementException("User $id doesn't exist.")}

        // 4. Actualizar y guardar.
        userToUpdate.firstName = dto.firstName
        userToUpdate.lastName = dto.lastName
        userToUpdate.enabled = dto.enabled ?: userToUpdate.enabled
        userToUpdate.email = dto.email
        userToUpdate.address = dto.address

        // Cambiar contraseña solo si se pasa contraseña nueva.
        if(!dto.password.isNullOrEmpty())
            userToUpdate.password = passwordEncoder.encode(dto.password)

        // Cambiar roles solo si se pasa lista de roles.
        if(!dto.roles.isNullOrEmpty()) {
            // Mapeo no funciona por defecto no funciona por diferencia en versiones de Java.
            // TODO: Arreglar mapeo.
            // userToUpdate.roleList = roleMapper.roleInputListToRoleSet(dto.roles!!.toList())
            val newRoles = mutableSetOf<Role>()
            dto.roles!!.forEach { role -> newRoles.add(Role(id=role.id, name="", privilegeList = setOf())) }
            userToUpdate.roleList = newRoles;
        }

        // 2. Cambiar información y almacenar en DB.
        userToUpdate = userRepository.save(userToUpdate)
        // 3. Devolver respuesta.
        return userMapper.userToUserResult(userToUpdate)
    }

    /**
     * Delete a User by id
     * @param id of the User to delete
     * @return true if successfully deleted, false otherwise
     */
    @Throws(NoSuchElementException::class)
    override fun delete(id: Long): Boolean {
        userRepository.findById(id)
            .orElseThrow { NoSuchElementException(String.format("User with id: %s not found!", id)) }

        userRepository.deleteById(id)
        return true
    }

    /**
     * Find a User by id
     * @param id of the User
     * @return the found UserResult
     */
    @Throws(NoSuchElementException::class)
    override fun findById(id: Long): UserResult {
        val user = userRepository.findById(id)
            .orElseThrow { NoSuchElementException(String.format("User with id: %s not found!", id)) }

        return userMapper.userToUserResult(user)
    }

    /**
     * Find all Users
     * @return a list of UserResults
     */
    override fun findAll(): List<UserResult> {
        val users = userRepository.findAll()
        return userMapper.userListToUserResultList(users)
    }

    override fun getMyself(): UserResult?{
        return userMapper.userToUserResult(this.getCurrentUser())
    }

    @Throws(NoSuchElementException::class)
    override fun getCurrentUser(): User{
        // 1. Obtener nombre de usuario desde el contexto de seguridad.
        // IMPORTANTE: Cambiar de correo, requiere volver a iniciar sesión.
        // Ya que el JWT contiene el correo anterior como 'claim'.
        // y al cambiar el correo en la base de datos, se van a producir no  resultados.
        val username = SecurityContextHolder.getContext().authentication.name
        return userRepository.findByEmail(username).orElseThrow{NoSuchElementException("User $username doesn't exist.")}
    }
}

interface ProductService{
    fun create(productInput:ProductInput):ProductResult
    fun update(productInput:ProductInput, id: Long):ProductResult
    fun delete(id:Long)
    fun findById(id:Long):ProductResult
    fun find(q:String?, category:Long?, tags:List<Long>?): List<ProductResult>
    fun allProductsToJSON(): String
}
@Service
class AbstractProductService(
    @Autowired
    val productRepository: ProductRepository,
    @Autowired
    val productMapper: ProductMapper
) : ProductService {

    /**
     * Create a new Product
     * @param productInput input data
     * @return the created ProductResult
     */
    @Transactional
    override fun create(productInput: ProductInput): ProductResult {
        val productEntity = productMapper.productInputToProduct(productInput)
        val savedProduct = productRepository.save(productEntity)
        return productMapper.productToProductResult(savedProduct)
    }

    /**
     * Update an existing Product
     * @param productInput input data
     * @return the updated ProductResult
     */
    @Transactional
    @Throws(NoSuchElementException::class)
    override fun update(productInput: ProductInput, @PathVariable id : Long): ProductResult {
        val product: Product = productRepository.findById(id)
            .orElseThrow { NoSuchElementException(String.format("Product with id: %s not found!", id)) }

        val updatedProduct: Product = productMapper.productInputToProduct(productInput)
        product.name = updatedProduct.name
        product.description = updatedProduct.description
        product.price = updatedProduct.price
        product.quantity = updatedProduct.quantity
        product.category = updatedProduct.category
        product.tags = updatedProduct.tags
        return productMapper.productToProductResult(productRepository.save(product))
    }

    /**
     * Delete a Product by id
     * @param id of the Product to delete
     * @return true if successfully deleted, false otherwise
     */
    @Transactional
    @Throws(NoSuchElementException::class)
    override fun delete(id: Long){
        val product = productRepository.findById(id)
            .orElseThrow { NoSuchElementException(String.format("Product with id: %s not found!", id)) }

        productRepository.delete(product)
    }

    /**
     * Find a Product by id
     * @param id of the Product
     * @return the found ProductResult
     */
    @Throws(NoSuchElementException::class)
    override fun findById(id: Long): ProductResult {
        val product = productRepository.findById(id)
            .orElseThrow { NoSuchElementException(String.format("Product with id: %s not found!", id)) }
        return productMapper.productToProductResult(product)
    }

    override fun find(q:String?, category:Long?, tags:List<Long>?): List<ProductResult> {
        // Convertir lista de IDs de etiquetas a un string con el siguiente formato: ({elementos})
        val tagList = StringBuilder()
        if(!tags.isNullOrEmpty()) {
            tagList.append("{")
            for (tag: Long in tags) {
                tagList.append("$tag,")
            }
            // Reemplazar última coma y cerrar el arreglo.
            tagList.setCharAt(tagList.length-1,'}')
        }
        // Realizar búsqueda y devolver resultado.
        val products = productRepository.search(q,
            category,
            if(!tags.isNullOrEmpty()) tagList.toString() else null)

        return productMapper.productListToProductResultList(products)
    }
    override fun allProductsToJSON(): String{
        val jsonMapper = jacksonObjectMapper()
        val products = productMapper.productListToProductResultList( productRepository.findAll())
        return jsonMapper.writeValueAsString(products)
    }
}

interface TagService{
    fun create(tagInput:TagInput):TagResult
    fun update(tagInput:TagInput,id: Long):TagResult
    fun delete(id:Long)
    fun findById(id:Long):TagResult
    fun find(q:String?):List<TagResult>
}
@Service
class AbstractTagService(
    @Autowired
    val tagRepository: TagRepository,

    @Autowired
    val tagMapper: TagMapper
) : TagService {

    /**
     * Create a new Tag
     * @param tagInput input data
     * @return the created TagResult
     */
    override fun create(tagInput: TagInput): TagResult {
        val tagEntity = tagMapper.tagInputToTag(tagInput)
        val savedTag = tagRepository.save(tagEntity)
        return tagMapper.tagToTagResult(savedTag)
    }

    /**
     * Update an existing Tag
     * @param tagInput input data
     * @return the updated TagResult
     */
    @Throws(NoSuchElementException::class)
    override fun update(tagInput: TagInput, id: Long): TagResult {
        val tag: Tag = tagRepository.findById(id)
            .orElseThrow { NoSuchElementException("Tag with id: ${tagInput.id} not found!") }
        tag.name = tagInput.name ?: tag.name
        return tagMapper.tagToTagResult(tagRepository.save(tag))
    }

    /**
     * Delete a Tag by id
     * @param id of the Tag to delete
     * @return true if successfully deleted, false otherwise
     */
    @Throws(NoSuchElementException::class)
    override fun delete(id: Long) {
        val tag = tagRepository.findById(id).orElse(null)
            ?: throw NoSuchElementException("Tag with id: $id not found!")

        tagRepository.delete(tag)
    }

    /**
     * Find a Tag by id
     * @param id of the Tag
     * @return the found TagResult
     */
    @Throws(NoSuchElementException::class)
    override fun findById(id: Long): TagResult {
        val tag = tagRepository.findById(id).orElse(null)
            ?: throw NoSuchElementException("Tag with id: $id not found!")

        return tagMapper.tagToTagResult(tag)
    }

    /**
     * Find all Tags
     * @return a list of TagResults
     */
    override fun find(q: String?): List<TagResult> {
        var tags: List<Tag> = listOf()
        if(q.isNullOrEmpty())
            tags = tagRepository.findAll()
        else
            tags = tagRepository.findByNameContainsIgnoreCase(q)
        return tagMapper.tagListToTagResultList(tags)
    }
}

@Service
@Transactional
class AppUserDetailsService(
    @Autowired
    val userRepository: UserRepository,
    @Autowired
    val roleRepository: RoleRepository,
) : UserDetailsService {

    /**
     * Locates the user based on the username. In the actual implementation, the search
     * may be case sensitive, or case insensitive depending on how the
     * implementation instance is configured. In this case, the `UserDetails`
     * object that comes back may have a username that is of a different case than what
     * was actually requested..
     * @param username the username identifying the user whose data is required.
     * @return a fully populated user record (never `null`)
     * @throws UsernameNotFoundException if the user could not be found or the user has no
     * GrantedAuthority
     */
    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {

        val userAuth: org.springframework.security.core.userdetails.User

        val user: User = userRepository.findByEmail(username).orElse(null)
            ?: return org.springframework.security.core.userdetails.User(
                "", "", true, true, true, true,
                getAuthorities(
                    listOf(
                        roleRepository.findByName("ROLE_USER").get()
                    )
                )
            )

        userAuth = org.springframework.security.core.userdetails.User(
            user.email, user.password, user.enabled, true, true,
            true, getAuthorities(user.roleList!!.toMutableList())
        )

        return userAuth
    }

    private fun getAuthorities(roles: Collection<Role>): Collection<GrantedAuthority> {
        return roles.flatMap { role ->
            sequenceOf(SimpleGrantedAuthority(role.name)) +
                    role.privilegeList.map { privilege -> SimpleGrantedAuthority(privilege.name) }
        }.toList()
    }
}

interface CategoryService{
    fun create(categoryInput:CategoryInput):CategoryResult?
    fun update(categoryInput:CategoryInput, id:Long):CategoryResult?
    fun deleteById(id:Long):Boolean
    fun findById(id:Long):CategoryResult?
    fun findAll():List<CategoryResult>?
}

@Service
class AbstractCategoryService(
    @Autowired
    val categoryRepository: CategoryRepository,
    @Autowired
    val categoryMapper: CategoryMapper
) : CategoryService {

    /**
     * Create a new Category
     * @param categoryInput input data
     * @return the created CategoryResult
     */
    override fun create(categoryInput: CategoryInput): CategoryResult? {
        val categoryEntity = categoryMapper.categoryInputToCategory(categoryInput)
        val savedCategory = categoryRepository.save(categoryEntity)
        return categoryMapper.categoryToCategoryResult(savedCategory)
    }

    /**
     * Update an existing Category
     * @param categoryInput input data
     * @return the updated CategoryResult
     */
    @Throws(NoSuchElementException::class)
    override fun update(categoryInput: CategoryInput, id: Long): CategoryResult? {
        val category = categoryRepository.findById(id).orElseThrow{NotFoundException()}

        val updatedCategory: Category = categoryMapper.categoryInputToCategory(categoryInput)
        category.name = updatedCategory.name
        return categoryMapper.categoryToCategoryResult(categoryRepository.save(category))
    }


    /**
     * Delete a Category by id
     * @param id of the Category to delete
     * @return true if successfully deleted, false otherwise
     */
    @Throws(NoSuchElementException::class)
    override fun deleteById(id: Long): Boolean {
        categoryRepository.findById(id).orElse(null)
            ?: throw NoSuchElementException(String.format("Category with id: %s not found!", id))

        categoryRepository.deleteById(id)
        return true
    }

    /**
     * Find a Category by id
     * @param id of the Category
     * @return the found CategoryResult
     */
    @Throws(NoSuchElementException::class)
    override fun findById(id: Long): CategoryResult? {
        val category = categoryRepository.findById(id).orElse(null)
            ?: throw NoSuchElementException(String.format("Category with id: %s not found!", id))

        return categoryMapper.categoryToCategoryResult(category)
    }

    /**
     * Find all Categories
     * @return a list of CategoryResults
     */
    override fun findAll(): List<CategoryResult>? {
        val categories = categoryRepository.findAll()
        return categoryMapper.categoryListToCategoryResultList(categories)
    }
}

interface OrderService{
    fun create(orderInput:OrderInput):OrderResult?
    fun updateStatus(orderInput:OrderInput, id:Long):OrderResult?
    fun findById(id:Long):OrderResult?
    fun findAll():List<OrderResult>?
    fun findByUser(id: Long?):List<OrderResult>
}
@Service
class AbstractOrderService(
    @Autowired
    private val orderRepository: OrderRepository,
    @Autowired
    private val userService: UserService,
    @Autowired
    private val orderMapper: OrderMapper,
    @Autowired
    private val userRepository: UserRepository,
    @Autowired
    private val productRepository: ProductRepository,
    @Autowired
    private val orderStatusRepository: OrderStatusRepository,

    ) : OrderService {

    /**
     * Create a new Order
     * @param orderInput input data
     * @return the created OrderResult
     */
    @Throws(NoSuchElementException::class, RuntimeException::class)
    override fun create(orderInput: OrderInput): OrderResult? {
        // Mapea la entrada a la entidad Order
        val orderEntity = orderMapper.orderInputToOrder(orderInput)

        // Asignar cliente
        orderEntity.client = userService.getCurrentUser()

        if(orderInput.orderDetails!!.isEmpty())
            throw RuntimeException("Can't create empty order.")

        // Mapea y asigna detalles de la orden
        orderEntity.orderDetails = orderInput.orderDetails!!.map{
            detail ->
                val product = productRepository.findById(detail.product!!.id!!).orElseThrow { NoSuchElementException() }
                OrderDetail(
                    product = product,
                    quantity = detail.quantity,
                    price = product.price,
                    total = product.price!!.times(detail.quantity!!),
                    order = orderEntity
                )
        }

        // Calcular total para orden.
        orderEntity.total  = orderEntity.orderDetails!!
            .map{detail->detail.total}
            .reduce { total, element -> total!!.plus(element!!) }

        // Asignar estado a la orden de 'pendiente'
        orderEntity.orderStatus = orderStatusRepository.findById(1).orElseThrow{NoSuchElementException()}

        // Guarda la orden
        val savedOrder = orderRepository.save(orderEntity)

        // Devuelve el resultado mapeado
        return orderMapper.orderToOrderResult(savedOrder)
    }

    /**
     * Update an existing Order
     * @param orderInput input data
     * @return the updated OrderResult
     */
    @Throws(NoSuchElementException::class)
    override fun updateStatus(orderInput: OrderInput, id:Long): OrderResult? {
        val order = orderRepository.findById(id).orElseThrow{NotFoundException()}

        val updatedOrder: Order = orderMapper.orderInputToOrder(orderInput)
        order.orderStatus = updatedOrder.orderStatus
        return orderMapper.orderToOrderResult(orderRepository.save(order))
    }



    /**
     * Find a Order by id
     * @param id of the Order
     * @return the found OrderResult
     */
    @Throws(NoSuchElementException::class)
    override fun findById(id: Long): OrderResult? {
        val order = orderRepository.findById(id).orElse(null)
            ?: throw NoSuchElementException(String.format("Order not found with id: $id"))
        return orderMapper.orderToOrderResult(order)
    }

    /**
     * Find all Orders
     * @return a list of OrderResults
     */
    override fun findAll(): List<OrderResult>? {
        // Devuelve todas las ordenes ordenadas por fecha de creación (últimas) y fecha de entrega (primeras)
        val orders = orderRepository.findAllByOrderByDateDescDeliveryDateAsc()
        return orderMapper.orderListToOrderResultList(orders)
    }

    /**
     * Find orders by the ID of the user who created the order.
     */
    @Throws(NoSuchElementException::class)
    override fun findByUser(id: Long?): List<OrderResult> {
        // Si se pasa ID para buscar, se buscan ordenes del usuario con ese ID
        // De lo contrario, se buscan las ordenes del usuario que llamó la funcion
        val client =
            if (id == null) userService.getCurrentUser()
            else userRepository.findById(id).orElseThrow { NoSuchElementException()}

        // Devuelve todas las ordenes de un clientes ordenadas por fecha de creación (últimas)
        // y fecha de entrega (primeras)
        val orders = orderRepository.findByClientOrderByDateDescDeliveryDateAsc(client)
        return orderMapper.orderListToOrderResultList(orders)
    }
}

interface RoleService{
    fun findAll():Set<RoleDetails>
}
@Transactional
@Service
class AbstractRoleService (
    @Autowired
    val roleRepository: RoleRepository,
    @Autowired
    val roleMapper: RoleMapper

): RoleService{
    override fun findAll(): Set<RoleDetails> {
        return roleMapper.roleListToRoleDetailsList(this.roleRepository.findAll().toSet())
    }
}

interface IAServiceStrategy {
    fun getResponse(prompt: String): String?
}

// Implementación del servicio para OpenAI
@Service
class OpenAIService (@Autowired val productService : ProductService) : IAServiceStrategy {

    private val client: OkHttpClient = OkHttpClient()

    @Value("\${openai.api.key}")
    lateinit var apiKey: String

    override fun getResponse(prompt: String): String? {
        val json = JSONObject()
        json.put("model", "gpt-4")

        val productsJSON = productService.allProductsToJSON()
        val productsInfo = "Aquí tienes la lista de productos como referencia: $productsJSON\n\n"
        //  Ignora preguntas o temas no relacionados con los productos de la lista.

        val instructions = """
        Responde solo en español. Basa tus respuestas únicamente en la lista de productos proporcionada.
        Si el mensaje contiene palabras como 'comprar', 'nueva orden', 'ordenar' o cualquier frase relacionada con la compra, 
        responde exclusivamente con un JSON en el siguiente formato:    
        {
            "productos": [
                {
                    "id": "ID del producto",
                    "cantidad": Número de unidades solicitadas (usar 1 si no se especifica)
                },
                ...
            ]
        }    
        Si la pregunta busca el precio de un producto específico, responde con el precio precedido por el símbolo "$" (por ejemplo, "$50.00").    
        Si la pregunta busca una recomendación, responde sugiriendo productos de la lista que puedan ser adecuados para la ocasión o situación indicada.    
        Si no puedes responder utilizando únicamente la lista de productos o no tienes una respuesta valida, responde con "Lo siento, solo puedo responder sobre los productos listados."
        """.trimIndent()


        val fullPrompt = "$instructions\n$productsInfo$prompt"

        // La API de chat usa "messages" en lugar de "prompt"
        val messages = JSONArray().apply {
            put(JSONObject().put("role", "user").put("content", fullPrompt))
        }
        json.put("messages", messages)
        json.put("max_tokens", 300)

        val mediaType = "application/json".toMediaTypeOrNull()
        val body = json.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")  // Nuevo endpoint
            .post(body)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            response.body?.let {
                val responseBody = it.string()
                val jsonResponse = JSONObject(responseBody)
                val content = jsonResponse.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
                    .trim()

                val jsonResponseFormatted = JSONObject()
                jsonResponseFormatted.put("answer", content)

                return jsonResponseFormatted.toString()
            }
        }

        return null
    }

}

// Implementación simulada para Google Gemini
@Service
class GoogleGeminiService : IAServiceStrategy {

    override fun getResponse(prompt: String): String? {
        return "Respuesta simulada de Google Gemini para el prompt: $prompt"
    }
}

// Implementación simulada para Claude
@Service
class ClaudeService : IAServiceStrategy {

    override fun getResponse(prompt: String): String? {
        return "Respuesta simulada de Claude para el prompt: $prompt"
    }
}

// Selector de estrategia para elegir el proveedor de IA adecuado
@Service
class IAServiceSelector(
    private val strategies: List<IAServiceStrategy> // Inyecta todas las estrategias disponibles
) {
    private val strategyMap: MutableMap<String, IAServiceStrategy> = mutableMapOf()

    @PostConstruct
    fun initializeStrategyMap() {
        // Mapea cada estrategia usando el nombre de la clase en minúsculas como clave
        strategies.forEach { strategy ->
            strategyMap[strategy::class.simpleName!!.lowercase()] = strategy
        }
    }

    fun getResponse(provider: String, prompt: String): String {
        // Selecciona la estrategia basada en el nombre del proveedor (como "openaiservice")
        val strategy = strategyMap[provider.lowercase()] ?: throw IllegalArgumentException("Proveedor no soportado: $provider")
        return strategy.getResponse(prompt) ?: "Sin respuesta del proveedor"
    }
}