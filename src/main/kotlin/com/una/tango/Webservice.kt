package com.una.tango

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("\${url.user}")
class UserController(private val userService: UserService, private val orderService: OrderService) {

    /**
     * WS to signup new users.
     * @return User object.
     */
    @PostMapping(path =["/signup"], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun signup(@RequestBody user:UserSignUpInput) = userService.signup(user)

    /**
     * WS to update existent users.
     * @return User object.
     */
    @PutMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun update(@RequestBody user:UserUpdateInput) = userService.update(user)

    /**
     * WS for admin or staff to update any existent users.
     * @return User object.
     */
    @PutMapping(path=["/{id}"],consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    // @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_STAFF')") // Requiere incluir rol(es) in JWT.
    fun updateAnyUser(@RequestBody dto:UserUpdateInput, @PathVariable id: Long) = userService.update(dto, id)

    /**
     * WS to delete any existent users.
     * @return User object.
     */
    @DeleteMapping(path=["/{id}"])
    @ResponseBody
    fun delete(@PathVariable id: Long) = userService.delete(id)

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun findAll() = userService.findAll()

    @GetMapping(path = ["/me"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun getMyself() = userService.getMyself()

    @GetMapping("/{userId}/order")
    @ResponseBody
    fun findByOrdersByUser(@PathVariable(required = false) userId: Long) = orderService.findByUser(userId)

    @GetMapping("/orders")
    @ResponseBody
    fun findByOrdersByUser() = orderService.findByUser(null)
}

@RestController
@RequestMapping("\${url.products}")
class ProductController(private val productService: ProductService) {

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun create(@RequestBody dto:ProductInput) = productService.create(dto)

    @PutMapping(path=["/{id}"],consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun update(@RequestBody dto:ProductInput, @PathVariable id: Long) = productService.update(dto, id)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = productService.delete(id)

}

@RestController
@RequestMapping("\${url.tags}")
class TagController(private val tagService: TagService) {

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun create(@RequestBody dto:TagInput) = tagService.create(dto)

    @PutMapping(path=["/{id}"],consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun update(@RequestBody dto:TagInput, @PathVariable id: Long) = tagService.update(dto, id)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = tagService.delete(id)

}

@RestController
@RequestMapping("\${url.unsecure.products}")
class UnsecureProductsController(
    private val productService: ProductService){

    @GetMapping(path=["/{id}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun getById(@PathVariable id: Long) = productService.findById(id)

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun find(@RequestParam(required = false) name:String?,
             @RequestParam(required = false) category:Long?,
             @RequestParam(required = false) tags :List<Long>?) = productService.find(name,category,tags)
}

@RestController
@RequestMapping("\${url.unsecure.tags}")
class UnsecureTagController(private val tagService: TagService){

    @GetMapping(path=["/{id}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun getById(@PathVariable id: Long) = tagService.findById(id)

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun find(@RequestParam(required = false) q: String?) = tagService.find(q)
}

@RestController
@RequestMapping("\${url.categories}")
class CategoryController(private val categoryService: CategoryService) {

    /**
     * WS to create new category.
     * @return Category object.
     */
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
        fun create(@RequestBody categoryInput:CategoryInput) : CategoryResult? {
        return categoryService.create(categoryInput)
    }

    /**
     * WS to update existent category.
     * @return Category object.
     */
    @PutMapping(path =["/{id}"], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun update(@RequestBody categoryInput:CategoryInput, @PathVariable id: Long) : CategoryResult? {
        return categoryService.update(categoryInput, id)
    }

    @Throws(NoSuchElementException::class)
    @DeleteMapping("{id}")
    @ResponseBody
    fun deleteById(@PathVariable id: Long) {
        categoryService.deleteById(id)
    }
}

@RestController
@RequestMapping("\${url.unsecure.categories}")
class UnsecureCategoryController(private val categoryService: CategoryService){

    @GetMapping(path=["/{id}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun getById(@PathVariable id: Long) = categoryService.findById(id)

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun findAll() = categoryService.findAll()
}

@RestController
@RequestMapping("\${url.order}")
class OrderController(private val orderService: OrderService) {

    /**
     * WS to create new order.
     * @return Order object.
     */
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun create(@RequestBody orderInput:OrderInput) : OrderResult? {
        return orderService.create(orderInput)
    }

    @GetMapping
    @ResponseBody
    fun findAll() = orderService.findAll()

    @Throws(NoSuchElementException::class)
    @GetMapping("{id}")
    @ResponseBody
    fun findById(@PathVariable id: Long) = orderService.findById(id)

    /**
     * WS to update existent order.
     * @return Order object.
     */
    @PutMapping(path =["/{id}"], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun update(@RequestBody orderInput:OrderInput, @PathVariable id: Long) : OrderResult? {
        return orderService.updateStatus(orderInput, id)
    }
}
@RestController
@RequestMapping("\${url.roles}")
class RolesController(private val roleService: RoleService) {

    @GetMapping
    @ResponseBody
    fun findAll() = roleService.findAll()
}

@RestController
@RequestMapping("\${url.unsecure.ia}")
class IAController(private val iaServiceSelector: IAServiceSelector) {

    @GetMapping("/ask")
    fun askIA(@RequestParam provider: String, @RequestParam question: String): String {
        return iaServiceSelector.getResponse(provider, question)
    }
}